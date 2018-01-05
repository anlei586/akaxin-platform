package com.akaxin.platform.operation.business.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaly.platform.storage.api.IPhoneDao;
import com.zaly.platform.storage.service.PhoneDaoService;

public class UserPhoneDao {
	private static final Logger logger = LoggerFactory.getLogger(UserPhoneDao.class);
	private static UserPhoneDao instance = new UserPhoneDao();
	private IPhoneDao phoneDao = new PhoneDaoService();

	public static UserPhoneDao getInstance() {
		return instance;
	}

	public boolean applyPhoneVerifyCode(String phoneId, String value, int expireTime) {
		boolean result = false;

		try {
			result = phoneDao.setStringValue(phoneId, value, expireTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getVerifyCode(String keyName) {
		try {
			return phoneDao.getStringValue(keyName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
