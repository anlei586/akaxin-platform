package com.akaxin.platform.storage.test;

import com.akaxin.platform.storage.impl.redis.client.RedisPoolManager;

import redis.clients.jedis.Jedis;

public class TestJedisPool {
	public static void main(String[] args) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			String result = jedis.set("redisPoolTest", "100");

			System.out.println(result);
		} catch (Exception e) {
			RedisPoolManager.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
	}
}
