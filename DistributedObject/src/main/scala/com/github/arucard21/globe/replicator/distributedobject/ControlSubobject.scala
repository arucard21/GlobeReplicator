package com.github.arucard21.globe.replicator.distributedobject

object ControlSubobject {

  def handle_request = {
    ???
  }

  def getState = {
    SemanticsSubobject.number.toString
  }

  // FIXME replace this with actual Globe replication mechanism
  def setState (newNumber : Integer) = {
    SemanticsSubobject.number = newNumber
  }
}
