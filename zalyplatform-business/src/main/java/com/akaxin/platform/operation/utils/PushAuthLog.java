package com.akaxin.platform.operation.utils;

import org.apache.log4j.PatternLayout;

import com.akaxin.common.logs.LogCreater;
import com.akaxin.common.logs.LogUtils;

public class PushAuthLog {
	private final String CONVERSION_PATTERN = "[%p] %d [%c] \\r\\n\\t%m%n";
	private final org.apache.log4j.Logger pushAuthlogger = LogCreater.createLogger("sms", null,
			new PatternLayout(CONVERSION_PATTERN), false, true);

	private PushAuthLog() {

	}

	public static PushAuthLog getInstance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {
		private static PushAuthLog instance = new PushAuthLog();
	}

	public void printLog(String messagePattern, Object... objects) {
		LogUtils.info(pushAuthlogger, messagePattern, objects);
	}
}