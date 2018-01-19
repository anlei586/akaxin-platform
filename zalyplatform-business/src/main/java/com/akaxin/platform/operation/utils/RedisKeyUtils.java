package com.akaxin.platform.operation.utils;

public class RedisKeyUtils {

	public static String getUserInfoKey(String userId) {
		return userId;
	}

	// 用户个人信息
	public static String getUserIdKey(String userId) {
		return "user_id_" + userId;
	}

	// 用户手机
	public static String getUserPhoneKey(String phoneId) {
		return "user_phone_" + phoneId;
	}

	// hash结构
	public static String getSessionKey(String sessionId) {
		return "user_session_" + sessionId;
	}

	// 用户令牌
	public static String getUserTokenKey(String deviceId) {
		return "user_token_" + deviceId;
	}

	// 手机令牌
	public static String getPhoneToken(String phoneId) {
		return "phone_token_" + phoneId;
	}

	// 手机验证码
	public static String getPhoneVCKey(String key) {
		return "phone_vc_" + key;
	}
}
