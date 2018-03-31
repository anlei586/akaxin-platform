package com.akaxin.platform.operation.business.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.storage.service.SessionDaoService;

/**
 * 用户登陆会生成sessionid，并且用户每次api请求，会通过sessionid验证请求的合法性
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-18 17:49:10
 */
public class SessionDao {
	private static final Logger logger = LoggerFactory.getLogger(SessionDao.class);
	private SessionDaoService session = new SessionDaoService();

	private SessionDao() {

	}

	private static class SingletonHolder {
		private static SessionDao instance = new SessionDao();
	}

	public static SessionDao getInstance() {
		return SingletonHolder.instance;
	}

	public boolean addSessionMap(String key, Map<String, String> map, int expireSec) {
		try {
			return session.addSession(key, map, expireSec);
		} catch (Exception e) {
			logger.error("add session errror. map={}" + map, e);
		}
		return false;
	}

	public Map<String, String> getSessionMap(String key) {
		try {
			return session.getSessionMap(key);
		} catch (Exception e) {
			logger.error("get session map error.", e);
		}
		return null;
	}

	public boolean deleteSessionKey(String key) {
		try {
			return session.deleteSession(key);
		} catch (Exception e) {
			logger.error("delete session key error,key=" + key, e);
		}
		return false;
	}

}
