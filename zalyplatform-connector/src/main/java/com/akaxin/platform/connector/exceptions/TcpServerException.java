package com.akaxin.platform.connector.exceptions;

/**
 * 增加tcpserver异常
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-20 10:40:43
 */
public class TcpServerException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7366967641018300790L;

	public TcpServerException(String message) {
		super(message);
	}

	public TcpServerException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
