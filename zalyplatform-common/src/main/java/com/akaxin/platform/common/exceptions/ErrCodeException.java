package com.akaxin.platform.common.exceptions;

import com.akaxin.common.constant.IErrorCode;
import com.akaxin.platform.common.constant.ErrorCode;

public class ErrCodeException extends Throwable {
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
