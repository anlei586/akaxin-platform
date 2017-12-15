package com.zaly.platform.storage.service;

import com.zaly.platform.storage.api.IPhoneDao;
import com.zaly.platform.storage.impl.redis.RedisPhoneDao;
import com.zaly.platform.storage.impl.redis.RedisTempSpaceDao;

public class PhoneDaoService implements IPhoneDao {

	@Override
	public boolean setStringValue(String key, String value, int expireTime) {
		return RedisPhoneDao.getInstance().setStringValue(key, value, expireTime);
	}

	@Override
	public String getStringValue(String key) {
		return RedisTempSpaceDao.getInstance().getStringValue(key);
	}
}
