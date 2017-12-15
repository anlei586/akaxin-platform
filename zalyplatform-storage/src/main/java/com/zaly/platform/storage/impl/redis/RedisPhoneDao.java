package com.zaly.platform.storage.impl.redis;

import redis.clients.jedis.Jedis;

public class RedisPhoneDao {
	private static RedisPhoneDao instance = new RedisPhoneDao();

	private Jedis jedis = RedisManager.getPhoneJedis();

	public static RedisPhoneDao getInstance() {
		return instance;
	}

	public boolean setStringValue(String phondId, String value, int expireTime) {
		long result = 0;
		String setResult = jedis.set(phondId, value);
		if ("OK".equalsIgnoreCase(setResult)) {
			result = jedis.expire(phondId, expireTime);
		}
		return result == 1;
	}

	public String getStringValue(String key) {
		String value = jedis.get(key);
		return value;
	}

}
