package com.akaxin.platform.test;

import com.akaxin.platform.operation.push.PushNotification;
import com.akaxin.platform.operation.push.xiaomi.XiaomiPackage;

public class TestXiaoMiPush {
	public static void main(String[] args) {
		String token = "dev_UUEoUKrIvoOA6zabq0kzvKlllZqVyVLFyIk0ZIvz42M=";
		String token2 = "dev_y6cMid8EqimdiWknb8vQxi4E4N3cK3ghg4fFMAyx4UQ=";
		XiaomiPackage pac = new XiaomiPackage();
		pac.setPushToken(token);
		pac.setTitle("开源社区 im.akaxin.com");
		pac.setDescription("消息描述");
		pac.setPayload("你收到一条绝密消息");// 这里是传递给客户端的消息内容，客户端通过解析获取其中信息
		PushNotification.pushXiaomiNotification(pac);
	}
}
