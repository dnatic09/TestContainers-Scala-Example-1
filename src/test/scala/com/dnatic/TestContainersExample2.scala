package com.dnatic

import com.dnatic.api.TestContainersApi
import com.dnatic.data.{RedisClient, RedisTestContainersDao}
import com.dnatic.exception._
import redis.clients.jedis.exceptions.JedisException

class TestContainersExample2 extends TestContainersApiTestHarness {
  override def beforeAll(): Unit = {
    super.beforeAll()
    importFile("/etc/data1.txt")
  }

  "TestContainersEx2" should "return NONE when the data does not exist" in {
    val result = getDataId("keyThatIsBad")
    result shouldBe Right(None)
  }

  it should "return a good result" in {
    val result = getDataId("key2")
    result shouldBe Right(Some("ABC123456789"))
  }

  it should "fail when length < 10" in {
    val key = "key1"
    val result = getDataId(key)
    result shouldBe Left(InvalidLengthException(key))
  }

  it should "fail when does not contain ABC" in {
    val key = "key3"
    val result = getDataId(key)
    result shouldBe Left(InvalidContentException(key))
  }

  it should "throw an exception when connection fails" in {
    val redisClient = new RedisClient("localhost", 44444)
    val dao = new RedisTestContainersDao(redisClient)
    val api = new TestContainersApi(dao)
    val result = api.getDataByDataId("key1")
    result.isLeft shouldBe true
    val ex = result.left.get
    ex.isInstanceOf[JedisException] shouldBe true
  }
}
