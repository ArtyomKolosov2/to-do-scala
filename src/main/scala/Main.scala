import cats.data.Kleisli
import cats.effect.*
import com.comcast.ip4s.{Host, Port}
import http.ToDoRoutes
import org.http4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Server
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object SimpleServer extends IOApp { // IOApp contains main inside
  given LoggerFactory[IO] = Slf4jFactory.create[IO]
  private val toDoRoutes = ToDoRoutes()
  
  val app: Kleisli[IO, Request[IO], Response[IO]] = Logger.httpRoutes[IO](
    logHeaders = true,
    logBody = true
  )(toDoRoutes.routes).orNotFound

  override def run(args: List[String]) : IO[ExitCode] =
    val server: Resource[IO, Server] = EmberServerBuilder.default[IO]
      .withHost(Host.fromString("0.0.0.0").get)
      .withPort(Port.fromInt(5000).get)
      .withHttpApp(app)
      .build

    server.useForever
    
    // ToDo add service with internal map which will act as mocked Database
}
