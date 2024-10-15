package http

import cats.effect.*
import models.todo.CreateToDo
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.{->, /, GET, Ok, Root}
import services.ToDoService

class ToDoRoutes(service: ToDoService[IO]) extends Http4sDsl[IO]:
  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / id =>
      for
        toDoOption <- service.getToDo(id)
        resp <- if toDoOption.isDefined then
          Ok(toDoOption.get)
        else
          NotFound(s"Couldn't find ToDo with id: $id")
      yield resp
    case req @ POST -> Root / "create" =>
      for
        createToDo <- req.as[CreateToDo]
        _ <- service.addToDo(createToDo)
        resp <- Created(createToDo)
      yield resp
  }

  final val routes = httpRoutes

object ToDoRoutes:
  def apply(service: ToDoService[IO]): ToDoRoutes = new ToDoRoutes(service)