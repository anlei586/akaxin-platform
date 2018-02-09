package com.akaxin.platform.storage.impl.redis;

import com.akaxin.platform.storage.impl.redis.client.JedisClient;

/**
 * 临时存储空间
 */
public class RedisTempSpaceDao {
	private static RedisTempSpaceDao instance = new RedisTempSpaceDao();

	public static RedisTempSpaceDao getInstance() {
		return instance;
	}

	public boolean setStringValue(String key, String value, int expireTime) {
		JedisClient jedis = new JedisClient();
		long result = 0;
		String setResult = jedis.set(key, value);
		if ("OK".equalsIgnoreCase(setResult)) {
			result = jedis.expire(key, expireTime);
		}
		return result == 1;
	}

	public String getStringValue(String key) {
		JedisClient jedis = new JedisClient();
		String value = jedis.get(key);
		return value;
	}
}
