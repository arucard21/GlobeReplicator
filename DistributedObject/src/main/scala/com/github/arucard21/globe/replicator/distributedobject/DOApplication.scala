package com.github.arucard21.globe.replicator.distributedobject

import java.net.URI

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes

import scala.sys.SystemProperties
import scala.util.{Failure, Success}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer

object DOApplication extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val distributedObjectName = "test"

  val lookupServiceUri = getLookupServiceUri
  val distributedObjectUri = getDistributedObjectUri

  def getLookupServiceUri = {
    val propertyKeyLookupServiceUrl : String = "lookupservice.url"
    val props = new SystemProperties
    val lsUrl = if (props contains propertyKeyLookupServiceUrl) props(propertyKeyLookupServiceUrl) else "http://localhost:8080"
    URI.create(lsUrl)
  }

  def getDistributedObjectUri = {
    val propertyKeyDistributedObjectUrl : String = "distributedobject.url"
    val props = new SystemProperties
    val doUrl = if (props contains propertyKeyDistributedObjectUrl) props(propertyKeyDistributedObjectUrl) else "http://localhost:8080"
    URI.create(doUrl)
  }

  CommunicationSubobject.register(
    lookupServiceUri,
    distributedObjectName,
    distributedObjectUri,
    {
      case Success(response) =>
        if (response.status == StatusCodes.OK)
          DOServer.startServer("0.0.0.0", distributedObjectUri.getPort)
        else
          println(s"""The registration of the distributed object with name "$distributedObjectName" and location "$distributedObjectUri" failed on the lookup service at: $lookupServiceUri""")
      case Failure(_) => println(s"""The request for the registration of the distributed object with name "$distributedObjectName" and location "$distributedObjectUri" failed on the lookup service at: $lookupServiceUri""")
    })

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  val locations : Array[Uri] = Await.result(CommunicationSubobject.findLocationsForDistributedObject(Uri("http://localhost:8080"), "test"), Duration.Inf)
  var locationsString = locations.map(location => location.toString()).toArray
  println(mapper.writeValueAsString(locationsString))

  var result = CommunicationSubobject.send_request(
    URI.create("http://localhost:8081"),
    "getNumber",
    0,
    {
      case Success(response) => {
        val resultNumber = Await.result(Unmarshal(response).to[String], Duration.Inf)
        val number : Int = resultNumber.toInt
        println(s"success: ${number}")
      }
      case Failure(_) => println(s"*********** failed *************")
    })
}
