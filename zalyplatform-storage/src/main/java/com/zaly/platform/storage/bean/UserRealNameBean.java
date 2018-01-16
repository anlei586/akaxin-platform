package com.zaly.platform.storage.bean;

import com.akaxin.common.utils.GsonUtils;

public class UserRealNameBean extends UserBean {
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
