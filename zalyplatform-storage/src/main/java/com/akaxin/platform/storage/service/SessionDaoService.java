package com.akaxin.platform.storage.service;

import java.util.Map;

import com.akaxin.platform.storage.impl.redis.RedisSessionDao;

public class SessionDaoService {
	private RedisSessionDao sessionDao = new RedisSessionDao();

	public boolean addSession(String key, Map<String, String> map) {
		return sessionDao.addSession(key, map);
	}

	public Map<String, String> getSessionMap(String key) {
		return sessionDao.getSessionMap(key);
	}
}
