package com.akaxin.platform.storage.impl.redis.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import redis.clients.jedis.Jedis;

public class JedisClient {
	private static final Logger logger = LoggerFactory.getLogger(JedisClient.class);

	public String set(final String key, final String value) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.set(key, value);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis set key={} value={}", key, value), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return null;
	}

	public String get(final String key) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.get(key);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis get key={} ", key), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return null;
	}

	public String hmset(final String key, final Map<String, String> hash) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.hmset(key, hash);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis get key={} ", key), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return null;
	}

	public Map<String, String> hgetAll(final String key) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.hgetAll(key);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis hgetall key={} ", key), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return null;
	}

	public Long hset(final String key, final String field, final String value) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.hset(key, field, value);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis hset key={} ", key), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return Long.valueOf(-1);
	}

	public String hget(final String key, final String field) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.hget(key, field);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis hget key={} ", key), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return null;
	}

	public Long expire(final String key, final int seconds) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.expire(key, seconds);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis expire key={} expireSeconds={}", key, seconds), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return 0l;
	}

	private String formatMessage(String messagePattern, Object... objects) {
		FormattingTuple format = MessageFormatter.arrayFormat(messagePattern, objects);
		return format.getMessage();
	}

}
