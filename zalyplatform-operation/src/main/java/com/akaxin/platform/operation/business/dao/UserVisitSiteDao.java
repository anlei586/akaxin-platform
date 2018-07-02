package com.akaxin.platform.operation.business.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.storage.service.UserSiteDaoService;

/**
 * <pre>
 * 用户访问的站点
 * 	 0:匿名站点
 *   1:实名站点
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-19 01:14:11
 */
public class UserVisitSiteDao {
	private static final Logger logger = LoggerFactory.getLogger(UserVisitSiteDao.class);

	private static final String REALNAME_SITE = "1";

	private UserSiteDaoService userSiteDao = new UserSiteDaoService();

	private UserVisitSiteDao() {
	}

	private static class SingletonHolder {
		private static UserVisitSiteDao instance = new UserVisitSiteDao();
	}

	public static UserVisitSiteDao getInstance() {
		return SingletonHolder.instance;
	}

	public boolean isUserRealNameSite(String key, String siteAddress) {
		try {
			String siteValue = userSiteDao.getUserSite(key, siteAddress);
			if (REALNAME_SITE.equals(siteValue)) {
				return true;
			}
		} catch (Exception e) {
			logger.error("add user token errror.", e);
		}
		return false;
	}

	// 设置站点访问的是实名站点
	public boolean setRealNameSite(String key, String siteAddress) {
		try {
			return userSiteDao.addUserSite(key, siteAddress, REALNAME_SITE);
		} catch (Exception e) {
			logger.error("set user site to realname site errror.", e);
		}
		return false;
	}

}
