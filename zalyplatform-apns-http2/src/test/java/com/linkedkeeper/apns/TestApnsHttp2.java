package com.linkedkeeper.apns;

import java.io.FileInputStream;
import java.util.concurrent.Future;

import com.akaxin.platform.push.client.ApnsHttp2Client;
import com.akaxin.platform.push.client.IApnsHttp2Client;
import com.akaxin.platform.push.constant.ApnsHttp2Config;
import com.akaxin.platform.push.notification.IApnsPushNotification;
import com.akaxin.platform.push.notification.IApnsPushNotificationResponse;
import com.akaxin.platform.push.notification.Payload;

/**
 * @author frank@linkedkeeper.com on 2016/12/28.
 */
public class TestApnsHttp2 {
	static final String AKAXIN_FILE = "/Users/anguoyue/Workspace/eclipse-oxygen/akaxin-apns-http2/src/main/resources/akaxin-sanbox-certificates.p12";
	static final boolean product = true;

	static final String pwd = "thinkpad";
//	static final String apnsToken = "1d4352e14520d751d12c7f434f22271f2f039f97eb8e36da7b60947957ea2e96";
	static final String apnsToken = "d7af5ebf275dd91f76588e6f45065d91938a4aed01858de2e52f78e3416fd231";

	public static void main(String[] args) throws Exception {
		try {
			ApnsHttp2Config config = new ApnsHttp2Config();
			config.setPassword(pwd);
			config.setKeyStoreStream(new FileInputStream(AKAXIN_FILE));
			config.setSandboxEnvironment(true);
			config.setPoolSize(2);

			IApnsHttp2Client client = new ApnsHttp2Client(config);

			long startTIme = System.currentTimeMillis();
			String payload = Payload.newPayload().addAlertBody("akaxin,你收到一条新消息  ").addBadge(1).build();
			System.out.println("payload=" + payload);
			Future<IApnsPushNotificationResponse<IApnsPushNotification>> response = client.pushMessageAsync(apnsToken,
					payload);
			IApnsPushNotificationResponse<IApnsPushNotification> notification = response.get();
			boolean success = notification.isSuccess();
			long endTIme = System.currentTimeMillis();
			System.out.println("result=" + success + "   cost=" + (endTIme - startTIme));
			System.out.println(notification);
			Thread.sleep(1000);

			client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
