package com.akaxin.platform.storage.service;

import java.util.Map;

import com.akaxin.platform.storage.impl.redis.RedisUserTokenDao;

public class UserTokenDaoService {

	public boolean addUserToken(String key, Map<String, String> map) {
		return RedisUserTokenDao.getInstance().addUserToken(key, map);
	}

	public boolean addUserToken(String key, String field, String value) {
		return RedisUserTokenDao.getInstance().addUserToken(key, field, value);
	}

	public String getUserTokenValue(String key, String field) {
		return RedisUserTokenDao.getInstance().getUserTokenField(key, field);
	}
}
