package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.utils.UserIdUtils;
import com.akaxin.common.utils.ValidatorPattern;
import com.akaxin.platform.operation.business.dao.PhoneVCTokenDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.storage.bean.UserBean;
import com.akaxin.proto.core.ClientProto;
import com.akaxin.proto.platform.ApiUserPushTokenProto;
import com.akaxin.proto.platform.ApiUserRealNameProto;

/**
 * service:ApiUserInfo methods:
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 */
public class ApiUserHandler extends AbstractApiHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ApiUserHandler.class);

	public boolean pushToken(Command command) {
		logger.info("----api.user.pushToken command={}", command.toString());
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiUserPushTokenProto.ApiUserPushTokenRequest request = ApiUserPushTokenProto.ApiUserPushTokenRequest
					.parseFrom(command.getParams());
			ClientProto.ClientType clientType = request.getClientType();
			String rom = request.getRom();
			String pushToken = request.getPushToken();
			String deviceId = command.getDeviceId();

			logger.info("api.user.pushToken deviceId={} request={}", deviceId, request.toString());

			UserBean userBean = new UserBean();
			userBean.setUserId(command.getSiteUserId());
			userBean.setClientType(clientType.getNumber());
			userBean.setRom(rom);
			userBean.setPushToken(pushToken);
			userBean.setDeviceId(deviceId);

			logger.info("userInfoBean=" + userBean.toString());

			if (UserInfoDao.getInstance().saveUserInfo(userBean)) {
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR2_USER_SAVE_PUSHTOKEN;
			}

		} catch (Exception e) {
			logger.error("api.push token error", e);
		}
		command.setResponse(commandResponse.setErrCode2(errCode));
		return true;
	}

	/**
	 * 实名认证,绑定手机号码以及设置密码
	 * 
	 */
	public boolean realName(Command command) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			ApiUserRealNameProto.ApiUserRealNameRequest request = ApiUserRealNameProto.ApiUserRealNameRequest
					.parseFrom(command.getParams());
			String userIdPrik = request.getUserIdPrik();
			String userIdPubk = request.getUserIdPubk();
			String userId = UserIdUtils.getV1GlobalUserId(userIdPubk);
			String phoneId = request.getPhoneId();
			String verifyCode = request.getPhoneVerifyCode();
			int vcType = request.getVcType();
			logger.info("api.user.realName command={} request={}", command.toString(), request.toString());

			// 验证条件
			// 1.判断参数是否合法
			if (StringUtils.isNoneEmpty(userIdPrik, userIdPubk, userId, phoneId, verifyCode)) {
				// 2.验证手机格式是否合法
				if (ValidatorPattern.isPhoneId(phoneId)) {
					String phoneId2 = UserInfoDao.getInstance().getUserPhoneId(userId);
					// 3.已经绑定的账号，不能绑定其他手机号
					if (ValidatorPattern.isPhoneId(phoneId2)) {
						if (phoneId.equals(phoneId2)) {
							// 已经绑定的号码就是此号码，提醒用户“此账号已经绑定该手机号码”
							errorCode = ErrorCode2.ERROR2_PHONE_SAME;
						} else {
							// 此账号已经绑定了手机号码
							errorCode = ErrorCode2.ERROR2_PHONE_REALNAME_EXIST;
						}
					} else {
						// 4.已经绑定的手机号码不能绑定其他账号
						UserBean userBean = UserInfoDao.getInstance().getUserInfoByPhoneId(phoneId);
						if (userBean.getUserIdPrik() != null && userBean.getUserIdPubk() != null) {
							// 此手机号码已经绑定其他账号
							errorCode = ErrorCode2.ERROR2_PHONE_EXIST;
						} else {
							String realVerifyCode = PhoneVCTokenDao.getInstance().getPhoneVC(phoneId + "_" + vcType);
							if (StringUtils.isNotEmpty(realVerifyCode) && realVerifyCode.equals(verifyCode)) {
								UserBean bean = new UserBean();
								bean.setUserId(userId);
								bean.setUserIdPrik(userIdPrik);
								bean.setUserIdPubk(userIdPubk);
								bean.setPhoneId(phoneId);
								bean.setPhoneRoaming("+86");
								logger.info("Phone code={} realCode={} bean={}", verifyCode, realVerifyCode,
										bean.toString());
								if (UserInfoDao.getInstance().updatePhoneInfo(bean)) {
									errorCode = ErrorCode2.SUCCESS;
								}
							} else {
								errorCode = ErrorCode2.ERROR2_PHONE_VERIFYCODE;
							}
						}
					}

				} else {
					// 不支持的手机号,手机号码格式错误
					errorCode = ErrorCode2.ERROR2_PHONE_FORMATTING;
				}
			} else {
				// 错误的请求参数
				errorCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api.user.realName error.", e);
		}
		logger.info("api.user.realName result={}", errorCode.toString());
		command.setResponse(commandResponse.setErrCode2(errorCode));
		return true;
	}

}
