package com.akaxin.platform.operation.push.xiaomi;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.common.utils.StringHelper;
import com.xiaomi.xmpush.server.Message;

public class PushXiaomiNotification {
	private static final Logger logger = LoggerFactory.getLogger(PushXiaomiNotification.class);
	private static final String APP_PACKAGE_NAME = "com.akaxin.client";
	private static final String APP_PACKAGE_NAME_DEBUG = "com.akaxin.client.debug";
	private static final String SANBOX_PRE = "dev_";

	private PushXiaomiNotification() {

	}

	private static class SingletonHolder {
		private static PushXiaomiNotification instance = new PushXiaomiNotification();
	}

	public static PushXiaomiNotification getInstance() {
		return SingletonHolder.instance;
	}

	public void pushNotification(XiaomiPackage xiaomiPack) {
		try {
			String xiaomiToken = xiaomiPack.getPushToken();
			boolean isSandbox = false;
			if (StringUtils.isNotEmpty(xiaomiToken) && xiaomiToken.startsWith(SANBOX_PRE)) {
				isSandbox = true;
				xiaomiToken = xiaomiToken.substring(4, xiaomiToken.length());
				xiaomiPack.setRestrictedPackageName(APP_PACKAGE_NAME_DEBUG);
			}
			Message message = xiaomiPack.buildMessage();
			XiaomiPushManager.pushMessage(xiaomiToken, message, isSandbox);
		} catch (Exception e) {
			logger.error(StringHelper.format("send xiaomi push error", xiaomiPack.toString()), e);
		}
	}

}
