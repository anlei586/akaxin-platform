package com.akaxin.platform.operation.business.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.proto.core.ClientProto;
import com.akaxin.proto.core.ClientProto.ClientType;
import com.zaly.platform.storage.api.IUserInfoDao;
import com.zaly.platform.storage.bean.UserBean;
import com.zaly.platform.storage.constant.UserKey;
import com.zaly.platform.storage.service.UserInfoDaoService;

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
			return userDao.saveUserInfo(userBean);
		} catch (Exception e) {
			logger.error("save user info error.", e);
		}
		return false;
	}

	public boolean updateUserInfo(String userId, Map<String, String> map) {
		try {
			String key = RedisKeyUtils.getUserInfoKey(userId);
			return userDao.updateUserInfo(key, map);
		} catch (Exception e) {
			logger.error("update user info error.", e);
		}
		return false;
	}

	public boolean updateUserField(String userId, String field, String value) {
		try {
			String redisKey = RedisKeyUtils.getUserInfoKey(userId);
			return userDao.hset(redisKey, field, value);
		} catch (Exception e) {
			logger.error("update user info error.", e);
		}
		return false;
	}

	public boolean updateRealNameInfo(UserBean userBean) {
		try {
			return userDao.updateRealNameInfo(userBean);
		} catch (Exception e) {
			logger.error("update rean user info error.", e);
		}
		return false;
	}

	public UserBean getRealNameInfo(String phoneId) {
		UserBean userBean = new UserBean();
		try {
			Map<String, String> phoneMap = userDao.getPhoneInfoByPhone(phoneId);
			userBean.setUserId(phoneMap.get(UserKey.userId));

			Map<String, String> userInfoMap = userDao.getUserInfoByUserId(userBean.getUserId());

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
			return userDao.getUserPhoneId(userId);
		} catch (Exception e) {
			logger.error("get phone id error.userIdf=" + userId, e);
		}
		return null;
	}

	public String getPhoneGlobalRoaming(String phoneId) {
		try {
			return userDao.getPhoneGlobalRoaming(phoneId);
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
			ClientProto.ClientType clientType = ClientProto.ClientType.forNumber(Integer.valueOf(type));
			return clientType;
		} catch (NumberFormatException e) {
			logger.error("get client type error.", e);
		}
		return ClientType.UNKNOW;
	}

}
