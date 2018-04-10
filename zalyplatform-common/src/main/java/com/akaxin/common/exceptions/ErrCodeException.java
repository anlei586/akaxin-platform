package com.akaxin.common.exceptions;

import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.constant.IErrorCode;

public class ErrCodeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IErrorCode errCode;

	public ErrCodeException(ErrorCode errCode) {
		super(errCode.toString());
		this.errCode = errCode;
	}

	public IErrorCode getErrCode() {
		return this.errCode;
	}
}
