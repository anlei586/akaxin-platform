package com.zaly.platform.storage.impl.redis;

import redis.clients.jedis.Jedis;

public class RedisManager {
	private static Jedis userInfoJedis;
	private static Jedis phoneJedis;
	private static Jedis tempJedis;

	public static Jedis getUserInfoJedis() {
		if (userInfoJedis == null) {
			userInfoJedis = new Jedis("localhost", 6379);
		}
		return userInfoJedis;
	}

	public static Jedis getPhoneJedis() {
		if (phoneJedis == null) {
			phoneJedis = new Jedis("localhost", 6379);
		}
		return phoneJedis;
	}

	public static Jedis getTempJedis() {
		if (tempJedis == null) {
			tempJedis = new Jedis("localhost", 6379);
		}
		return tempJedis;
	}
}
