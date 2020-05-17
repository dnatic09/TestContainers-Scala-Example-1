package com.dnatic

import com.dimafeng.testcontainers.GenericContainer
import com.dnatic.api.TestContainersApi
import com.dnatic.data.{RedisClient, RedisTestContainersDao}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.testcontainers.containers.wait.strategy.Wait

trait TestContainersApiTestHarness extends FlatSpec with Matchers with BeforeAndAfterAll {
  private val REDIS_PORT = 6379
  private val container = GenericContainer("redis:5.0.8-alpine",
    exposedPorts = Seq(REDIS_PORT),
    waitStrategy = Wait.forListeningPort()
  )
  container.start()
  private val mappedRedisExternalPort = container.mappedPort(REDIS_PORT)
  private val redisClient = new RedisClient("localhost", mappedRedisExternalPort)
  private val dao = new RedisTestContainersDao(redisClient)
  private val api = new TestContainersApi(dao)

  override def afterAll(): Unit = {
    super.afterAll()
    container.stop()
  }

  def purgeAllData(): Unit = redisClient.doWithJedis(_.flushAll())

  def importFile(resource: String): Unit = {
    val res = getClass.getResourceAsStream(resource)
    val lines = scala.io.Source.fromInputStream(res).getLines()
    val map = lines.map { l =>
      val Array(k, v) = l.split('=')
      k -> v
    }.toMap
    map.foreach { case (k, v) => redisClient.doWithJedis(_.set(k, v)) }
  }

  def getDataId(dataId: String): Either[Throwable, Option[String]] = api.getDataByDataId(dataId)
}