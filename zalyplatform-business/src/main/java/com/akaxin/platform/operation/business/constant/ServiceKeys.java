package com.akaxin.platform.operation.business.constant;

public enum ServiceKeys {
	None(0, "none"), //
	API_USER(1, "user"), //
	API_PHONE(2, "phone"), //
	API_TEMP_SPACE(3, "temp"), //
	API_PUSH(50, "push");//

	private int index;
	private String name;

	ServiceKeys(int index, String name) {
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
