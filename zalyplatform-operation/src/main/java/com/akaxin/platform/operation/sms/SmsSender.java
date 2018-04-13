package com.akaxin.platform.operation.sms;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.akaxin.common.logs.LogUtils;
import com.akaxin.platform.common.logs.Log2Creater;
import com.akaxin.platform.common.utils.StringHelper;
import com.akaxin.platform.operation.bean.SmsResult;
import com.akaxin.platform.operation.monitor.SMSMonitor;
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
	private static final Logger logger = Log2Creater.createTimeLogger("sms");
	private static long sendTotalNumber = 0;
	private static final int appid = 1400063986;
	private static final String appkey = "6a468cc0ce6a85df972cf6c2a1cfb73e";
	// may use to control the send limit
	protected static RateLimiter limiter = RateLimiter.create(1);

	private SmsSender() {

	}

	public static SmsResult send(String phoneId, String vc, int expireMin) {
		try {
			SmsSingleSender sender = new SmsSingleSender(appid, appkey);
			ArrayList<String> params = new ArrayList<String>();
			params.add(vc);
			params.add(expireMin + "");
			SmsSingleSenderResult smsResult = sender.sendWithParam("86", phoneId, 80031, params, null, null, null);
			outPrintSmsLog(sendTotalNumber, phoneId, vc, smsResult);
			return new SmsResult(smsResult.result, smsResult.errMsg);
		} catch (Exception e) {
			SMSMonitor.COUNTER_ERROR.inc();
			logger.error(StringHelper.format("send sms error,phoneId:{} vc:{}", phoneId, vc), e);
		}
		return null;
	}

	private static void outPrintSmsLog(long count, String phoneId, String vc, SmsSingleSenderResult result) {
		try {
			SMSMonitor.COUNTER_TOTAL.inc();
			if (result.result == 0) {
				SMSMonitor.COUNTER_SUCCESS.inc();
			} else {
				SMSMonitor.COUNTER_FILTER.inc();
			}
			sendTotalNumber++;
			LogUtils.info(logger, "sms-count:{} phoneId:{} vc:{} result:{} errorMsg:{} ", sendTotalNumber, phoneId, vc,
					result.result, result.errMsg);
		} catch (Exception e) {
			logger.error(
					StringHelper.format("print sms send result error,phoneId:{},vc:{},result:{}", phoneId, vc, result),
					e);
		}
	}

}
