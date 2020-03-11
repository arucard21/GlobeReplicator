package com.github.arucard21.globe.replicator.distributedobject

object DOApplication extends App {
  DOServer.startServer("0.0.0.0", 8080)
}
