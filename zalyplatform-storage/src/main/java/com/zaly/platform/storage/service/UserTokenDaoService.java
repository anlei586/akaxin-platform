package com.zaly.platform.storage.service;

import java.util.Map;

import com.zaly.platform.storage.impl.redis.RedisUserTokenDao;

public class UserTokenDaoService {
	private RedisUserTokenDao userTokenDao = new RedisUserTokenDao();

	public boolean addUserToken(String key, Map<String, String> map) {
		return userTokenDao.addUserToken(key, map);
	}

	public boolean addUserToken(String key, String field, String value) {
		return userTokenDao.addUserToken(key, field, value);
	}

	public String getUserTokenValue(String key, String field) {
		return userTokenDao.getUserTokenField(key, field);
	}
}
