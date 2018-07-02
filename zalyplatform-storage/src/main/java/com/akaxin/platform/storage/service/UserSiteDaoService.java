package com.akaxin.platform.storage.service;

import java.util.Map;

import com.akaxin.platform.storage.impl.redis.RedisUserSiteDao;

/**
 * 用户访问的站点redis相关DAO服务
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-29 20:19:39
 */
public class UserSiteDaoService {

	public boolean addUserSite(String key, String siteAddress, String value) {
		return RedisUserSiteDao.getInstance().addUserSite(key, siteAddress, value);
	}

	public boolean addUserSites(String key, Map<String, String> map) {
		return RedisUserSiteDao.getInstance().addUserSites(key, map);
	}

	public String getUserSite(String key, String siteAddress) {
		return RedisUserSiteDao.getInstance().getUserSite(key, siteAddress);
	}

}
