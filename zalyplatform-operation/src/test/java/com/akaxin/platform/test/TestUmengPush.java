package com.akaxin.platform.test;

import com.akaxin.platform.operation.push.PushNotification;
import com.akaxin.platform.operation.push.umeng.UmengPackage;

public class TestUmengPush {
	public static void main(String[] args) {
		String token = "dev_Ao68kmqfT8zy61XF1XVgULws2KPLNUVtMsD_DWv9A4py";

		UmengPackage pac = new UmengPackage();
		pac.setPushToken(token);
		pac.setTitle("开源社区 im.akaxin.com");
		pac.setTicker("test 提示");
		pac.setText("你好啊，哈哈哈");
		pac.setPushGoto("zaly://im.akaxin.com/goto");
		PushNotification.pushUMengNotification(pac);
	}
}
