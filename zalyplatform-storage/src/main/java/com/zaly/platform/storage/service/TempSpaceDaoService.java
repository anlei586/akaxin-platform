package com.zaly.platform.storage.service;

import com.zaly.platform.storage.api.ITempSpaceDao;
import com.zaly.platform.storage.impl.redis.RedisTempSpaceDao;

public class TempSpaceDaoService implements ITempSpaceDao {

	@Override
	public boolean setStringValue(String key, String value, int expireTime) {
		return RedisTempSpaceDao.getInstance().setStringValue(key, value, expireTime);
	}

	@Override
	public String getStringValue(String key) {
		return RedisTempSpaceDao.getInstance().getStringValue(key);
	}
}
