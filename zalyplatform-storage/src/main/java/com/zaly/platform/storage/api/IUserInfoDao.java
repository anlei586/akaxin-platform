package com.zaly.platform.storage.api;

import java.util.Map;

import com.zaly.platform.storage.bean.PushTokenBean;
import com.zaly.platform.storage.bean.UserBean;
import com.zaly.platform.storage.bean.UserRealNameBean;

public interface IUserInfoDao {
	public boolean saveUserInfo(UserBean bean);

	public boolean updateRealUserInfo(UserRealNameBean bean);

	Map<String, String> getPhoneInfoByPhone(String phoneId);

	Map<String, String> getUserInfoByUserId(String userID);

	public String getUserPhoneId(String userId);

	public String getPhoneGlobalRoaming(String phoneId);

	public PushTokenBean getPushToken(String userId);
}
