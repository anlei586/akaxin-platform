package com.akaxin.platform.operation.push.apns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.push.client.ApnsHttp2Client;
import com.akaxin.platform.push.client.IApnsHttp2Client;
import com.akaxin.platform.push.constant.ApnsHttp2Config;

/**
 * platform与APNs服务交互管理，发送
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-23 17:12:51
 */
public class APNsPushManager {
	private static final Logger logger = LoggerFactory.getLogger(APNsPushManager.class);
	private static final String AKAXIN_PUSH_NAME = "AKAXIN_PUSH";
	private static final String PRODUCT_APPLE_CERT_FILE = "akaxin-push-certificates.p12";
	private static final String SANDBOX_APPLE_CERT_FILE = "akaxin-apns-development.p12";
	private static final int PRODUCT_MAX_CONN = 4;
	private static final int DEVELOP_MAX_CONN = 2;

	private static final String PASSWD = "123456";
	private static APNsPushManager instance;
	private static IApnsHttp2Client apnsHttp2Client;
	private static IApnsHttp2Client sandboxApnsHttp2Client;

	private APNsPushManager() {

	}

	public static APNsPushManager getInstance() {
		if (instance == null) {
			instance = new APNsPushManager();
		}
		return instance;
	}

	public void start() {
		try {
			// apnsHttp2Client = SingletonHolder.getApnsHttp2Client();
			sandboxApnsHttp2Client = SingletonHolder.getSandboxApnsHttp2Client();
			logger.info("start apns client client={} sandboxClient={}", apnsHttp2Client, sandboxApnsHttp2Client);
		} catch (Exception e) {
			logger.error("create apns client error", e);
		}
	}

	public IApnsHttp2Client getApnsClient(boolean isSandboxEnv) {
		return isSandboxEnv ? sandboxApnsHttp2Client : apnsHttp2Client;
	}

	private static class SingletonHolder {

		private static IApnsHttp2Client buildApnsHttp2Client(String certFileName, String password,
				int maxApnsConnections, boolean isSandboxEnv) {
			IApnsHttp2Client client = null;
			if (client == null) {
				try {
					ApnsHttp2Config apnsHttp2Config = new ApnsHttp2Config();
					apnsHttp2Config.setName(AKAXIN_PUSH_NAME);
					apnsHttp2Config.setKeyStoreStream(certFileName);
					apnsHttp2Config.setSandboxEnvironment(isSandboxEnv);
					apnsHttp2Config.setPassword(password);
					apnsHttp2Config.setPoolSize(maxApnsConnections);
					client = new ApnsHttp2Client(apnsHttp2Config);
				} catch (Exception e) {
					logger.error("build APNs Linked error", e);
				}
			}

			return client;
		}

		public static IApnsHttp2Client getApnsHttp2Client() {
			return buildApnsHttp2Client(PRODUCT_APPLE_CERT_FILE, PASSWD, PRODUCT_MAX_CONN, false);
		}

		public static IApnsHttp2Client getSandboxApnsHttp2Client() {
			return buildApnsHttp2Client(SANDBOX_APPLE_CERT_FILE, PASSWD, DEVELOP_MAX_CONN, true);
		}

	}

}
