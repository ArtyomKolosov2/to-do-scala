package models.todo

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class UpdateToDo(description: String, status: Status)

object UpdateToDo:
  given Decoder[UpdateToDo] = deriveDecoder[UpdateToDo]
