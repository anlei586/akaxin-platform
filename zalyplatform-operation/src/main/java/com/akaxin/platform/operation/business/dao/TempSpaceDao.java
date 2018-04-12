package com.akaxin.platform.operation.business.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.common.utils.StringHelper;
import com.akaxin.platform.storage.api.ITempSpaceDao;
import com.akaxin.platform.storage.service.TempSpaceDaoService;

public class TempSpaceDao {
	private static final Logger logger = LoggerFactory.getLogger(TempSpaceDao.class);
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
			logger.info(StringHelper.format("apply temp space key={} value={} expireTime={}", key, value, expireTime),
					e);
		}
		logger.info(StringHelper.format("apply temp space key={} value={} expireTime={} result={}", key, value,
				expireTime, result));
		return result;
	}

	public String getTempValue(String keyName) {
		String value = null;
		try {
			value = tempSpaceDao.getStringValue(keyName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info(StringHelper.format("get temp space key={} value={}", keyName, value));
		return value;
	}
}
