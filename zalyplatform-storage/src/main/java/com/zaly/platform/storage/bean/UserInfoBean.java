package com.zaly.platform.storage.bean;

import com.akaxin.common.utils.GsonUtils;

public class UserInfoBean {
	private String userId;
	private String userIdPrik;
	private String userIdPubk;
	private String userPhoto;
	private String userName;
	private String userPhoneId;
	private String clientType;
	private String rom;
	private String pushToken;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserIdPrik() {
		return userIdPrik;
	}

	public void setUserIdPrik(String userIdPrik) {
		this.userIdPrik = userIdPrik;
	}

	public String getUserIdPubk() {
		return userIdPubk;
	}

	public void setUserIdPubk(String userIdPubk) {
		this.userIdPubk = userIdPubk;
	}

	public String getUserPhoto() {
		return userPhoto;
	}

	public void setUserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhoneId() {
		return userPhoneId;
	}

	public void setUserPhoneId(String userPhoneId) {
		this.userPhoneId = userPhoneId;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getRom() {
		return rom;
	}

	public void setRom(String rom) {
		this.rom = rom;
	}

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
