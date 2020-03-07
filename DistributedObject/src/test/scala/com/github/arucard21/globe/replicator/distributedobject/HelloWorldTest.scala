package com.github.arucard21.globe.replicator.distributedobject

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HelloWorldTest extends AnyFunSuite {
  test("helloWorldMethod has correct text") {
    assert(HelloWorld.helloWorldMethod() == "Hello World from Distributed Object!")
  }
}
