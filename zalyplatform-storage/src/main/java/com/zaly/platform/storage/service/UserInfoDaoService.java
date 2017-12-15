package com.zaly.platform.storage.service;

import java.util.Map;

import com.zaly.platform.storage.api.IUserInfoDao;
import com.zaly.platform.storage.bean.RealNameUserBean;
import com.zaly.platform.storage.bean.UserInfoBean;
import com.zaly.platform.storage.impl.redis.RedisUserInfoDao;

public class UserInfoDaoService implements IUserInfoDao {

	@Override
	public boolean saveUserInfo(UserInfoBean bean) {
		return RedisUserInfoDao.getInstance().saveUserInfo(bean);
	}

	@Override
	public boolean updateRealUserInfo(RealNameUserBean bean) {
		return RedisUserInfoDao.getInstance().updateRealUser(bean);
	}

	@Override
	public Map<String, String> getPhoneInfoByPhone(String phoneId) {
		return RedisUserInfoDao.getInstance().getPhoneInfoByPhone(phoneId);
	}

	@Override
	public Map<String, String> getUserInfoByUserId(String userID) {
		return RedisUserInfoDao.getInstance().getUserInfoByUserId(userID);
	}

}
