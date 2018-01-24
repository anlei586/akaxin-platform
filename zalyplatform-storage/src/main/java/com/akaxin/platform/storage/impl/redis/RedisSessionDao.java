package com.akaxin.platform.storage.impl.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.storage.constant.SessionConst;

import redis.clients.jedis.Jedis;

public class RedisSessionDao {
	private static final Logger logger = LoggerFactory.getLogger(RedisSessionDao.class);

	private Jedis jedis = RedisManager.getSessionJedis();

	public boolean addSession(String key, Map<String, String> map) {
		String result = jedis.hmset(key, map);
		logger.info("hmset session key:{} map:{}", key, map);
		if ("OK".equalsIgnoreCase(result)) {
			jedis.expire(key, SessionConst.SESSION_EXPIRE_TIME);
			return true;
		}
		return false;
	}

	public Map<String, String> getSessionMap(String key) {
		return jedis.hgetAll(key);
	}

}