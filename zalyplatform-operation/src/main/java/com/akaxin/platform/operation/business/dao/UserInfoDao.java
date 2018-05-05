package com.akaxin.platform.operation.business.dao;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.api.IUserInfoDao;
import com.akaxin.platform.storage.bean.UserBean;
import com.akaxin.platform.storage.constant.UserKey;
import com.akaxin.platform.storage.service.UserInfoDaoService;
import com.akaxin.proto.core.ClientProto;
import com.akaxin.proto.core.ClientProto.ClientType;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.08 14:32:32
 */
public class UserInfoDao {
	private static final Logger logger = LoggerFactory.getLogger(UserInfoDao.class);
	private static UserInfoDao instance = new UserInfoDao();
	private IUserInfoDao userDao = new UserInfoDaoService();

	public static UserInfoDao getInstance() {
		return instance;
	}

	public boolean saveUserInfo(UserBean userBean) {
		try {
			if (userBean != null && StringUtils.isNotBlank(userBean.getUserId())) {
				String redisKey = RedisKeyUtils.getUserIdKey(userBean.getUserId());
				return userDao.saveUserInfo(redisKey, userBean);
			}
		} catch (Exception e) {
			logger.error("save user info error.", e);
		}
		return false;
	}

	public boolean updateUserInfo(String userId, Map<String, String> map) {
		try {
			if (StringUtils.isNotBlank(userId) && map != null) {
				String key = RedisKeyUtils.getUserIdKey(userId);
				return userDao.updateUserInfo(key, map);
			}
		} catch (Exception e) {
			logger.error("update user info error.", e);
		}
		return false;
	}

	public boolean updateUserField(String userId, String field, String value) {
		try {
			String redisKey = RedisKeyUtils.getUserIdKey(userId);
			return userDao.hset(redisKey, field, value);
		} catch (Exception e) {
			logger.error("update user info error.", e);
		}
		return false;
	}

	public boolean delUserField(String userId, String field) {
		try {
			String redisKey = RedisKeyUtils.getUserIdKey(userId);
			userDao.hdel(redisKey, field);
			return true;
		} catch (Exception e) {
			logger.error("del user filed error.", e);
		}
		return false;
	}

	public boolean updatePhoneInfo(UserBean userBean) {
		try {
			if (userBean != null && StringUtils.isNotBlank(userBean.getPhoneId())) {
				String redisKey = RedisKeyUtils.getUserPhoneKey(userBean.getPhoneId());
				return userDao.updatePhoneInfo(redisKey, userBean);
			}
		} catch (Exception e) {
			logger.error("update rean user info error.", e);
		}
		return false;
	}

	public UserBean getRealNameUserInfo(String phoneId) {
		UserBean userBean = new UserBean();
		try {
			String phoneKey = RedisKeyUtils.getUserPhoneKey(phoneId);
			Map<String, String> phoneMap = userDao.getPhoneInfoMap(phoneKey);
			userBean.setUserId(phoneMap.get(UserKey.userId));

			String userKey = RedisKeyUtils.getUserIdKey(userBean.getUserId());
			Map<String, String> userInfoMap = userDao.getUserInfoMap(userKey);

			userBean.setUserIdPrik(userInfoMap.get(UserKey.userIdPrik));
			userBean.setUserIdPubk(userInfoMap.get(UserKey.userIdPubk));

		} catch (Exception e) {
			logger.error("getRealUserInfo by phoneid error.", e);
		}
		return userBean;
	}

	/**
	 * 获取用户手机号
	 */
	public String getUserPhoneId(String userId) {
		try {
			String userKey = RedisKeyUtils.getUserIdKey(userId);
			return userDao.hget(userKey, UserKey.userPhoneId);
		} catch (Exception e) {
			logger.error("get phone id error.userIdf=" + userId, e);
		}
		return null;
	}

	// 获取手机国际区号
	public String getPhoneGlobalRoaming(String phoneId) {
		try {
			String phoneKey = RedisKeyUtils.getUserPhoneKey(phoneId);
			return userDao.hget(phoneKey, UserKey.phoneRoaming);
		} catch (Exception e) {
			logger.error("get phone global roaming error.phoneId=" + phoneId, e);
		}
		return null;
	}

	public String getPushToken(String userId) {
		try {
			String redisKey = RedisKeyUtils.getUserIdKey(userId);
			return userDao.hget(redisKey, UserKey.pushToken);
		} catch (Exception e) {
			logger.error("get push token info error", e);
		}
		return null;
	}

	public String getLatestDeviceId(String userId) {
		try {
			String redisKey = RedisKeyUtils.getUserIdKey(userId);
			return userDao.hget(redisKey, UserKey.deviceId);
		} catch (Exception e) {
			logger.error("get push token info error", e);
		}
		return null;
	}

	public ClientProto.ClientType getClientType(String userId) {
		try {
			String redisKey = RedisKeyUtils.getUserIdKey(userId);
			String type = userDao.hget(redisKey, UserKey.clientType);
			if (StringUtils.isNumeric(type)) {
				ClientProto.ClientType clientType = ClientProto.ClientType.forNumber(Integer.valueOf(type));
				return clientType;
			}
		} catch (Exception e) {
			logger.error("get client type error.", e);
		}
		return ClientType.UNKNOW;
	}

	/**
	 * 通过手机号码，获取用户信息，可能获取不到
	 * 
	 * @param phoneId
	 * @return
	 */
	public UserBean getUserInfoByPhoneId(String phoneId) {
		UserBean userBean = new UserBean();
		try {
			String phoneKey = RedisKeyUtils.getUserPhoneKey(phoneId);
			Map<String, String> phoneMap = userDao.getPhoneInfoMap(phoneKey);
			if (phoneMap != null) {
				String userId = phoneMap.get(UserKey.userId);

				String userKey = RedisKeyUtils.getUserIdKey(userId);
				Map<String, String> userInfoMap = userDao.getUserInfoMap(userKey);
				userBean.setUserIdPrik(userInfoMap.get(UserKey.userIdPrik));
				userBean.setUserIdPubk(userInfoMap.get(UserKey.userIdPubk));
				userBean.setUserId(userId);// globalUserId
			}

		} catch (Exception e) {
			logger.error("getUserInfo by phoneid error.", e);
		}
		return userBean;
	}

	public String getUserIdPubkByPhoneId(String phoneId) {
		try {
			String phoneKey = RedisKeyUtils.getUserPhoneKey(phoneId);
			Map<String, String> phoneMap = userDao.getPhoneInfoMap(phoneKey);
			if (phoneMap != null) {
				String userId = phoneMap.get(UserKey.userId);
				String userIdKey = RedisKeyUtils.getUserIdKey(userId);
				return userDao.hget(userIdKey, UserKey.userIdPubk);
			}

		} catch (Exception e) {
			logger.error("get userIdPubk by phoneid error.", e);
		}
		return null;
	}
}
