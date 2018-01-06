package com.akaxin.platform.operation.business.constant;

public enum RequestKeys {
	None(0, "none"), //

	API_ACTION(1, "api"), //
	API_PLATFORM(2, "platform"), //
	API_USER(3, "user"), //
	API_PHONE(4, "phone"), //
	API_TEMP_SPACE(5, "temp"), //
	API_PUSH(50, "push"), //

	IM_ACTION(100, "im"), //
	IM_HELLO(101, "hello"), //
	IM_AUTH(102, "auth");//

	private int index;
	private String name;

	RequestKeys(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}
}
