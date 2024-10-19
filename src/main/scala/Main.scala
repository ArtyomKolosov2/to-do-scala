import cats.effect.{IO, IOApp}
import modules.{Ember, HttpApi, ServiceModule}
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Main extends IOApp.Simple: // IOApp contains main inside

  given LoggerFactory[IO] = Slf4jFactory.create[IO]

  override def run: IO[Unit] =
    (for
      services <- ServiceModule.make[IO]
      httpApp  = HttpApi[IO](services).httpApp
      server   <- Ember.default[IO](httpApp)
    yield server).useForever
