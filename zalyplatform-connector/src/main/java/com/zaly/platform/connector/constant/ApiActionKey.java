package com.zaly.platform.connector.constant;

public enum ApiActionKey {
	none(0), //
	uploadUserInfo(1), //
	sendUserPush(2);//

	private int index;

	ApiActionKey(int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}
}
