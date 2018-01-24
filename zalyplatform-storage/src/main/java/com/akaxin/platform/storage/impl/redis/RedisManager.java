package com.akaxin.platform.storage.impl.redis;

import redis.clients.jedis.Jedis;

public class RedisManager {
	private static Jedis userInfoJedis;
	private static Jedis phoneJedis;
	private static Jedis tempJedis;
	private static Jedis sessionJedis;
	private static Jedis userTokenJedis;

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

	public static Jedis getSessionJedis() {
		if (sessionJedis == null) {
			sessionJedis = new Jedis("localhost", 6379);
		}
		return sessionJedis;
	}

	public static Jedis getUserTokenJedis() {
		if (userTokenJedis == null) {
			userTokenJedis = new Jedis("localhost", 6379);
		}
		return userTokenJedis;
	}
}
