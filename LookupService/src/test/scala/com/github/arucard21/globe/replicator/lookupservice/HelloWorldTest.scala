package com.github.arucard21.globe.replicator.lookupservice

import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HelloWorldTest extends AnyFunSuite {
  test("helloWorldMethod has correct text") {
    assert(HelloWorld.helloWorldMethod() == "Hello World from Lookup Service!")
  }
}
