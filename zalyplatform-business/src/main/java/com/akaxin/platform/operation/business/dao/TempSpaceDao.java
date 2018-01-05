package com.akaxin.platform.operation.business.dao;

import com.zaly.platform.storage.api.ITempSpaceDao;
import com.zaly.platform.storage.service.TempSpaceDaoService;

public class TempSpaceDao {

	private static TempSpaceDao instance = new TempSpaceDao();
	private ITempSpaceDao tempSpaceDao = new TempSpaceDaoService();

	public static TempSpaceDao getInstance() {
		return instance;
	}

	public boolean applyTempSpace(String key, String value, int expireTime) {
		boolean result = false;
		try {
			result = tempSpaceDao.setStringValue(key, value, expireTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getTempValue(String keyName) {
		try {
			return tempSpaceDao.getStringValue(keyName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
