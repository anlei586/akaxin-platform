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

	// hash结构
	public static String getSessionKey(String userId) {
		return "user_session_" + userId;
	}

	// 用户令牌
	public static String getUserTokenKey(String deviceId) {
		return "user_token_" + deviceId;
	}

	// 手机令牌
	public static String getPhoneToken(String phoneToken) {
		return "phone:token:" + phoneToken;
	}

	// 手机验证码
	public static String getPhoneVCKey(String key) {
		return "phone:vc:" + key;
	}

	// 用户个人静音设置 Hash接口
	public static String getUserMuteKey(String userId) {
		return "user:mute:" + userId;
	}

}
