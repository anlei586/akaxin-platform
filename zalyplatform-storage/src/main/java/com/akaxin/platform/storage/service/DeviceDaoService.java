package com.akaxin.platform.storage.service;

import java.util.Map;

import com.akaxin.platform.storage.impl.redis.RedisDeviceDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-31 17:15:26
 */
public class DeviceDaoService {
	private RedisDeviceDao deviceDao = new RedisDeviceDao();

	public boolean addDeviceMap(String key, Map<String, String> map) {
		return deviceDao.addDeviceMap(key, map);
	}

	public Map<String, String> getDeviceMap(String key) {
		return deviceDao.getDeviceMap(key);
	}

	public String getDeviceField(String key, String field) {
		return deviceDao.getDeviceField(key, field);
	}
}
