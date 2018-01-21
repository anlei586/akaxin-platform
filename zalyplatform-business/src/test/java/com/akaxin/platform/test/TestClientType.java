package com.akaxin.platform.test;

import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.proto.core.ClientProto;

public class TestClientType {
	public static void main(String[] args) {
		ClientProto.ClientType clientType = UserInfoDao.getInstance().getClientType("");

		switch (clientType) {
		case IOS:
			System.out.print("IOS");
			break;
		case ANDROID_HUAWEI:
		case ANDROID_OPPO:
		case ANDROID_XIAOMI:
		case ANDROID:
			System.out.print("ANDROID");
			break;
		default:
			System.out.print("IOS");
			break;
		}
	}
}
