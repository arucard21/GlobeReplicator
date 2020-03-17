package com.github.arucard21.globe.replicator.distributedobject

import java.net.URI

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.stream.ActorMaterializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}

import scala.concurrent.{Future, Await}
import java.util.UUID
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.duration.Duration
import scala.util.Try

case class Registration(name: String, location: URI)

object CommunicationSubobject {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def findLocationsForDistributedObject(lookupServiceUri : Uri, distributedObjectName : String) = {
    val getIdFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
      method = HttpMethods.GET,
      uri = lookupServiceUri + s"/getId/$distributedObjectName"
    ))
    getIdFuture.flatMap ( (getIdResponse) => {
        if (getIdResponse.status == StatusCodes.OK) {
          val objectIdString: String = Await.result(Unmarshal(getIdResponse).to[String], Duration.Inf)
          val objectId = UUID.fromString(objectIdString)
          val getLocationsFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
            method = HttpMethods.GET,
            uri = lookupServiceUri + s"/getLocations/${objectId.toString}"
          ))
          getLocationsFuture.flatMap( (getLocationsResponse) => {
              if (getLocationsResponse.status == StatusCodes.OK) {
                val objectLocationsJson = Await.result(Unmarshal(getLocationsResponse).to[String], Duration.Inf)
                val objectLocationsList = mapper.readValue[Array[String]](objectLocationsJson)
                Future.successful(objectLocationsList.map(location => Uri(location)))
              }
              else
                Future.failed(new IllegalStateException(s"""Could not retrieve the locations of the object with the ID "$objectId" on the lookup service at: $lookupServiceUri"""))
            })
        }
        else
          Future.failed(new IllegalStateException(s"""Could not retrieve the ID of the object with the name "$distributedObjectName" on the lookup service at: $lookupServiceUri"""))
    })
  }

  /*def send_request = {
    ???
  }*/

  def send_request(requestUri : URI, method : String, param: Int, onCompleteFunction : (Try[HttpResponse]) => Unit) = {
    var endpoint = Uri(requestUri.toString).withPath(Path(s"/$method"))
    var request_method = HttpMethods.GET

    if (method == "setNumber") {
      endpoint = endpoint.withPath(Path(s"/$method/$param"))
      request_method = HttpMethods.POST
    }

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
      method = request_method,
      uri = endpoint
    ))
    responseFuture.onComplete(onCompleteFunction)
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
