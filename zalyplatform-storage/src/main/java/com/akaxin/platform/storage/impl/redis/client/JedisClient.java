package com.akaxin.platform.storage.impl.redis.client;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import redis.clients.jedis.Jedis;

/**
 * 定制话的redisClient，通过自定义的jedis线程池实现
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-16 16:01:01
 */
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

	public Long incr(final String key) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.incr(key);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis incr key={} error", key), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return -1l;
	}

	public Long incr(final String key, final long integer) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.incrBy(key, integer);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis incr key={} by integer={} error", key, integer), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return -1l;
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

	public Long hdel(final String key, final String field) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.hdel(key, field);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis hget key={} ", key), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return -1l;
	}

	public Long hincrBy(final String key, final String field, final long value) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.hincrBy(key, field, value);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis hincrby key={} value={} error", key, value), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return -1l;
	}

	public Long zcard(final String key) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.zcard(key);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis zcard key={} error", key), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return -1l;
	}

	public Long zadd(final String key, final double score, final String member) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.zadd(key, score, member);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis zadd key={} score={} member={} error", key, score, member), e);
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return -1l;
	}

	public Set<String> zrange(final String key, final long start, final long end) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.zrange(key, start, end);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis zrange key={} start={} end={} error", key, start, end), e);
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

	public Long del(final String... keys) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.del(keys);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis delete key={} error", String.valueOf(keys), e));
		} finally {
			RedisPoolManager.returnResource(jedis);
		}
		return 0l;
	}

	public Long del(final String key) {
		Jedis jedis = null;
		try {
			jedis = RedisPoolManager.getJedis();
			return jedis.del(key);
		} catch (Exception e) {
			RedisPoolManager.close(jedis);
			logger.error(formatMessage("jedis delete key={} error", key, e));
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
