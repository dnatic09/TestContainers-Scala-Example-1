package com.dnatic.data

import redis.clients.jedis.Jedis

class RedisClient(host: String, port: Int) {
  private val jedis = new Jedis(host, port)

  final def doWithJedis[T](fn: Jedis => T): T = {
    try {
      if (!jedis.isConnected) jedis.connect()
      fn(jedis)
    } finally { jedis.close()}
  }


}
