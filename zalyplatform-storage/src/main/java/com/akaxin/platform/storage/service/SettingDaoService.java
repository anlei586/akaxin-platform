package com.akaxin.platform.storage.service;

import java.util.Map;

import com.akaxin.platform.storage.impl.redis.RedisSettingDao;

public class SettingDaoService {
	private RedisSettingDao settingDao = new RedisSettingDao();

	public boolean addSetting(String key, String field, String value) {
		return settingDao.addSetting(key, field, value);
	}

	public boolean addSettings(String key, Map<String, String> map) {
		return settingDao.addSettings(key, map);
	}

	public boolean delSetting(String key, String field) {
		return settingDao.delSetting(key, field);
	}

	public String getSetting(String key, String field) {
		return settingDao.getSetting(key, field);
	}

	public Map<String, String> getSessionMap(String key) {
		return settingDao.getSettingMap(key);
	}
}
