package com.akaxin.platform.operation.utils;

public class RedisKeyUtils {

	// 用户个人信息
	public static String getUserIdKey(String userId) {
		return "user_id_" + userId;
	}

	// 用户手机
	public static String getUserPhoneKey(String phoneId) {
		return "user_phone_" + phoneId;
	}

	// 用户令牌：Hash结构
	// 存储每个站点的usertoken <siteAddress,userToken>
	public static String getUserTokenKey(String deviceId) {
		return "user:token:" + deviceId;
	}

	// 用户存储的session:hash结构
	// 存储globalUserId && deviceId
	public static String getSessionKey(String sessionId) {
		return "user:session:" + sessionId;
	}

	// 用户device存储的内容:hash结构
	// 存储sessionId && globalUserId
	public static String getUserDeviceKey(String deviceId) {
		return "user:device:" + deviceId;
	}

	// 用户个人静音设置:Hash结构
	public static String getUserMuteKey(String globalUserId) {
		return "user:mute:" + globalUserId;
	}

	// 手机令牌
	public static String getPhoneToken(String phoneToken) {
		return "phone:token:" + phoneToken;
	}

	// 手机验证码
	public static String getPhoneVCKey(String key) {
		return "phone:vc:" + key;
	}

	// 用户访问的站点
	public static String getUserRealNameSite(String globalUserId) {
		return "user:" + globalUserId + ":visit:site";
	}

}
