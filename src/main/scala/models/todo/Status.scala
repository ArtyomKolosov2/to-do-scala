package models.todo

import io.circe.{ Decoder, Encoder }

enum Status:
  case NOT_STARTED, IN_PROGRESS, DONE

object Status:
  given Encoder[Status] = Encoder.encodeString.contramap[Status](_.toString)

  given Decoder[Status] = Decoder.decodeString.emap {
    case "NOT_STARTED" => Right(Status.NOT_STARTED)
    case "IN_PROGRESS" => Right(Status.IN_PROGRESS)
    case "DONE"        => Right(Status.DONE)
  }
