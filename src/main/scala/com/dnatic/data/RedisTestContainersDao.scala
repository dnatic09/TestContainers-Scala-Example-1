package com.dnatic.data

class RedisTestContainersDao(redisClient: RedisClient) extends TestContainersDao {
  override def getData(dataId: String): String = {
    redisClient.doWithJedis { jedis => jedis.get(dataId) }
  }
}
