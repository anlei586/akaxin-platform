package com.akaxin.platform.operation.exceptions;

import com.akaxin.common.constant.ErrorCode2;

public class RequestException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ErrorCode2 errCode;

	public RequestException(ErrorCode2 errCode) {
		super(errCode.toString());
	}

	public ErrorCode2 getErrCode() {
		return this.errCode;
	}
}
