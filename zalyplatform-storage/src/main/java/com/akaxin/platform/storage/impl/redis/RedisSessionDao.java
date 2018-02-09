package com.akaxin.platform.storage.impl.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.storage.constant.SessionConst;
import com.akaxin.platform.storage.impl.redis.client.JedisClient;

import redis.clients.jedis.Jedis;

public class RedisSessionDao {
	private static final Logger logger = LoggerFactory.getLogger(RedisSessionDao.class);

	public boolean addSession(String key, Map<String, String> map) {
		JedisClient jedis = new JedisClient();
		logger.info("hmset session key:{} map:{}", key, map);
		String result = jedis.hmset(key, map);
		if ("OK".equalsIgnoreCase(result)) {
			jedis.expire(key, SessionConst.SESSION_EXPIRE_TIME);
			return true;
		}
		return false;
	}

	public Map<String, String> getSessionMap(String key) {
		JedisClient jedis = new JedisClient();
		return jedis.hgetAll(key);
	}

}