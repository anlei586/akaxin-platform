package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.crypto.HashCrypto;
import com.akaxin.common.utils.ValidatorPattern;
import com.akaxin.platform.operation.business.dao.PhoneCodeDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.utils.PhoneCodeUtils;
import com.akaxin.proto.core.ClientProto;
import com.akaxin.proto.platform.ApiUserPushTokenProto;
import com.akaxin.proto.platform.ApiUserRealNameProto;
import com.zaly.platform.storage.bean.UserBean;

/**
 * service:ApiUserInfo methods:
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 */
public class ApiUserHandler extends AbstractApiHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ApiUserHandler.class);

	public boolean pushToken(Command command) {
		logger.info("----api.user.supplyToken command={}", command.toString());
		CommandResponse commandResponse = new CommandResponse();
		String errCode = ErrorCode.ERROR;
		try {
			ApiUserPushTokenProto.ApiUserPushTokenRequest request = ApiUserPushTokenProto.ApiUserPushTokenRequest
					.parseFrom(command.getParams());
			String deviceId = command.getDeviceId();
			ClientProto.ClientType clientType = request.getClientType();
			String rom = request.getRom();
			String pushToken = request.getPushToken();

			logger.info("api.user.supplyToken request={}", request.toString());

			UserBean userBean = new UserBean();
			userBean.setUserId(command.getSiteUserId());
			userBean.setClientType(clientType.getNumber());
			userBean.setRom(rom);
			userBean.setPushToken(pushToken);

			logger.info("userInfoBean=" + userBean.toString());

			if (UserInfoDao.getInstance().saveUserInfo(userBean)) {
				errCode = ErrorCode.SUCCESS;
			}
			return true;
		} catch (Exception e) {
			logger.error("api.push token error", e);
		}
		commandResponse.setErrCode(errCode);
		return false;
	}

	/**
	 * 实名认证,绑定手机号码以及设置密码
	 * 
	 */
	public boolean realName(Command command) {
		logger.info("------------api.user.realName-----------");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
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
			bean.setUserPhoneId(phoneId);
			bean.setPhoneRoaming("+86");

			logger.info("phone verify code bean={}", bean.toString());

			if (!ValidatorPattern.isPhoneId(phoneId) || StringUtils.isEmpty(userIdPrik)
					|| StringUtils.isEmpty(userIdPubk)) {
				command.setResponse(commandResponse.setErrCode(errorCode));
				return false;
			}

			String redisKey = PhoneCodeUtils.getPhoneVCKey(phoneId);
			String realVerifyCode = PhoneCodeDao.getInstance().getPhoneCode(redisKey);

			logger.info("Phone code={} realCode={} bean={}", verifyCode, realVerifyCode, bean.toString());

			if (StringUtils.isNotEmpty(realVerifyCode) && realVerifyCode.equals(verifyCode)) {
				if (UserInfoDao.getInstance().updateRealNameInfo(bean)) {
					errorCode = ErrorCode.SUCCESS;
				}
			} else {
				commandResponse.setErrInfo("verify phone code error.");
			}

		} catch (Exception e) {
			commandResponse.setErrInfo("api.user.realName exception");
			logger.error("api.user.realName error.", e);
		}

		command.setResponse(commandResponse.setErrCode(errorCode));
		return true;
	}

}
