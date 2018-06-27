/** 
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.akaxin.platform.common.constant;

import com.akaxin.common.constant.IErrorCode;

/**
 * <pre>
 * 站点服务端的错误信息
 * 		1.每个请求，成功状态只有一种状态
 * 		2.错误需要提示用户使用code=error.alert，客户端能够展示错误信息
 * 		3.其他错误使用code=error 客户端默认提示请求失败或者不展示错误提示
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-02 18:05:31
 */
public enum ErrorCode implements IErrorCode {
	SUCCESS("success", ""), // 操作成功

	ERROR_SYSTEMERROR("error.alert", "系统异常"), //
	ERROR_PARAMETER("error.alert", "请求参数错误"), // 请求参数错误
	ERROR_NOPERMISSION("error.alert", "用户无权限该操作"), // 用户无权限错误
	ERROR_UNSUPPORT_PROTOCOL("error.alert", "协议版本错误"), // 服务端不支持该功能
	ERROR_ILLEGALREQUEST("error.alert", "非法请求"), // 非法的请求
	ERROR_INVALIDPACKAGEACTION("error.alert", "无效的包名称"), // 无效的包action
	ERROR_DATABASE_EXECUTE("error.alert", "数据库执行错误"), // 无效的包action

	ERROR_USER_ID("error.alert", "用户ID身份不匹配"), // 请求参数错误
	ERROR_SESSION("error.session", "用户身份认证失败"), // session验证失败

	ERROR_REGISTER("error.alert", "用户注册失败"), // 用户注册失败
	ERROR_REGISTER_SAVEPROFILE("error.alert", "保存用户数据失败"), // 用户注册失败
	ERROR_REGISTER_UIC("error.alert", "用户邀请码错误"), // 用户邀请码错误
	ERROR_REGISTER_PHONEID("error.alert", "用户实名手机号验证错误"), // 验证手机号失败

	ERROR2_USER_SAVE_PUSHTOKEN("error.alert", "保存数据失败"), //
	ERROR2_USER_UPDATE_PROFILE("error.alert", "更新数据库用户身份失败"), //

	ERROR2_FRIEND_IS("error.alert", "用户已经是你的好友"), //
	ERROR2_FRIEND_APPLYSELF("error.alert", "用户不能添加自己为好友"), //
	ERROR2_FRIEND_APPLYCOUNT("error.alert", "添加好友最多为5次"), //

	ERROR_GROUP_INVITE_CHAT_CLOSE("error.alert", "群主已关闭邀请群聊功能"), //
	ERROR_GROUP_MAXMEMBERCOUNT("error.alert", "超过群人数上限"), // 添加群成员，人数超过上限
	ERROR_GROUP_MEMBERLESS3("error.alert", "创建群成员少于三人"), //

	ERROR2_LOGGIN_USERID_EMPTY("error.alert", "用户身份为空"), // 用户身份校验失败
	ERROR2_LOGGIN_DEVICEID_EMPTY("error.alert", "设备身份为空"), // 用户身份校验失败
	ERROR2_LOGGIN_UPDATE_DEVICE("error.alert", "更新设备失败"), // 更新设备失败
	ERROR2_LOGGIN_UPDATE_SESSION("error.alert", "保存session失败"), // 保存session
	ERROR2_LOGGIN_ERRORSIGN("error.alert", "用户身份校验失败，请重新登陆"), // 用户身份校验失败
	ERROR2_LOGGIN_NOREGISTER("error.login.need_register", ""), // 用户需要在该站点注册
	ERROR2_LOGGIN_SEALUPUSER("error.alert", "当前用户无权登陆"), // 用户需要在该站点注册

	ERROR2_IMAUTH_FAIL("error.alert", "im连接认证失败"), // 用户需要在该站点注册

	ERROR2_FILE_DOWNLOAD("error.file.download", ""), //

	ERROR2_PHONE_SAME("error.phone.same", "本机身份已与此号码实名绑定"), //
	ERROR2_PHONE_EXIST("error.alert", "该手机号码已经绑定其他账号"), //
	ERROR2_PHONE_REALNAME_EXIST("error.alert", "此账号已经绑定手机号码"), //
	ERROR2_PHONE_VERIFYCODE("error.alert", "验证码验证失败"), //
	ERROR2_PHONE_GETVERIFYCODE("error.alert", "获取验证码失败"), //
	ERROR2_PHONE_FORMATTING("error.alert", "手机号格式错误"), //

	ERROR2_PLUGIN_STATUS("error.alert", "扩展状态错误"), //

	ERROR2_SECRETCHAT_CLOSE("error.alert", "站点服务不支持绝密聊天"), //

	ERROR_PUSH_MUTE("error.alert", "PUSH发送失败，用户设置了静音"), //
	ERROR_PUSH_NO_CLIENTTYPE("error.alert", "错误的客户端类型"), //
	ERROR_PUSH_USERTOKEN("error.alert", "usertoken is null"), //
	ERROR_PUSH_RIGHT_USERTOKEN("error.alert", "usertoken is not right"), //

	ERROR("error.alert", "请求失败"); // 默认未知错误

	private String code;
	private String info;

	ErrorCode(String code, String info) {
		this.code = code;
		this.info = info;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getInfo() {
		return this.info;
	}

	@Override
	public boolean isSuccess() {
		return "success".equals(this.code) ? true : false;
	}

	@Override
	public String toString() {
		return "errCode:" + this.code + " errInfo:" + this.info;
	}
}
