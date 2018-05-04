package com.akaxin.platform.test.push;

import com.akaxin.platform.operation.push.umeng.AndroidNotification;
import com.akaxin.platform.operation.push.umeng.AndroidUnicast;
import com.akaxin.platform.operation.push.umeng.UmengPushClient;
import com.akaxin.platform.operation.push.umeng.AndroidNotification.DisplayType;

//umeng server for java
public class Demo {

	private static final String UMENG_APP_KEY = "5aeaae6bf29d9812810000b1";
	private static final String UMENG_APP_MASTER_SECRET = "b9xs50emaqzf5fzfbclm9qbupq2gyeul";

	private static final String UMENG_APP_KEY_DEBUG = "5aeaadf98f4a9d06c200010b";
	private static final String UMENG_APP_SECRET_DEBUG = "ahwsqtjdtr9dku1mzx0sm3bq7arro7rz";

	private String appkey = null;
	private String appMasterSecret = null;
	private UmengPushClient client = new UmengPushClient();

	public Demo(String key, String secret) {
		try {
			appkey = key;
			appMasterSecret = secret;
			System.out.println("key" + key);
			System.out.println("secret=" + secret);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void sendAndroidUnicast() throws Exception {
		AndroidUnicast unicast = new AndroidUnicast(appkey, appMasterSecret);
		// TODO Set your device token
		unicast.setDeviceToken("Ao68kmqfT8zy61XF1XVgULws2KPLNUVtMsD_DWv9A4py");
		unicast.setTicker("Android unicast ticker");
		unicast.setTitle("中文的title");
		unicast.setText("Android unicast text");
		unicast.goAppAfterOpen();
		unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device.
		// For how to register a test device, please see the developer doc.
		// unicast.setProductionMode();
		unicast.setTestMode();
		// Set customized fields
		unicast.setExtraField("test", "helloworld");
		System.out.println(client.send(unicast));
	}

	public static void main(String[] args) {
		// TODO set your appkey and master secret here
		Demo demo = new Demo(UMENG_APP_KEY_DEBUG, UMENG_APP_SECRET_DEBUG);
		// Demo demo = new Demo(UMENG_APP_KEY, UMENG_APP_SECRET);
		try {
			demo.sendAndroidUnicast();
			/*
			 * TODO these methods are all available, just fill in some fields and do the
			 * test demo.sendAndroidCustomizedcastFile(); demo.sendAndroidBroadcast();
			 * demo.sendAndroidGroupcast(); demo.sendAndroidCustomizedcast();
			 * demo.sendAndroidFilecast();
			 * 
			 * demo.sendIOSBroadcast(); demo.sendIOSUnicast(); demo.sendIOSGroupcast();
			 * demo.sendIOSCustomizedcast(); demo.sendIOSFilecast();
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
