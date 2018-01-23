package com.akaxin.platform.operation.sms;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.operation.bean.SmsResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.google.common.util.concurrent.RateLimiter;

/**
 * 通过腾讯短信通道，发送短信
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-22 20:51:55
 */
public class SmsSender {
	private static final Logger logger = LoggerFactory.getLogger(SmsSender.class);
	private static long sendTotalNumber = 0;
	private static final int appid = 1400063986;
	private static final String appkey = "6a468cc0ce6a85df972cf6c2a1cfb73e";
	private static RateLimiter limiter = RateLimiter.create(1);

	private SmsSender() {

	}

	public static SmsResult send(String phoneId, String vc, int expireMin) {
		try {
			SmsSingleSender sender = new SmsSingleSender(appid, appkey);
			ArrayList<String> params = new ArrayList<String>();
			params.add(vc);
			params.add(expireMin + "");
			SmsSingleSenderResult smsResult = sender.sendWithParam("86", phoneId, 80031, params, null, null, null);
			sendTotalNumber++;
			logger.info("send sms count={} phoneId={} vc={} result={} errorMsg={} ", sendTotalNumber, phoneId, vc,
					smsResult.result, smsResult.errMsg);
			return new SmsResult(smsResult.result, smsResult.errMsg);
		} catch (Exception e) {
			logger.error("send sms error", e);
		}
		return null;
	}

	public static void main(String args[]) {
		send("15271868205", "201024", 1);
	}

}
