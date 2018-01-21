package com.akaxin.platform.operation.business.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaly.platform.storage.service.UserTokenDaoService;

/**
 * 客户端授权用户凭证
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-19 01:14:11
 */
public class UserTokenDao {
	private static final Logger logger = LoggerFactory.getLogger(UserTokenDao.class);
	private UserTokenDaoService userTokenDao = new UserTokenDaoService();

	private UserTokenDao() {
	}

	private static class SingletonHolder {
		private static UserTokenDao instance = new UserTokenDao();
	}

	public static UserTokenDao getInstance() {
		return SingletonHolder.instance;
	}

	public boolean addUserToken(String key, Map<String, String> map) {
		try {
			return userTokenDao.addUserToken(key, map);
		} catch (Exception e) {
			logger.error("add user token errror.", e);
		}
		return false;
	}

	public boolean addUserToken(String key, String field, String value) {
		try {
			return userTokenDao.addUserToken(key, field, value);
		} catch (Exception e) {
			logger.error("add user token errror.", e);
		}
		return false;
	}

	public String getUserToken(String key, String field) {
		try {
			return userTokenDao.getUserTokenValue(key, field);
		} catch (Exception e) {
			logger.error("get user token error.", e);
		}
		return null;
	}

}
