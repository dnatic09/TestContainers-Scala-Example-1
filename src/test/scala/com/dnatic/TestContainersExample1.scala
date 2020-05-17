package com.dnatic

import com.dimafeng.testcontainers.GenericContainer
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.testcontainers.containers.wait.strategy.Wait

class TestContainersExample1 extends FlatSpec with Matchers with BeforeAndAfterAll {
  private val container = GenericContainer("redis:5.0.8-alpine",
    waitStrategy = Wait.forListeningPort()
  )
  container.start()

  override def afterAll(): Unit = {
    super.afterAll()
    container.stop()
  }

  "TestContainers" should "start a Docker container" in {
    val cmd = container.execInContainer("/usr/local/bin/redis-cli", "PING")
    val output = cmd.getStdout
    val expected = "PONG"
    output.contains(expected) shouldBe true
  }
}
