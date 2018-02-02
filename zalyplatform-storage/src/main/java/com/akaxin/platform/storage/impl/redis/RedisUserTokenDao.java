package com.akaxin.platform.storage.impl.redis;

import java.util.Map;

import redis.clients.jedis.Jedis;

public class RedisUserTokenDao {
	private Jedis jedis = RedisManager.getUserTokenJedis();

	public boolean addUserToken(String key, String field, String value) {
		if (jedis.hset(key, field, value) >= 0) {
			return true;
		}
		return false;
	}

	// key="user_token_deviceId"
	public boolean addUserToken(String key, Map<String, String> map) {
		if ("OK".equalsIgnoreCase(jedis.hmset(key, map))) {
			return true;
		}
		return false;
	}

	public String getUserTokenField(String key, String field) {
		return jedis.hget(key, field);
	}

}