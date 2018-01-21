package com.zaly.platform.storage.impl.redis;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.utils.GsonUtils;
import com.akaxin.common.utils.ValidatorPattern;
import com.zaly.platform.storage.bean.PushTokenBean;
import com.zaly.platform.storage.bean.UserBean;
import com.zaly.platform.storage.constant.UserKey;

import redis.clients.jedis.Jedis;

/**
 * <pre>
 * 用户信息包括两部分(均为hash结构)
 * 		1.用户基本资料信息user_id,user_id_prik,user_id_pubk
 * 		2.用户实名绑定手机号
 * 
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.1
 */
public class RedisUserInfoDao {
	private static final Logger logger = LoggerFactory.getLogger(RedisUserInfoDao.class);
	private static RedisUserInfoDao instance = new RedisUserInfoDao();
	private Jedis jedis = RedisManager.getUserInfoJedis();

	public static RedisUserInfoDao getInstance() {
		return instance;
	}

	public boolean hset(String key, String field, String value) {
		logger.info("hset user key={},field={},value={}", key, field, value);
		jedis.hset(key, field, value);
		return false;
	}

	public String hget(String key, String field) {
		logger.info("hget user key={},field={}", key, field);
		return jedis.hget(key, field);
	}

	public boolean saveUserInfo(String key, UserBean bean) {
		Map<String, String> userMap = new HashMap<String, String>();
		if (bean.getUserIdPrik() != null) {
			userMap.put(UserKey.userIdPrik, bean.getUserIdPrik());
		}
		if (bean.getUserIdPubk() != null) {
			userMap.put(UserKey.userIdPubk, bean.getUserIdPubk());
		}
		if (bean.getClientType() > 0) {
			userMap.put(UserKey.clientType, bean.getClientType() + "");
		}
		if (ValidatorPattern.isPhoneId(bean.getPhoneId())) {
			userMap.put(UserKey.userPhoneId, bean.getPhoneId());
		}
		if (StringUtils.isNotBlank(bean.getPushToken())) {
			userMap.put(UserKey.pushToken, bean.getPushToken());
		}
		if (StringUtils.isNotBlank(bean.getRom())) {
			userMap.put(UserKey.rom, bean.getRom());
		}
		if (StringUtils.isNotBlank(bean.getDeviceId())) {
			userMap.put(UserKey.deviceId, bean.getDeviceId());
		}

		logger.info("userINfoMap={}", GsonUtils.toJson(userMap));

		if (userMap.size() > 0) {
			if ("OK".equalsIgnoreCase(jedis.hmset(key, userMap))) {
				return true;
			}
		}
		return false;
	}

	public boolean updateUserInfo(String key, Map<String, String> map) {
		boolean result = false;
		if ("OK".equalsIgnoreCase(jedis.hmset(key, map))) {
			result = true;
		}
		logger.info("hmset result={} key={} map={}", result, key, GsonUtils.toJson(map));

		return result;
	}

	public Map<String, String> getUserInfoMap(String key) {
		return jedis.hgetAll(key);
	}

	public PushTokenBean getUserPushInfo(String key) {
		PushTokenBean bean = new PushTokenBean();
		bean.setClientType(jedis.hget(key, UserKey.clientType));
		bean.setPushToken(jedis.hget(key, UserKey.pushToken));
		return bean;
	}

	// phone hmap
	public boolean updatePhoneInfo(String phoneKey, UserBean bean) {
		Map<String, String> phoneMap = new HashMap<String, String>();
		phoneMap.put(UserKey.userId, bean.getUserId());
		phoneMap.put(UserKey.phoneRoaming, bean.getPhoneRoaming());
		if (!"OK".equalsIgnoreCase(jedis.hmset(phoneKey, phoneMap))) {
			return false;
		}
		String userKey = "user_id_" + bean.getUserId();
		return saveUserInfo(userKey, bean);
	}

	public Map<String, String> getPhoneInfoMap(String key) {
		return jedis.hgetAll(key);
	}

	public String getPhoneField(String key, String field) {
		return jedis.hget(key, UserKey.userPhoneId);
	}

}