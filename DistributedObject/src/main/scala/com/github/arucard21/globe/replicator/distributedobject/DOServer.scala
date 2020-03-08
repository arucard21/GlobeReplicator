package com.github.arucard21.globe.replicator.distributedobject

import java.net.URI
import java.util.UUID

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, HttpApp, Route}
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.{JsonMappingException, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.collection.mutable

case class Registration(name: String, location: URI)

object DOServer extends HttpApp {
  var number : Integer = 0
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override protected def routes: Route =
    Directives.concat(
      Directives.path("getNumber") {
        Directives.get {
          Directives.complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, number.toString))
        }
      },
      Directives.pathPrefix("setNumber" / IntNumber) { newNumber =>
        Directives.post {
          Directives.complete{
            var prevNumber = number
            number = newNumber
            if (replicateChange()){
              HttpEntity(ContentTypes.`text/plain(UTF-8)`, "The number of this distributed object has been updated from "+prevNumber+" to "+number)
            }
            else {
              number = prevNumber
              HttpResponse(StatusCodes.InternalServerError, entity="The change could not be applied since it could not be replicated correct")
            }
          }
        }
      }
    )

  /**
   * Replicate the state of this local object to all replicas of this distributed object according to the Globe replication mechanism.
   *
   * @return true iff the replication was successful.
   */
  def replicateChange(): Boolean ={
    // TODO
    true
  }
}
