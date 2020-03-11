package com.github.arucard21.globe.replicator.distributedobject

import java.net.URI

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, HttpApp, Route}
import com.fasterxml.jackson.databind.{ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

case class RequestFromOtherObject(method: String, parameter: Int)

object DOServer extends HttpApp {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override protected def routes: Route =
    Directives.concat(
      Directives.pathSingleSlash {
        Directives.post {
          Directives.decodeRequest{
            Directives.entity(Directives.as[String]){ requestContent =>
              Directives.complete{
                val requestData = mapper.readValue[RequestFromOtherObject](requestContent, classOf[RequestFromOtherObject])
                ControlSubobject.handle_request(requestData.method, requestData.parameter)
                HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Request handles successfully")
              }
            }
          }
        }
      },
      Directives.path("getNumber") {
        Directives.get {
          Directives.complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, ControlSubobject.getNumber.toString))
        }
      },
      Directives.pathPrefix("setNumber" / IntNumber) { newNumber =>
        Directives.post {
          Directives.complete{
            val prevNumber = ControlSubobject.getNumber
            if (prevNumber == newNumber){
              HttpEntity(ContentTypes.`text/plain(UTF-8)`, "The number of this distributed object did not need to be updated. It was already set to " + prevNumber.toString)
            }
            else{
              ControlSubobject.setNumber(newNumber)
              val number = ControlSubobject.getNumber
                if (number == prevNumber){
                HttpResponse(StatusCodes.InternalServerError, entity = "The change could not be applied (it might not have been replicated correctly)")
              }
              else{
                HttpEntity(ContentTypes.`text/plain(UTF-8)`, "The number of this distributed object has been updated from " + prevNumber + " to " + number)
              }
            }
          }
        }
      }
    )
}
