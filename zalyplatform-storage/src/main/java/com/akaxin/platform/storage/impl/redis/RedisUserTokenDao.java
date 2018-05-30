package com.akaxin.platform.storage.impl.redis;

import java.util.Map;

import com.akaxin.platform.storage.impl.redis.client.JedisClient;

public class RedisUserTokenDao {
	private JedisClient jedis = new JedisClient();

	private RedisUserTokenDao() {

	}

	public static RedisUserTokenDao getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {
		private static RedisUserTokenDao instance = new RedisUserTokenDao();
	}

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

	public long delUserTokenField(String key, String field) {
		return jedis.hdel(key, field);
	}

}