package com.akaxin.platform.operation.business.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.zaly.platform.storage.api.IPhoneDao;
import com.zaly.platform.storage.service.PhoneDaoService;

/**
 * <pre>
 * 1.用户手机验证码
 * 2.手机令牌（phone-token）
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-16 14:31:57
 */
public class PhoneVCTokenDao {
	private static final Logger logger = LoggerFactory.getLogger(PhoneVCTokenDao.class);
	private static PhoneVCTokenDao instance = new PhoneVCTokenDao();
	private IPhoneDao phoneDao = new PhoneDaoService();

	public static PhoneVCTokenDao getInstance() {
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
	public boolean applyPhoneToken(String key, String value, int expireTime) {
		boolean result = false;
		try {
			result = phoneDao.setStringValue(key, value, expireTime);
		} catch (Exception e) {
			logger.error("apply phone verrify code error", e);
		}
		return result;
	}

	/**
	 * 获取手机令牌
	 * 
	 * @param keyName
	 * @return
	 */
	public String getPhoneToken(String phoneToken) {
		try {
			return phoneDao.getStringValue(phoneToken);
		} catch (Exception e) {
			logger.error("get phoneToken error。 phoneToken=" + phoneToken, e);
		}
		return null;
	}

	// 设置手机验证码
	public boolean setPhoneVC(String phoneId, String value, int expireTime) {
		boolean result = false;
		try {
			String key = RedisKeyUtils.getPhoneVCKey(phoneId);
			result = phoneDao.setStringValue(key, value, expireTime);
		} catch (Exception e) {
			logger.error("apply phone verrify code error", e);
		}
		return result;
	}

	// 获取验证码
	public String getPhoneVC(String phoneId) {
		String key = RedisKeyUtils.getPhoneVCKey(phoneId);
		try {
			return phoneDao.getStringValue(key);
		} catch (Exception e) {
			logger.error("get verify code error keyName=" + key, e);
		}
		return null;
	}

}