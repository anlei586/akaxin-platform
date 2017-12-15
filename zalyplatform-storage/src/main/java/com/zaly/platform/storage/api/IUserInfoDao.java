package com.zaly.platform.storage.api;

import java.util.Map;

import com.zaly.platform.storage.bean.RealNameUserBean;
import com.zaly.platform.storage.bean.UserInfoBean;

public interface IUserInfoDao {
	public boolean saveUserInfo(UserInfoBean bean);

	public boolean updateRealUserInfo(RealNameUserBean bean);

	Map<String, String> getPhoneInfoByPhone(String phoneId);

	Map<String, String> getUserInfoByUserId(String userID);
}
