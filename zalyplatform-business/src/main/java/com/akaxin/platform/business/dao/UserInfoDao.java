package com.akaxin.platform.business.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaly.platform.storage.api.IUserInfoDao;
import com.zaly.platform.storage.bean.RealNameUserBean;
import com.zaly.platform.storage.bean.UserInfoBean;
import com.zaly.platform.storage.constant.UserInfoKey;
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

	public boolean uploadUserInfo(UserInfoBean userBean) {
		try {
			return userDao.saveUserInfo(userBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateRealUserInfo(RealNameUserBean userBean) {
		try {
			return userDao.updateRealUserInfo(userBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public RealNameUserBean getRealUserInfo(String phoneId) {
		RealNameUserBean userBean = new RealNameUserBean();
		try {
			Map<String, String> phoneMap = userDao.getPhoneInfoByPhone(phoneId);
			userBean.setUserId(phoneMap.get(UserInfoKey.userId));
			userBean.setPassword(phoneMap.get(UserInfoKey.userPassword));

			Map<String, String> userInfoMap = userDao.getUserInfoByUserId(userBean.getUserId());

			userBean.setUserIdPrik(userInfoMap.get(UserInfoKey.userIdPrik));
			userBean.setUserIdPubk(userInfoMap.get(UserInfoKey.userIdPubk));

		} catch (Exception e) {
			logger.error("getRealUserInfo by phoneid error.", e);
		}
		return userBean;
	}
}
