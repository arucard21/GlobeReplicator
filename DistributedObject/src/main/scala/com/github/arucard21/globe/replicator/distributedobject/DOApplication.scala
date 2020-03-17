package com.github.arucard21.globe.replicator.distributedobject

import java.net.URI

import akka.http.scaladsl.model.StatusCodes

import scala.sys.SystemProperties
import scala.util.{Failure, Success}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import akka.http.scaladsl.model.Uri

object DOApplication extends App {
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
}
