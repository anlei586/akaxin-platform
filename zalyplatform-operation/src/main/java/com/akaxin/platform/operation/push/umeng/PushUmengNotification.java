package com.akaxin.platform.operation.push.umeng;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.operation.push.PushResult;

/**
 * 推送umeng-Push
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-04 18:44:48
 */
public class PushUmengNotification {
	private static final Logger logger = LoggerFactory.getLogger(PushUmengNotification.class);

	private static final String UMENG_APP_KEY = "5aeaae6bf29d9812810000b1";
	private static final String UMENG_APP_MASTER_SECRET = "b9xs50emaqzf5fzfbclm9qbupq2gyeul";

	private static final String SANBOX_PRE = "dev_";
	private static final String UMENG_APP_KEY_DEBUG = "5aeaadf98f4a9d06c200010b";
	private static final String UMENG_APP_MASTER_SECRET_DEBUG = "ahwsqtjdtr9dku1mzx0sm3bq7arro7rz";

	private static final String PUSH_GOTO = "push-goto";

	private UmengPushClient umengPushclient = new UmengPushClient();

	private PushUmengNotification() {

	}

	private static class SingletonHolder {
		private static PushUmengNotification instance = new PushUmengNotification();
	}

	public static PushUmengNotification getInstance() {
		return SingletonHolder.instance;
	}

	// 通过客户端，执行push操作
	public void pushNotification(UmengPackage pack) {
		try {
			String pushToken = pack.getPushToken();

			if (StringUtils.isEmpty(pushToken)) {
				return;
			}

			AndroidUnicast unicast = null;
			if (StringUtils.isNotEmpty(pushToken) && pushToken.startsWith(SANBOX_PRE)) {
				pushToken = pushToken.substring(4, pushToken.length());
				unicast = new AndroidUnicast(UMENG_APP_KEY_DEBUG, UMENG_APP_MASTER_SECRET_DEBUG);
				unicast.setTestMode();
			} else {
				unicast = new AndroidUnicast(UMENG_APP_KEY, UMENG_APP_MASTER_SECRET);
				unicast.setProductionMode();
			}
			unicast.setDeviceToken(pushToken);
			unicast.setTicker(pack.getTicker());
			unicast.setTitle(pack.getTicker());
			unicast.setText(pack.getText());
			unicast.goAppAfterOpen();
			unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);

			unicast.setExtraField(PUSH_GOTO, pack.getPushGoto());

			PushResult result = umengPushclient.send(unicast);

			logger.info("send umeng push result={}", result.toString());
		} catch (Exception e) {
			logger.error("push umeng notification error", e);
		}

	}
}
