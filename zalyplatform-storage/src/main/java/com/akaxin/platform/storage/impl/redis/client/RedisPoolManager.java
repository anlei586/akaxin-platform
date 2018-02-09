package com.akaxin.platform.storage.impl.redis.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPoolManager {
	private static JedisPool jedisPool;
	private static JedisPoolConfig jedisConfig;
	private static final String JEDIS_HOST = "localhost";

	static {
		initRedisPool();
	}

	private static void initRedisPool() {
		jedisConfig = new JedisPoolConfig();
		// true 连接耗尽，阻塞直到超时
		jedisConfig.setBlockWhenExhausted(true);
		// 使用连接池的jmx, 默认true使用
		jedisConfig.setJmxEnabled(true);
		// 最大空闲连接数
		jedisConfig.setMaxIdle(20);
		// 在获取连接的时候检查有效性, 默认false，这里检测连接是否可用
		jedisConfig.setTestOnBorrow(true);
		jedisConfig.setMaxWaitMillis(300);
		jedisConfig.setMaxTotal(200);
		jedisPool = new JedisPool(jedisConfig, JEDIS_HOST);
	}

	public static Jedis getJedis() {
		if (jedisPool == null) {
			initRedisPool();
		}
		return jedisPool.getResource();
	}

	public static void returnResource(Jedis jedis) {
		jedisPool.returnResource(jedis);
	}

	public static void close(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	public static void returnBrokenResource(Jedis jedis) {
		jedisPool.returnBrokenResource(jedis);
	}
}
