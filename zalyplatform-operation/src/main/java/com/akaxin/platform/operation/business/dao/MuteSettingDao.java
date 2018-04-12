package com.akaxin.platform.operation.business.dao;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.common.utils.ServerAddress;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.service.SettingDaoService;

/**
 * <pre>
 * 用户静音设置相关的数据库业务层操作
 * 
 * 默认0：非静音状态 
 * 		1:表示静音 
 * 		0:表示非静音状态
 *  		-1:表示异常
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-16 15:51:01
 */
public class MuteSettingDao {
	private static final Logger logger = LoggerFactory.getLogger(MuteSettingDao.class);
	private SettingDaoService settingDao = new SettingDaoService();

	private MuteSettingDao() {
	}

	private static class SingletonHolder {
		private static MuteSettingDao instance = new MuteSettingDao();
	}

	public static MuteSettingDao getInstance() {
		return SingletonHolder.instance;
	}

	public boolean checkSiteMute(String globalUserId, ServerAddress siteAddress) {
		int muteNum = getSiteMute(globalUserId, siteAddress);
		if (muteNum == 1) {
			return true;
		}
		return false;
	}

	/**
	 * <pre>
	 * 默认false：非静音状态 
	 * 	1:表示静音 
	 * 	0:表示打开 
	 *  -1:表示异常
	 * </pre>
	 */
	public int getSiteMute(String globalUserId, ServerAddress siteAddress) {
		try {
			String addressField = siteAddress.getAddress();
			if (StringUtils.isNoneEmpty(addressField)) {
				String redisKey = RedisKeyUtils.getUserMuteKey(globalUserId);
				String muteStr = settingDao.getSetting(redisKey, addressField);
				if (StringUtils.isNotEmpty(muteStr)) {
					return Integer.valueOf(muteStr);
				} else {
					return 0;
				}
			}
		} catch (Exception e) {
			logger.error("add session errror. user=" + globalUserId + " siteAddress={}" + siteAddress.getAddress(), e);
		}
		return -1;
	}

	public boolean updateSiteMute(String globalUserId, ServerAddress siteAddress, boolean mute) {
		try {
			String addressField = siteAddress.getAddress();
			if (StringUtils.isNotEmpty(addressField)) {
				String redisKey = RedisKeyUtils.getUserMuteKey(globalUserId);
				if (mute) {// 是静音
					return settingDao.addSetting(redisKey, addressField, "1");
				} else {
					return settingDao.delSetting(redisKey, addressField);
				}
			}
		} catch (Exception e) {
			logger.error("get session map error.", e);
		}
		return false;
	}

}
