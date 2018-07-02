package com.akaxin.platform.storage.impl.redis;

import java.util.Map;

import com.akaxin.platform.storage.impl.redis.client.JedisClient;

/**
 * 用户访问站点存储操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-29 20:12:58
 */
public class RedisUserSiteDao {
	private JedisClient jedis = new JedisClient();

	private RedisUserSiteDao() {
	}

	private static RedisUserSiteDao instance = new RedisUserSiteDao();

	public static RedisUserSiteDao getInstance() {
		return instance;
	}

	public boolean addUserSite(String key, String field, String value) {
		if (jedis.hset(key, field, value) >= 0) {
			return true;
		}
		return false;
	}

	public boolean addUserSites(String key, Map<String, String> map) {
		if ("OK".equalsIgnoreCase(jedis.hmset(key, map))) {
			return true;
		}
		return false;
	}

	public String getUserSite(String key, String field) {
		return jedis.hget(key, field);
	}

	public long delUserSite(String key, String field) {
		return jedis.hdel(key, field);
	}

}
