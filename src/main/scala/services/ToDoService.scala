package services

import cats.effect.{IO, Sync}
import cats.syntax.all.*
import cats.effect.kernel.Ref
import models.todo.{CreateToDo, ToDo}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import cats.Monad

trait ToDoService[F[_]]:
  def getToDo(id: String): F[Option[ToDo]]
  def addToDo(request: CreateToDo): F[Unit]
  def removeToDo(id: String): F[Unit]
  def updateToDo(id: String): F[Unit]

class ToDoServiceInMemory[F[_]: Sync](entities: Ref[F, List[ToDo]])(
  logger: SelfAwareStructuredLogger[F]
) extends ToDoService[F]:
  override def getToDo(id: String): F[Option[ToDo]] =
    for
      toDoList <- entities.get
      toDo = toDoList.find(x => x.id == id) // ToDo: If not found then GG, add proper handling of option (e.g. Exception or empty value or smt else)
    yield toDo

  override def addToDo(request: CreateToDo): F[Unit] =
    for
      toDo <- ToDo.fromCreateToDo(request, IdFactory.getNewId)
      _ <- entities.update(_.appended(toDo))
      _ <- logger.info(s"ToDo with ${toDo.id} ID was added to in-memory db")
    yield ()

  override def removeToDo(id: String): F[Unit] = ???

  override def updateToDo(id: String): F[Unit] = ???

object ToDoService:
  def inMemory[F[_]: Sync]: F[ToDoService[F]] =
    for
      state  <- Ref.of(List.empty[ToDo])
      logger <- Slf4jLogger.create[F]
    yield new ToDoServiceInMemory[F](state)(logger)

object IdFactory {
  private var counter : Int = 0

  def getNewId[F[_]: Sync]: F[String] =
    counter += 1
    Sync[F].delay(counter.toString)
}
