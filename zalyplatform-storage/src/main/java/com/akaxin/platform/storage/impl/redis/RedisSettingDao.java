package com.akaxin.platform.storage.impl.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.storage.impl.redis.client.JedisClient;

/**
 * <pre>
 * 增加设置信息
 * 		1.mute静音设置相关（个人，站点，群组）
 * 		2.后期其他设置
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-16 16:05:01
 */
public class RedisSettingDao {
	private static final Logger logger = LoggerFactory.getLogger(RedisSettingDao.class);

	public boolean addSetting(String key, String field, String value) {
		JedisClient jedis = new JedisClient();
		logger.info("hset setting key:{} field:{} value:{}", key, field, value);
		long result = jedis.hset(key, field, value);
		if (result == 1 || result == 0) {
			return true;
		}
		return false;
	}

	public boolean addSettings(String key, Map<String, String> map) {
		JedisClient jedis = new JedisClient();
		logger.info("hmset setting key:{} map:{}", key, map);
		String result = jedis.hmset(key, map);
		if ("OK".equalsIgnoreCase(result)) {
			return true;
		}
		return false;
	}

	public boolean delSetting(String key, String field) {
		JedisClient jedis = new JedisClient();
		logger.info("hdel setting key:{} field:{} ", key, field);
		long result = jedis.hdel(key, field);
		if (result == 1 || result == 0) {
			return true;
		}
		return false;
	}

	public String getSetting(String key, String field) {
		JedisClient jedis = new JedisClient();
		return jedis.hget(key, field);
	}

	public Map<String, String> getSettingMap(String key) {
		JedisClient jedis = new JedisClient();
		return jedis.hgetAll(key);
	}

}