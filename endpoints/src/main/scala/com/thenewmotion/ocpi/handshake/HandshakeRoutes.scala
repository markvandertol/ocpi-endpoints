package com.thenewmotion.ocpi.handshake

import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes.GenericSuccess
import com.thenewmotion.ocpi.CurrentTimeComponent
import spray.routing.HttpService

import scalaz._

trait HandshakeRoutes extends HttpService with CurrentTimeComponent {

  def hdh: HandshakeDataHandler
  def handshakeService: HandshakeService

  def handshakeRoute(version: String, auth: String) = {
    import com.thenewmotion.ocpi.msgs.v2_0.Credentials._
    import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._
    import spray.httpx.SprayJsonSupport._

    path(hdh.config.endpoint) {
        post {
          entity(as[Creds]) { clientCreds =>
            handshakeService.registerVersionsEndpoint(version, auth, Credentials.fromOcpiClass(clientCreds)) match {
              case -\/(_) => reject()
              case \/-(newCreds) => complete(CredsResp(GenericSuccess.code,Some(GenericSuccess.default_message),
                currentTime.instance, newCreds))
            }
          }
      }
    }
  }
}