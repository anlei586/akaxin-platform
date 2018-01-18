package com.akaxin.platform.operation.utils;

public class RedisKeyUtils {

	public static String getUserInfoKey(String userId) {
		return userId;
	}

	// hash结构
	public static String getUserIdKey(String userId) {
		return "user_id_" + userId;
	}

	// hash结构
	public static String getUserPhoneKey(String phoneId) {
		return "user_phone_" + phoneId;
	}

	// hash结构
	public static String getSessionKey(String sessionId) {
		return "user_session_" + sessionId;
	}

	public static String getUserTokenKey(String deviceId) {
		return "user_token_" + deviceId;
	}
}
