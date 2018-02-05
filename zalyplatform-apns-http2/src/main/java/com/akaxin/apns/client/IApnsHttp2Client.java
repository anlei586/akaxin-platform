package com.akaxin.apns.client;

import com.akaxin.apns.notification.IApnsPushNotification;
import com.akaxin.apns.notification.IApnsPushNotificationResponse;

import io.netty.util.concurrent.Future;

public interface IApnsHttp2Client {

	IApnsPushNotificationResponse<IApnsPushNotification> pushMessageSync(String token, String payload, int timeout)
			throws Exception;

	Future<IApnsPushNotificationResponse<IApnsPushNotification>> pushMessageAsync(String token, String payload)
			throws Exception;

	void disconnect() throws Exception;
}
