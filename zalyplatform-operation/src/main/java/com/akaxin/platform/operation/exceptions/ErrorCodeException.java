package com.akaxin.platform.operation.exceptions;

import com.akaxin.common.constant.ErrorCode;

public class ErrorCodeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ErrorCode errCode;

	public ErrorCodeException(ErrorCode errCode) {
		super(errCode.toString());
	}

	public ErrorCode getErrCode() {
		return this.errCode;
	}
}
