package com.github.arucard21.globe.replicator.distributedobject

import java.net.URI

import akka.http.scaladsl.model.StatusCodes

import scala.sys.SystemProperties
import scala.util.{Failure, Success}

object DOApplication extends App {
  val PropertyKeyLookupServiceUrl : String = "lookupservice.url"
  val PropertyKeyDistributedObjectUrl : String = "distributedobject.url"
  val DistributedObjectName = "test"

  val props = new SystemProperties
  var lsUrl = if (props contains PropertyKeyLookupServiceUrl) props(PropertyKeyLookupServiceUrl) else "http://localhost:8080"
  val lookupServiceUri : URI = URI.create(lsUrl)
  var doUrl = if (props contains PropertyKeyDistributedObjectUrl) props(PropertyKeyDistributedObjectUrl) else "http://localhost:8080"
  val distributedObjectUri : URI = URI.create(doUrl)
  CommunicationSubobject.register(
    lookupServiceUri,
    DistributedObjectName,
    distributedObjectUri,
    {
      case Success(response) =>
        if (response.status == StatusCodes.OK)
          DOServer.startServer("0.0.0.0", distributedObjectUri.getPort)
        else
          println(s"""The registration of the distributed object with name "$DistributedObjectName" and location "$distributedObjectUri" failed on the lookup service at: $lookupServiceUri""")
      case Failure(_) => println(s"""The request for the registration of the distributed object with name "$DistributedObjectName" and location "$distributedObjectUri" failed on the lookup service at: $lookupServiceUri""")
    })
  }
