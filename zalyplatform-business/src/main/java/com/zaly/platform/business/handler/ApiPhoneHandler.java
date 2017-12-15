package com.zaly.platform.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.zaly.common.command.Command;
import com.zaly.common.command.CommandResponse;
import com.zaly.common.constant.CommandConst;
import com.zaly.common.constant.ErrorCode;
import com.zaly.common.crypto.HashCrypto;
import com.zaly.common.utils.GsonUtils;
import com.zaly.common.utils.ValidatorPattern;
import com.zaly.platform.business.dao.UserInfoDao;
import com.zaly.platform.business.dao.UserPhoneDao;
import com.zaly.platform.storage.bean.RealNameUserBean;
import com.zaly.proto.platform.ApiPhoneLoginProto;
import com.zaly.proto.platform.ApiPhoneVerifyCodeProto;

/**
 * 平台：用户手机验证码申请，以及手机号登陆
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ApiPhoneHandler extends AbstractCommonHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ApiPhoneHandler.class);

	/**
	 * 用户申请发送验证码<br>
	 * 
	 */
	public boolean verifyCode(Command command) {
		logger.info(" ========verify phone code========");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		try {
			ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeRequest request = ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeRequest
					.parseFrom(command.getParams());

			String phoneId = request.getPhoneId();
			String phoneVerifyCode = "201024";
			int expireTime = 60;// 60s

			String redisKey = "phone_code_" + phoneId;
			if (UserPhoneDao.getInstance().applyPhoneVerifyCode(redisKey, phoneVerifyCode, expireTime)) {
				ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeResponse response = ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeResponse
						.newBuilder().setExpireTime(expireTime).build();
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
			String password = request.getPassword();
			logger.info("Phone Login  request phoneid={},password={}", phoneId, password);

			if (!ValidatorPattern.isPhoneId(phoneId) || StringUtils.isEmpty(password)) {
				errorCode = 101 + "";
				command.setResponse(commandRespone.setErrCode(errorCode));
				return false;
			}

			RealNameUserBean userBean = UserInfoDao.getInstance().getRealUserInfo(phoneId);
			logger.info("phone login userBean={}", GsonUtils.toJson(userBean));
			String realPasswordId = userBean.getPassword();
			String verifyPasswordId = HashCrypto.MD5(password);

			if (!verifyPasswordId.equals(realPasswordId)) {
				errorCode = 102 + "";
				command.setResponse(commandRespone.setErrCode(errorCode));
				return false;
			}

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

}
