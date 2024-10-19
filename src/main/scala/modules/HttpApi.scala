package modules

import cats.effect.kernel
import cats.effect.kernel.Async
import http.ToDoRoutes
import org.http4s.*
import org.http4s.server.*
import org.http4s.server.middleware.{ErrorHandling, Logger}
import org.http4s.syntax.all.*
import org.typelevel.log4cats.LoggerFactory

final case class HttpApi[F[_]: Async: LoggerFactory](
    services: ServiceModule[F]
):
  private val toDoRoutes = ToDoRoutes[F](services.toDoService)

  private val routes = Router(
    "api/todo" -> toDoRoutes.routes
  )

  val httpApp: HttpApp[F] =
    ErrorHandling.httpRoutes[F](
      Logger.httpRoutes[F](
        logHeaders = true,
        logBody = true
      )(routes)
    ).orNotFound