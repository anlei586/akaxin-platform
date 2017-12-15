package com.zaly.platform.storage.bean;

import com.zaly.common.utils.GsonUtils;

public class RealNameUserBean extends UserInfoBean {
	public String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
