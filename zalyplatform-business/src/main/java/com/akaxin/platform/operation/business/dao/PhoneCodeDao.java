package com.akaxin.platform.operation.business.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaly.platform.storage.api.IPhoneDao;
import com.zaly.platform.storage.service.PhoneDaoService;

/**
 * 用户手机验证码相关
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-16 14:31:57
 */
public class PhoneCodeDao {
	private static final Logger logger = LoggerFactory.getLogger(PhoneCodeDao.class);
	private static PhoneCodeDao instance = new PhoneCodeDao();
	private IPhoneDao phoneDao = new PhoneDaoService();

	public static PhoneCodeDao getInstance() {
		return instance;
	}

	/**
	 * 申请设置手机code
	 * 
	 * @param phoneId
	 * @param value
	 * @param expireTime
	 * @return
	 */
	public boolean setPhoneCode(String phoneId, String value, int expireTime) {
		boolean result = false;
		try {
			result = phoneDao.setStringValue(phoneId, value, expireTime);
		} catch (Exception e) {
			logger.error("apply phone verrify code error", e);
		}
		return result;
	}

	/**
	 * 获取设置的邀请码，用于核对与用户提供的是否匹配
	 * 
	 * @param keyName
	 * @return
	 */
	public String getPhoneCode(String keyName) {
		try {
			return phoneDao.getStringValue(keyName);
		} catch (Exception e) {
			logger.error("get verify code error keyName=" + keyName, e);
		}
		return null;
	}

}
