package com.akaxin.platform.operation.business.handler;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.common.utils.ValidatorPattern;
import com.akaxin.platform.operation.business.dao.PhoneCodeDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.utils.PhoneCodeUtils;
import com.akaxin.proto.platform.ApiPhoneApplyCodeProto;
import com.akaxin.proto.platform.ApiPhoneConfirmCodeProto;
import com.akaxin.proto.platform.ApiPhoneLoginProto;
import com.akaxin.proto.platform.ApiPhoneVerifyCodeProto;
import com.google.protobuf.InvalidProtocolBufferException;
import com.zaly.platform.storage.bean.UserBean;

/**
 * 平台：用户手机验证码申请，以及手机号登陆
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ApiPhoneHandler extends AbstractApiHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ApiPhoneHandler.class);

	private static final int EXPIRE_TIME = 60;

	/**
	 * 用户申请发送验证码<br>
	 * 
	 */
	public boolean verifyCode(Command command) {
		logger.info("------api.phone.verify------");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		try {
			ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeRequest request = ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeRequest
					.parseFrom(command.getParams());
			String phoneId = request.getPhoneId();
			// 这随机生成一个6位数验证码
			// int phoneVerifyCode = (int)((Math.random() * 9 + 1) * 100000);
			String phoneVerifyCode = "201024";

			String redisKey = PhoneCodeUtils.getPhoneVCKey(phoneId);
			if (PhoneCodeDao.getInstance().setPhoneCode(redisKey, phoneVerifyCode + "", EXPIRE_TIME)) {
				ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeResponse response = ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeResponse
						.newBuilder().setExpireTime(EXPIRE_TIME).build();
				commandResponse.setParams(response.toByteArray());
				errorCode = ErrorCode.SUCCESS;
			} else {
				commandResponse.setErrInfo("verify code error.");
			}

		} catch (InvalidProtocolBufferException e) {
			commandResponse.setErrInfo("phone verify code error");
			logger.error("phone verify code error.", e);
		}
		command.setResponse(commandResponse.setErrCode(errorCode));
		return false;
	}

	/**
	 * 使用手机号，配合手机验证码登陆
	 * 
	 * @param command
	 * 
	 */
	public boolean login(Command command) {
		CommandResponse commandRespone = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		try {
			ApiPhoneLoginProto.ApiPhoneLoginRequest request = ApiPhoneLoginProto.ApiPhoneLoginRequest
					.parseFrom(command.getParams());
			String phoneId = request.getPhoneId();
			String phoneVerifyCode = request.getPhoneVerifyCode();

			logger.info("Phone Login  request phoneid={},vc={}", phoneId, phoneVerifyCode);

			if (!ValidatorPattern.isPhoneId(phoneId) || StringUtils.isEmpty(phoneVerifyCode)) {
				command.setResponse(commandRespone.setErrCode(errorCode));
				return false;
			}

			String vc_key = PhoneCodeUtils.getPhoneVCKey(phoneId);
			String realVerifyCode = PhoneCodeDao.getInstance().getPhoneCode(vc_key);
			logger.info("vc1={} vc2={}", phoneVerifyCode, realVerifyCode);

			if (!phoneVerifyCode.equals(realVerifyCode)) {
				command.setResponse(commandRespone.setErrCode(errorCode));
				return false;
			}

			UserBean userBean = UserInfoDao.getInstance().getRealNameInfo(phoneId);
			logger.info("phone login userBean={}", GsonUtils.toJson(userBean));

			ApiPhoneLoginProto.ApiPhoneLoginResponse response = ApiPhoneLoginProto.ApiPhoneLoginResponse.newBuilder()
					.setUserIdPrik(String.valueOf(userBean.getUserIdPrik()))
					.setUserIdPubk(String.valueOf(userBean.getUserIdPubk())).build();

			commandRespone.setParams(response.toByteArray());
			errorCode = ErrorCode.SUCCESS;

		} catch (InvalidProtocolBufferException e) {
			commandRespone.setErrInfo("phone login error");
			logger.error("phone login error.", e);
		}
		command.setResponse(commandRespone.setErrCode(errorCode));
		return false;
	}

	public boolean allowCode(Command command) {
		CommandResponse commandRespone = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		try {
			ApiPhoneApplyCodeProto.ApiPhoneApplyCodeRequest request = ApiPhoneApplyCodeProto.ApiPhoneApplyCodeRequest
					.parseFrom(command.getParams());
			String userId = request.getUserId();
			String phoneId = UserInfoDao.getInstance().getUserPhoneId(userId);

			if (ValidatorPattern.isPhoneId(phoneId)) {
				String phoneGlobalRoaming = UserInfoDao.getInstance().getPhoneGlobalRoaming(phoneId);
				if (StringUtils.isNotBlank(phoneGlobalRoaming)) {
					phoneId = phoneGlobalRoaming + " " + phoneId;
				}
				String phoneCode = UUID.randomUUID().toString();

				logger.info("userId={},phoneId={},phoneCode={}", userId, phoneId, phoneCode);
				// 随机UUID
				if (PhoneCodeDao.getInstance().setPhoneCode(phoneCode, phoneId, EXPIRE_TIME)) {
					ApiPhoneApplyCodeProto.ApiPhoneApplyCodeResponse response = ApiPhoneApplyCodeProto.ApiPhoneApplyCodeResponse
							.newBuilder().setPhoneCode(phoneCode).build();
					commandRespone.setParams(response.toByteArray());
					errorCode = ErrorCode.SUCCESS;
				}
			}

		} catch (Exception e) {
			commandRespone.setErrInfo("phone login error");
			logger.error("phone login error.", e);
		}
		command.setResponse(commandRespone.setErrCode(errorCode));
		return false;
	}

	public boolean confirmCode(Command command) {
		CommandResponse commandRespone = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		try {
			ApiPhoneConfirmCodeProto.ApiPhoneConfirmCodeRequest request = ApiPhoneConfirmCodeProto.ApiPhoneConfirmCodeRequest
					.parseFrom(command.getParams());

			String phoneCode = request.getPhoneCode();
			String phoneId = PhoneCodeDao.getInstance().getPhoneCode(phoneCode);

			logger.info("api.phone.confimCode phoneCode={} phoneId={}", phoneCode, phoneId);

			if (StringUtils.isNotBlank(phoneId)) {
				ApiPhoneConfirmCodeProto.ApiPhoneConfirmCodeResponse response = ApiPhoneConfirmCodeProto.ApiPhoneConfirmCodeResponse
						.newBuilder().setPhoneId(phoneId).build();
				commandRespone.setParams(response.toByteArray());
				errorCode = ErrorCode.SUCCESS;
			}

		} catch (Exception e) {
			commandRespone.setErrInfo("phone login error");
			logger.error("phone login error.", e);
		}
		command.setResponse(commandRespone.setErrCode(errorCode));
		return false;
	}

}
