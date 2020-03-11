package com.github.arucard21.globe.replicator.distributedobject

object ControlSubobject {

  def handle_request(method : String, parameter: Int) = {
    if(method == "setNumber"){
      SemanticsSubobject.number = parameter
    }
    else{
      throw new IllegalStateException("Could not handle the request with unknown method: " + method)
    }
  }

  def getNumber = {
    SemanticsSubobject.number
  }

  // FIXME replace this with actual Globe replication mechanism
  def setNumber (newNumber : Integer) = {
    SemanticsSubobject.number = newNumber
  }

  def getState = {
    SemanticsSubobject.number.toString
  }

  def setState (newState : String) = {
    SemanticsSubobject.number = newState.toInt
  }
}
