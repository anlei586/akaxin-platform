package com.akaxin.platform.operation.push.xiaomi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;

public class XiaomiPushManager {
	private static final Logger logger = LoggerFactory.getLogger(XiaomiPushManager.class);
	private static final String APP_SECRET_KEY = "S5BPUWhx4v7F3bxMHoaOfA==";
	private static final String APP_SECRET_KEY_DEBUG = "m3s/b/KvbKjSZaep1zE2Zw==";

	// private static Sender sandboxSender;
	// private static Sender officialSender;

	public static void pushMessage(String token, Message message, boolean isSandbox) throws Exception {
		System.out.println("token=" + token + " issanbox=" + isSandbox);
		System.out.println("message=" + message);

		Sender sender = null;
		Constants.useOfficial();
		if (isSandbox) {
			Constants.useOfficial();
			sender = new Sender(APP_SECRET_KEY_DEBUG);
		} else {
			sender = new Sender(APP_SECRET_KEY);
		}
		Result result = sender.send(message, token, 1); // 根据regID，发送消息到指定设备上，不重试。
		System.out.println("result=" + result);
		logger.info("send xiaomi push result={}", result);
	}

	/**
	 * 批量发送模式
	 * 
	 * @throws Exception
	 */
	public static void pushTargetedMessage(Message mesage) throws Exception {
		// Constants.useOfficial();
		// Sender sender = new Sender(APP_SECRET_KEY);
		// List<TargetedMessage> messages = new ArrayList<TargetedMessage>();
		// TargetedMessage targetedMessage1 = new TargetedMessage();
		// targetedMessage1.setTarget(TargetedMessage.TARGET_TYPE_ALIAS, "alias1");
		// String messagePayload1 = "This is a message1";
		// String title1 = "notification title1";
		// String description1 = "notification description1";
		// Message message1 = new
		// Message.Builder().title(title1).description(description1).payload(messagePayload1)
		// .restrictedPackageName(MY_PACKAGE_NAME).notifyType(1) // 使用默认提示音提示
		// .build();
		// targetedMessage1.setMessage(message1);
		// messages.add(targetedMessage1);
		//
		//
		// sender.send(messages, 0); // 根据alias，发送消息到指定设备上，不重试。
	}

}
