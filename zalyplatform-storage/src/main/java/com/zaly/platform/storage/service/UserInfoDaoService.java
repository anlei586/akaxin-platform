package com.zaly.platform.storage.service;

import java.util.Map;

import com.zaly.platform.storage.api.IUserInfoDao;
import com.zaly.platform.storage.bean.UserBean;
import com.zaly.platform.storage.impl.redis.RedisUserInfoDao;

public class UserInfoDaoService implements IUserInfoDao {

	@Override
	public boolean hset(String key, String field, String value) {
		return RedisUserInfoDao.getInstance().hset(key, field, value);
	}

	@Override
	public String hget(String key, String field) {
		return RedisUserInfoDao.getInstance().hget(key, field);
	}

	@Override
	public boolean saveUserInfo(UserBean bean) {
		return RedisUserInfoDao.getInstance().saveUserInfo(bean);
	}

	@Override
	public boolean updateUserInfo(String key, Map<String, String> map) {
		return RedisUserInfoDao.getInstance().updateUserInfo(key, map);
	}

	@Override
	public boolean updateRealNameInfo(UserBean bean) {
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

	@Override
	public String getUserPhoneId(String userId) {
		return RedisUserInfoDao.getInstance().getUserPhoneId(userId);
	}

	@Override
	public String getPhoneGlobalRoaming(String phoneId) {
		return RedisUserInfoDao.getInstance().getPhoneGlobalRoaming(phoneId);
	}

}
