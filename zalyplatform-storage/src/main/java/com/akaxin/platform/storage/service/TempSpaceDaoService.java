package com.akaxin.platform.storage.service;

import com.akaxin.platform.storage.api.ITempSpaceDao;
import com.akaxin.platform.storage.impl.redis.RedisTempSpaceDao;

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
