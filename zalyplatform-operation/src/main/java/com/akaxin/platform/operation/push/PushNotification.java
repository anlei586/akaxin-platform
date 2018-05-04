package com.akaxin.platform.operation.push;

import com.akaxin.platform.operation.push.apns.ApnsPackage;
import com.akaxin.platform.operation.push.apns.PushApnsNotification;
import com.akaxin.platform.operation.push.umeng.PushUmengNotification;
import com.akaxin.platform.operation.push.umeng.UmengPackage;
import com.akaxin.platform.operation.push.xiaomi.PushXiaomiNotification;
import com.akaxin.platform.operation.push.xiaomi.XiaomiPackage;

public class PushNotification {
	/**
	 * 发送APNs消息通知PUSH
	 * 
	 * @param pack
	 */
	public static void pushAPNsNotification(ApnsPackage pack) {
		PushApnsNotification.getInstance().pushNotification(pack);
	}

	/**
	 * 发送XiaoMi消息通知PUSH
	 * 
	 * @param pack
	 */
	public static void pushXiaomiNotification(XiaomiPackage pack) {
		PushXiaomiNotification.getInstance().pushNotification(pack);
	}

	public static void pushUMengNotification(UmengPackage pack) {
		PushUmengNotification.getInstance().pushNotification(pack);
	}

}
