package com.zaly.platform.storage.impl.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class RedisSessionDao {
	private static final Logger logger = LoggerFactory.getLogger(RedisSessionDao.class);

	private Jedis jedis = RedisManager.getSessionJedis();

	public boolean addSession(String key, Map<String, String> map) {
		if ("OK".equalsIgnoreCase(jedis.hmset(key, map))) {
			return true;
		}
		return false;
	}

	public Map<String, String> getSessionMap(String key) {
		return jedis.hgetAll(key);
	}

}