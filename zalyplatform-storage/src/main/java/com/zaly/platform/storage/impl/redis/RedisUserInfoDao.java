package com.zaly.platform.storage.impl.redis;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.utils.GsonUtils;
import com.akaxin.common.utils.ValidatorPattern;
import com.zaly.platform.storage.bean.PushTokenBean;
import com.zaly.platform.storage.bean.UserBean;
import com.zaly.platform.storage.bean.UserRealNameBean;
import com.zaly.platform.storage.constant.UserKey;

import redis.clients.jedis.Jedis;

/**
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

	public boolean saveUserInfo(UserBean bean) {
		Map<String, String> userMap = new HashMap<String, String>();

		String key = bean.getUserId();

		if (bean.getUserIdPrik() != null) {
			userMap.put(UserKey.userIdPrik, bean.getUserIdPrik());
		}
		if (bean.getUserIdPubk() != null) {
			userMap.put(UserKey.userIdPubk, bean.getUserIdPubk());
		}
		if (bean.getUserName() != null) {
			userMap.put(UserKey.userName, bean.getUserName());
		}
		if (bean.getUserPhoto() != null) {
			userMap.put(UserKey.userPhoto, bean.getUserPhoto());
		}
		if (bean.getClientType() != null) {
			userMap.put(UserKey.clientType, bean.getClientType());
		}
		if (ValidatorPattern.isPhoneId(bean.getUserPhoneId())) {
			userMap.put(UserKey.userPhoneId, bean.getUserPhoneId());
		}
		userMap.put(UserKey.pushToken, bean.getPushToken());

		logger.info("userINfoMap={}", GsonUtils.toJson(userMap));

		if ("OK".equalsIgnoreCase(jedis.hmset(key, userMap))) {
			return true;
		}

		return false;
	}

	public boolean updateRealUser(UserRealNameBean bean) {
		Map<String, String> phoneMap = new HashMap<String, String>();
		phoneMap.put(UserKey.userId, bean.getUserId());
		phoneMap.put(UserKey.userPassword, bean.getPassword());
		phoneMap.put(UserKey.phoneRoaming, bean.getPhoneRoaming());
		if (!"OK".equalsIgnoreCase(jedis.hmset(bean.getUserPhoneId(), phoneMap))) {
			return false;
		}
		return saveUserInfo(bean);
	}

	public Map<String, String> getPhoneInfoByPhone(String phoneId) {
		return jedis.hgetAll(phoneId);
	}

	public Map<String, String> getUserInfoByUserId(String userID) {
		return jedis.hgetAll(userID);
	}

	public String getUserPhoneId(String userId) {
		return jedis.hget(userId, UserKey.userPhoneId);
	}

	public String getPhoneGlobalRoaming(String phoneId) {
		return jedis.hget(phoneId, UserKey.phoneRoaming);
	}

	public PushTokenBean getUserPushInfo(String userId) {
		PushTokenBean bean = new PushTokenBean();
		bean.setClientType(jedis.hget(userId, UserKey.clientType));
		bean.setPushToken(jedis.hget(userId, UserKey.pushToken));
		return bean;
	}
}