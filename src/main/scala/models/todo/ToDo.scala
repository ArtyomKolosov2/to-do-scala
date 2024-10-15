package models.todo

import cats.{Functor, Monad}
import cats.effect.IO
import cats.effect.kernel.{Clock, Sync}
import cats.syntax.all.*
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import models.todo.Status.NOT_STARTED

import java.time.Instant

final case class ToDo(
    id: String,
    description: String,
    status: Status,
    createdAt: Instant
)

object ToDo:
  given Encoder[ToDo] = deriveEncoder[ToDo]
  def fromCreateToDo[F[_]: Functor: Clock: Monad](request: CreateToDo, idGenerator: F[String]): F[ToDo] =
    for
      id <- idGenerator
      instant <- Clock[F].realTimeInstant
    yield ToDo(id, request.description, NOT_STARTED, instant)
