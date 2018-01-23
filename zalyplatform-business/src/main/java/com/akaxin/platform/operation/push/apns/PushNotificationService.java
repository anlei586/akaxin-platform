package com.akaxin.platform.operation.push.apns;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.apns.client.IApnsHttp2Client;
import com.akaxin.apns.notification.IApnsPushNotification;
import com.akaxin.apns.notification.IApnsPushNotificationResponse;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-23 18:17:29
 */
public class PushNotificationService {
	private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
	private static final boolean isSandboxEnv = true;

	private static class SingletonHolder {
		private static PushNotificationService instance = new PushNotificationService();
	}

	public static PushNotificationService getInstance() {
		return SingletonHolder.instance;
	}

	private PushNotificationService() {

	}

	public boolean disconnectAPNs() {
		try {
			IApnsHttp2Client apnsHttp2Client = APNsPushManager.getInstance().getApnsClient(false);
			apnsHttp2Client.disconnect();
			return true;
		} catch (Exception e) {
			logger.error("disconnct apns connection error", e);
		}
		return false;
	}

	public boolean apnsPushNotification(ApnsPackage apnsPack) {
		logger.info("token={} payload={}", apnsPack.getToken(), apnsPack.buildPayloadJson());
		return sendPayload(apnsPack.getToken(), apnsPack.buildPayloadJson());
	}

	private boolean sendPayload(String apnsToken, String payload) {
		try {
			IApnsHttp2Client apnsHttp2Client = APNsPushManager.getInstance().getApnsClient(isSandboxEnv);
			Future<IApnsPushNotificationResponse<IApnsPushNotification>> response = apnsHttp2Client
					.pushMessageAsync(apnsToken, payload);
			logger.info("send payload response={}", response.get().getApnsPushNotification());
			return true;
		} catch (Exception e) {
			logger.error("send payload error");
		}
		return false;
	}

}
