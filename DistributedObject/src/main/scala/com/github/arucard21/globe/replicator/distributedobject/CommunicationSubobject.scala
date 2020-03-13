package com.github.arucard21.globe.replicator.distributedobject

import java.net.URI

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.stream.ActorMaterializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.concurrent.Future
import scala.util.Try

case class Registration(name: String, location: URI)

object CommunicationSubobject {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def send_request = {
    ???
  }

  def register(lookupServiceUri : URI, name : String, location : URI, onCompleteFunction : (Try[HttpResponse]) => Unit) = {
    val registration = new Registration(name, location)
    val registrationJson : String = mapper.writeValueAsString(registration)
    val registrationUri = Uri(lookupServiceUri.toString).withPath(Path("/register"))

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = registrationUri,
      entity = registrationJson
    ))
    responseFuture.onComplete(onCompleteFunction)
  }

  def acquire_lock = {
    ???
  }

  def release_lock = {
    ???
  }

}
