package com.akaxin.platform.test;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;

public class TestSms {
	public static void main(String[] args) {
		try {
			int appid = 1400063986;
			String appkey = "6a468cc0ce6a85df972cf6c2a1cfb73e";
			SmsSingleSender sender = new SmsSingleSender(appid, appkey);
			SmsSingleSenderResult result = sender.send(0, "86", "18811782523", "【阿卡信】验证码测试", "", "123");
			System.out.print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
