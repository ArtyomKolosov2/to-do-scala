package http

import cats.data.Kleisli
import cats.effect.*
import cats.syntax.all.*
import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io.{->, /, GET, Ok, Root}
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.circe.CirceEntityEncoder.*
import models.todo.CreateToDo

class ToDoRoutes extends Http4sDsl[IO]:
  private val httpRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "length" / str => Ok(str.length.toString)
    case req @ POST -> Root / "create" =>
      for
        createToDo <- req.as[CreateToDo]
        resp <- Created(createToDo)
      yield resp
  }

  final val routes = httpRoutes

object ToDoRoutes:
  def apply(): ToDoRoutes = new ToDoRoutes()