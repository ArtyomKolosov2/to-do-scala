package modules

import cats.effect.kernel.{ Resource, Sync }
import services.ToDoService
import cats.effect.syntax.all.*

final case class ServiceModule[F[_]](
    toDoService: ToDoService[F]
)

object ServiceModule:
  def make[F[_]: Sync]: Resource[F, ServiceModule[F]] =
    for
      toDoService <- ToDoService.inMemory[F].toResource
    yield ServiceModule(toDoService)
