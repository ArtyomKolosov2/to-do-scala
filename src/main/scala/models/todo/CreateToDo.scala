package models.todo

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class CreateToDo(
    description: String
)

object CreateToDo:
  given Decoder[CreateToDo] = deriveDecoder[CreateToDo]
  given Encoder[CreateToDo] = deriveEncoder[CreateToDo]