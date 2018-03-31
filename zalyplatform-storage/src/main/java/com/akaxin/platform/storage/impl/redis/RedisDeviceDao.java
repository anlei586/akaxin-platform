package com.akaxin.platform.storage.impl.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.storage.impl.redis.client.JedisClient;

/**
 * 设置用户设备相关信息存储。这里需要考虑过期或者定期删除操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-31 17:05:11
 */
public class RedisDeviceDao {
	private static final Logger logger = LoggerFactory.getLogger(RedisDeviceDao.class);

	public boolean addDeviceMap(String key, Map<String, String> map) {
		JedisClient jedis = new JedisClient();
		logger.debug("hmset userDevice key:{} map:{}", key, map);
		String result = jedis.hmset(key, map);
		if ("OK".equalsIgnoreCase(result)) {
			return true;
		}
		return false;
	}

	public Map<String, String> getDeviceMap(String key) {
		JedisClient jedis = new JedisClient();
		return jedis.hgetAll(key);
	}

	public String getDeviceField(String key, String field) {
		JedisClient jedis = new JedisClient();
		return jedis.hget(key, field);
	}
}