package com.akaxin.platform.operation.bean;

public class SmsResult {
	private int result;
	private String errMsg;

	public SmsResult(int result, String errMsg) {
		this.result = result;
		this.errMsg = errMsg;
	}

	public boolean isSuccess() {
		return result == 0;
	}

	public String getErrMsg() {
		return errMsg;
	}
}