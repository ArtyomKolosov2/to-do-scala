package http

import cats.effect.*
import models.todo.{CreateToDo, UpdateToDo}
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*
import services.ToDoService
import cats.syntax.all.*
import cats.data.*
import io.circe.syntax.EncoderOps
import models.errors.ToDoError.*
import models.errors.ToDoError.encoder

class ToDoRoutes[F[_]: Concurrent](service: ToDoService[F]) extends Http4sDsl[F]:
  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "get" / id =>
      for
        toDoOption <- service.getToDo(id)
        response <- toDoOption match
          case Some(toDo) =>
            Ok(toDo)
          case None =>
            NotFound(s"Couldn't find ToDo with id: $id")
      yield response
    case req @ POST -> Root / "create" =>
      for
        createToDo <- req.as[CreateToDo]
        _          <- service.addToDo(createToDo)
        response   <- Created(createToDo)
      yield response
    case DELETE -> Root / "delete" / id =>
      for
        _        <- service.removeToDo(id)
        response <- Ok(s"ToDo with id: $id deleted")
      yield response
    case req @ PUT -> Root / "update" / id =>
      for
        updateToDo <- req.as[UpdateToDo]
        _          <- service.updateToDo(id, updateToDo)
        response   <- Ok()
      yield response
  }

  final val routes = httpRoutes.recoverWith {
    case err : ToDoNotFoundError => Kleisli { _ =>
      OptionT.liftF(NotFound(err.asJson))
    }
  }

object ToDoRoutes:
  def apply[F[_]: Concurrent](service: ToDoService[F]): ToDoRoutes[F] = new ToDoRoutes(service)
