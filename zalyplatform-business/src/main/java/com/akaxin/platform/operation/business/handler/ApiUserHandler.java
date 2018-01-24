package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.crypto.HashCrypto;
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
		logger.info("------------api.user.realName-----------");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			ApiUserRealNameProto.ApiUserRealNameRequest request = ApiUserRealNameProto.ApiUserRealNameRequest
					.parseFrom(command.getParams());

			String userIdPrik = request.getUserIdPrik();
			String userIdPubk = request.getUserIdPubk();
			String userId = HashCrypto.SHA1(userIdPubk);
			String phoneId = request.getPhoneId();
			String verifyCode = request.getPhoneVerifyCode();

			UserBean bean = new UserBean();
			bean.setUserId(userId);
			bean.setUserIdPrik(userIdPrik);
			bean.setUserIdPubk(userIdPubk);
			bean.setPhoneId(phoneId);
			bean.setPhoneRoaming("+86");

			logger.info("phone verify code bean={}", bean.toString());

			if (!ValidatorPattern.isPhoneId(phoneId) || StringUtils.isEmpty(userIdPrik)
					|| StringUtils.isEmpty(userIdPubk)) {
				errorCode = ErrorCode2.ERROR_PARAMETER;
				command.setResponse(commandResponse.setErrCode2(errorCode));
				return false;
			}

			if (!UserInfoDao.getInstance().existPhoneId(phoneId)) {
				String realVerifyCode = PhoneVCTokenDao.getInstance().getPhoneVC(phoneId);
				logger.info("Phone code={} realCode={} bean={}", verifyCode, realVerifyCode, bean.toString());
				if (StringUtils.isNotEmpty(realVerifyCode) && realVerifyCode.equals(verifyCode)) {
					if (UserInfoDao.getInstance().updatePhoneInfo(bean)) {
						errorCode = ErrorCode2.SUCCESS;
					}
				} else {
					errorCode = ErrorCode2.ERROR2_PHONE_VERIFYCODE;
				}
			} else {
				errorCode = ErrorCode2.ERROR2_PHONE_EXIST;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api.user.realName error.", e);
		}
		command.setResponse(commandResponse.setErrCode2(errorCode));
		return true;
	}

}
