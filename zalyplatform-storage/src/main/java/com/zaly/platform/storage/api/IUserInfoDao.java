package com.zaly.platform.storage.api;

import java.util.Map;

import com.zaly.platform.storage.bean.UserBean;

public interface IUserInfoDao {
	// common
	public boolean hset(String key, String field, String value);

	public String hget(String key, String field);

	// user
	public boolean saveUserInfo(String key, UserBean bean);

	boolean updateUserInfo(String key, Map<String, String> map);

	Map<String, String> getUserInfoMap(String key);

	// phone
	public boolean updatePhoneInfo(String key, UserBean bean);

	Map<String, String> getPhoneInfoMap(String key);

}
