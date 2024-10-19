package modules

import cats.effect.kernel.{Async, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import com.comcast.ip4s.*
import fs2.io.net.Network
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.{Logger, LoggerFactory}

object Ember:
  private def make[F[_]: Async](httpApp: HttpApp[F]) =
    given  LoggerFactory[F] = Slf4jFactory.create[F]

    EmberServerBuilder.default[F]
      .withHost(Host.fromString("0.0.0.0").get)
      .withPort(Port.fromInt(5000).get)
      .withHttpApp(httpApp)
  
  def default[F[_] : Async : Console](httpApp: HttpApp[F]): Resource[F, Server] =
    make(httpApp).build