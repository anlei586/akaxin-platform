package com.zaly.platform.storage.impl.redis;

import redis.clients.jedis.Jedis;

/**
 * 手机验证码，读写操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-16 14:20:00
 */
public class RedisPhoneDao {
	private static RedisPhoneDao instance = new RedisPhoneDao();

	private Jedis jedis = RedisManager.getPhoneJedis();

	public static RedisPhoneDao getInstance() {
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
