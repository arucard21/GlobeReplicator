package com.github.arucard21.globe.replicator.distributedobject

sealed trait GlobeMessage { def message: String }
case object Invoke extends GlobeMessage { val message = "Invoke" }
case object InvokeSend extends GlobeMessage { val message = "Send" }
case object Return extends GlobeMessage { val message = "Return" }
case class Fail(message: String) extends GlobeMessage

object ReplicationSubobject {
  private var lock = false;
  def start(method: String, parameter: Int = 0): GlobeMessage = {
    method match {
      case "setNumber" => setNumber(parameter)
      case "getNumber" => Invoke // TODO: add checks? not sure when to fail this
      case _ => Fail("Unknown method called")
    }
  }

  def setNumber(parameter: Int): GlobeMessage = {
    if(lock) {
      Fail("Local object currently locked.");
    }
    // TODO: Replace with correct call to acquire lock
    if(CommunicationSubobject.acquire_lock) {
      InvokeSend
    }
    Fail("Failed to acquire lock from other objects.")
  }

  def invoked(): Unit = {
    lock = true
  }

  def send(method: String, parameter: Int): GlobeMessage = {
    // TODO: Replace with proper call to sub-object
    if(CommunicationSubobject.send_request) {
      Return
    }
    Fail(s"Failed to send($method, $parameter)")
  }

  def finish(): GlobeMessage = {
    lock = false
    if(CommunicationSubobject.release_lock) {
      Return
    }
    Fail("Failed to release_lock in communication subobject")
  }

  def acquireLock(): Boolean = {
    if(!lock) {
      lock = true;
      true
    }
    false
  }

  def releaseLock():Boolean = {
    if(lock) {
      lock = false
      true
    }
    false
  }
}
