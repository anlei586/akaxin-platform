package com.akaxin.platform.storage.impl.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.storage.constant.SessionConst;
import com.akaxin.platform.storage.impl.redis.client.JedisClient;

public class RedisSessionDao {
	private static final Logger logger = LoggerFactory.getLogger(RedisSessionDao.class);

	// 添加用户的sessionid，并且设置过期时间，如果过期时间0，则使用默认过期时间
	public boolean addSession(String key, Map<String, String> map, int expireSec) {
		JedisClient jedis = new JedisClient();
		logger.debug("hmset session key:{} map:{}", key, map);
		String result = jedis.hmset(key, map);
		if ("OK".equalsIgnoreCase(result)) {
			int extime = expireSec > 0 ? expireSec : SessionConst.SESSION_EXPIRE_TIME;
			jedis.expire(key, extime);
			return true;
		}
		return false;
	}

	public Map<String, String> getSessionMap(String key) {
		JedisClient jedis = new JedisClient();
		return jedis.hgetAll(key);
	}

	public boolean deleteSession(String key) {
		JedisClient jedis = new JedisClient();
		return jedis.del(key) > 0;
	}
}