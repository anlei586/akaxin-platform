package com.akaxin.platform.storage.bean;

import com.akaxin.platform.common.utils.GsonUtils;

public class UserBean {
	private String userId;
	private String userIdPrik;
	private String userIdPubk;
	private String deviceId;
	private String phoneId;
	private String phoneRoaming;
	private int clientType;
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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getPhoneId() {
		return phoneId;
	}

	public void setPhoneId(String phoneId) {
		this.phoneId = phoneId;
	}

	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
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

	public String getPhoneRoaming() {
		return phoneRoaming;
	}

	public void setPhoneRoaming(String phoneRoaming) {
		this.phoneRoaming = phoneRoaming;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
