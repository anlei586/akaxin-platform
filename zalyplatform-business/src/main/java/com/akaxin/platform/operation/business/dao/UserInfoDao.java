package com.akaxin.platform.operation.business.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaly.platform.storage.api.IUserInfoDao;
import com.zaly.platform.storage.bean.PushTokenBean;
import com.zaly.platform.storage.bean.UserBean;
import com.zaly.platform.storage.bean.UserRealNameBean;
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

	public boolean uploadUserInfo(UserBean userBean) {
		try {
			return userDao.saveUserInfo(userBean);
		} catch (Exception e) {
			logger.error("update user info error.", e);
		}
		return false;
	}

	public boolean updateRealUserInfo(UserRealNameBean userBean) {
		try {
			return userDao.updateRealUserInfo(userBean);
		} catch (Exception e) {
			logger.error("update rean user info error.", e);
		}
		return false;
	}

	public UserRealNameBean getRealUserInfo(String phoneId) {
		UserRealNameBean userBean = new UserRealNameBean();
		try {
			Map<String, String> phoneMap = userDao.getPhoneInfoByPhone(phoneId);
			userBean.setUserId(phoneMap.get(UserKey.userId));
			userBean.setPassword(phoneMap.get(UserKey.userPassword));

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

	public PushTokenBean getPushToken(String userId) {
		try {
			return userDao.getPushToken(userId);
		} catch (Exception e) {
			logger.error("get push token info error", e);
		}
		return null;
	}

}
