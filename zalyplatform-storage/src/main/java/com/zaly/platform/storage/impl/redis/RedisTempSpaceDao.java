package com.zaly.platform.storage.impl.redis;

import redis.clients.jedis.Jedis;

/**
 * 临时存储空间
 */
public class RedisTempSpaceDao {

	private static RedisTempSpaceDao instance = new RedisTempSpaceDao();
	private Jedis jedis = RedisManager.getPhoneJedis();

	public static RedisTempSpaceDao getInstance() {
		return instance;
	}

	public boolean setStringValue(String key, String value, int expireTime) {
		long result = 0;
		String setResult = jedis.set(key, value);
		if ("OK".equalsIgnoreCase(setResult)) {
			result = jedis.expire(key, expireTime);
		}
		return result == 1;
	}

	public String getStringValue(String key) {
		String value = jedis.get(key);
		return value;
	}
}
