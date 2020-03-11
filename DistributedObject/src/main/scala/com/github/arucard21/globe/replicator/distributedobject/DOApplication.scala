package com.github.arucard21.globe.replicator.distributedobject

import java.net.URI

import scala.sys.SystemProperties

object DOApplication extends App {
  val PropertyKeyLookupServiceUrl : String = "lookupservice.url"
  val PropertyKeyDistributedObjectUrl : String = "distributedobject.url"
  val DistributedObjectName = "test"

  val props = new SystemProperties
  var lsUrl = if (props contains PropertyKeyLookupServiceUrl) props(PropertyKeyLookupServiceUrl) else "http://localhost:8080"
  val lookupServiceUri : URI = URI.create(lsUrl)
  var doUrl = if (props contains PropertyKeyDistributedObjectUrl) props(PropertyKeyDistributedObjectUrl) else "http://localhost:8080"
  val distributedObjectUri : URI = URI.create(doUrl)
  val registrationSucceeded = CommunicationSubobject.register(lookupServiceUri, DistributedObjectName, distributedObjectUri)
  if (registrationSucceeded){
    DOServer.startServer("0.0.0.0", distributedObjectUri.getPort)
  }
  else{
    println(s"Could not register the distributed object with name $distributedObjectUri on location $distributedObjectUri with the lookup service at $lookupServiceUri")
  }
}
