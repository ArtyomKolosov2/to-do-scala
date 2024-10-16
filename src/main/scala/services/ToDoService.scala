package services

import cats.ApplicativeThrow
import cats.effect.Sync
import cats.effect.kernel.Ref
import cats.syntax.all.*
import models.errors.ToDoError.ToDoNotFoundError
import models.todo.{CreateToDo, ToDo, UpdateToDo}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

trait ToDoService[F[_]]:
  def getToDo(id: String): F[Option[ToDo]]
  def addToDo(request: CreateToDo): F[Unit]
  def removeToDo(id: String): F[Unit]
  def updateToDo(id: String, request: UpdateToDo): F[Unit]

class ToDoServiceInMemory[F[_]: Sync](entities: Ref[F, List[ToDo]])(
    logger: SelfAwareStructuredLogger[F]
) extends ToDoService[F]:
  override def getToDo(id: String): F[Option[ToDo]] =
    for
      toDoList <- entities.get
      toDo = toDoList.find(_.id == id)
    yield toDo

  override def addToDo(request: CreateToDo): F[Unit] =
    for
      toDo <- ToDo.fromCreateToDo(request, IdFactory.getNewId)
      _    <- entities.update(_.appended(toDo))
      _    <- logger.info(s"ToDo with ${toDo.id} ID was added to in-memory db")
    yield ()

  override def removeToDo(id: String): F[Unit] =
    for
      _ <- entities.update(_.filterNot(_.id == id))
      _ <- logger.info(s"ToDo with $id ID was removed from in-memory db")
    yield ()

  override def updateToDo(id: String, request: UpdateToDo): F[Unit] =
    for
      getOption <- getToDo(id)
      toDo <- getOption match
        case Some(toDo) =>
          Sync[F].pure { toDo }
        case None =>
          ApplicativeThrow[F].raiseError(ToDoNotFoundError(id))
      newToDo = createUpdatedToDo(toDo, request)
      _ <- entities.update(_.filterNot(_.id == id))
      _ <- entities.update(_.appended(newToDo))
      _ <- logger.info(s"ToDo with $id ID was updated in in-memory db")
    yield ()

  private def createUpdatedToDo(originalToDo: ToDo, updateRequest: UpdateToDo): ToDo =
      ToDo(originalToDo.id, updateRequest.description, updateRequest.status, originalToDo.createdAt)
    

object ToDoService:
  def inMemory[F[_]: Sync]: F[ToDoService[F]] =
    for
      state  <- Ref.of(List.empty[ToDo])
      logger <- Slf4jLogger.create[F]
    yield new ToDoServiceInMemory[F](state)(logger)

object IdFactory:
  private var counter: Int = 0

  def getNewId[F[_]: Sync]: F[String] =
    counter += 1
    Sync[F].delay(counter.toString)
