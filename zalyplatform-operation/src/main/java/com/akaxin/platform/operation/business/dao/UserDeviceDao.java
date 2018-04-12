package com.akaxin.platform.operation.business.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.common.utils.StringHelper;
import com.akaxin.platform.storage.service.DeviceDaoService;

/**
 * 用户设备相关信息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-18 17:49:10
 */
public class UserDeviceDao {
	private static final Logger logger = LoggerFactory.getLogger(UserDeviceDao.class);
	private DeviceDaoService deviceDaoService = new DeviceDaoService();

	private UserDeviceDao() {

	}

	private static class SingletonHolder {
		private static UserDeviceDao instance = new UserDeviceDao();
	}

	public static UserDeviceDao getInstance() {
		return SingletonHolder.instance;
	}

	public boolean addDevicemap(String key, Map<String, String> map) {
		try {
			return deviceDaoService.addDeviceMap(key, map);
		} catch (Exception e) {
			logger.error(StringHelper.format("add device map errror. map={}", map), e);
		}
		return false;
	}

	public Map<String, String> getDeviceMap(String key) {
		try {
			return deviceDaoService.getDeviceMap(key);
		} catch (Exception e) {
			logger.error("get device map error.", e);
		}
		return null;
	}

	public String getDeviceField(String key, String field) {
		try {
			return deviceDaoService.getDeviceField(key, field);
		} catch (Exception e) {
			logger.error(StringHelper.format("get device field error,key={} field={}", key, field), e);
		}
		return null;
	}

}
