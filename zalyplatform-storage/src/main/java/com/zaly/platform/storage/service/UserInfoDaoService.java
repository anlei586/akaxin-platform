package com.zaly.platform.storage.service;

import java.util.Map;

import com.zaly.platform.storage.api.IUserInfoDao;
import com.zaly.platform.storage.bean.UserBean;
import com.zaly.platform.storage.impl.redis.RedisUserInfoDao;

public class UserInfoDaoService implements IUserInfoDao {
	// common
	@Override
	public boolean hset(String key, String field, String value) {
		return RedisUserInfoDao.getInstance().hset(key, field, value);
	}

	@Override
	public String hget(String key, String field) {
		return RedisUserInfoDao.getInstance().hget(key, field);
	}

	// user
	@Override
	public boolean saveUserInfo(String key, UserBean bean) {
		return RedisUserInfoDao.getInstance().saveUserInfo(key, bean);
	}

	@Override
	public boolean updateUserInfo(String key, Map<String, String> map) {
		return RedisUserInfoDao.getInstance().updateUserInfo(key, map);
	}

	@Override
	public Map<String, String> getUserInfoMap(String key) {
		return RedisUserInfoDao.getInstance().getUserInfoMap(key);
	}

	// phone
	@Override
	public boolean updatePhoneInfo(String key, UserBean bean) {
		return RedisUserInfoDao.getInstance().updatePhoneInfo(key, bean);
	}

	@Override
	public Map<String, String> getPhoneInfoMap(String key) {
		return RedisUserInfoDao.getInstance().getPhoneInfoMap(key);
	}

}