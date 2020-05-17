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

  /**
   * Removes all entries from the K/V (Redis) store
   */
  def purgeAllData(): Unit = redisClient.doWithJedis(_.flushAll())

  /**
   * Import a line-delimited file with entries defined in the pattern (key=value)
   * @param resource Path to resource on the classpath
   */
  def importFile(resource: String): Unit = {
    val res = getClass.getResourceAsStream(resource)
    try {
      val lines = scala.io.Source.fromInputStream(res).getLines()
      val map = lines.map { l =>
        val Array(k, v) = l.split('=')
        k -> v
      }.toMap
      map.foreach { case (k, v) => redisClient.doWithJedis(_.set(k, v)) }
    } finally res.close()
  }

  /**
   * Import the specified key and value to the K/V (Redis) store
   * @param key Key of the entry
   * @param valueForKey Value of the entry
   */
  def importKey(key: String, valueForKey: String): Unit = redisClient.doWithJedis(_.set(key, valueForKey))

  /**
   * Call the API to retrieve data by the specified dataId
   * @param dataId dataId of the K/V entry
   * @return Right(Some(VALUE)), if the value exists and is valid; Right(NONE), if no entry exists; LEFT(Throwable) when a connection error occurs or the entry is not valid
   */
  def getDataId(dataId: String): Either[Throwable, Option[String]] = api.getDataByDataId(dataId)
}