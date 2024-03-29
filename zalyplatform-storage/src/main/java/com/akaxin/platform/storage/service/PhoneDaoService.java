package com.akaxin.platform.storage.service;

import com.akaxin.platform.storage.api.IPhoneDao;
import com.akaxin.platform.storage.impl.redis.RedisPhoneDao;

public class PhoneDaoService implements IPhoneDao {

	@Override
	public boolean setStringValue(String key, String value, int expireTime) {
		return RedisPhoneDao.getInstance().setStringValue(key, value, expireTime);
	}

	@Override
	public String getStringValue(String key) {
		return RedisPhoneDao.getInstance().getStringValue(key);
	}

	@Override
	public long delStringValue(String key) {
		return RedisPhoneDao.getInstance().delStringValue(key);
	}
}
