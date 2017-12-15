package com.zaly.platform.storage.impl.redis;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaly.common.utils.GsonUtils;
import com.zaly.platform.storage.bean.RealNameUserBean;
import com.zaly.platform.storage.bean.UserInfoBean;
import com.zaly.platform.storage.constant.UserInfoKey;

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

	public boolean saveUserInfo(UserInfoBean bean) {
		Map<String, String> userMap = new HashMap<String, String>();

		String key = bean.getUserId();

		if (bean.getUserIdPrik() != null) {
			userMap.put(UserInfoKey.userIdPrik, bean.getUserIdPrik());
		}
		if (bean.getUserIdPubk() != null) {
			userMap.put(UserInfoKey.userIdPubk, bean.getUserIdPubk());
		}
		if (bean.getUserName() != null) {
			userMap.put(UserInfoKey.userName, bean.getUserName());
		}
		if (bean.getUserPhoto() != null) {
			userMap.put(UserInfoKey.userPhoto, bean.getUserPhoto());
		}
		if (bean.getClientType() != null) {
			userMap.put(UserInfoKey.clientType, bean.getClientType());
		}

		logger.info("userINfoMap={}", GsonUtils.toJson(userMap));

		if ("OK".equalsIgnoreCase(jedis.hmset(key, userMap))) {
			return true;
		}

		return false;
	}

	public boolean updateRealUser(RealNameUserBean bean) {
		Map<String, String> phoneMap = new HashMap<String, String>();
		phoneMap.put(UserInfoKey.userId, bean.getUserId());
		phoneMap.put(UserInfoKey.userPassword, bean.getPassword());
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
}
