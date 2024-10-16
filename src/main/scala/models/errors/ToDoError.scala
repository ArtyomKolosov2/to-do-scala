package models.errors

import io.circe.Encoder

sealed trait ToDoError extends Throwable:
  def message: String

  override def getMessage: String = message
  
object ToDoError:
  implicit def encoder[A <: ToDoError]: Encoder[A] = Encoder.forProduct1("message")(_.getMessage())
  
  case class ToDoNotFoundError(id: String) extends ToDoError:
    override def message: String = s"Can't find ToDo with ID: $id"