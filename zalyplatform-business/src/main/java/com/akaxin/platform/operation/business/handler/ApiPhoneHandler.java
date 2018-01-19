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
import com.akaxin.platform.operation.business.dao.PhoneVCTokenDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.proto.platform.ApiPhoneApplyTokenProto;
import com.akaxin.proto.platform.ApiPhoneConfirmTokenProto;
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
	 * 用户申请发送验证码VC<br>
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
			// int phoneVC = (int)((Math.random() * 9 + 1) * 100000);
			String phoneVC = "201024";

			if (PhoneVCTokenDao.getInstance().setPhoneVC(phoneId, phoneVC + "", EXPIRE_TIME)) {
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
			String phoneVC = request.getPhoneVerifyCode();

			logger.info("Phone Login  request phoneid={},vc={}", phoneId, phoneVC);

			if (!ValidatorPattern.isPhoneId(phoneId) || StringUtils.isEmpty(phoneVC)) {
				command.setResponse(commandRespone.setErrCode(errorCode));
				return false;
			}

			String realPhoneVC = PhoneVCTokenDao.getInstance().getPhoneVC(phoneId);
			logger.info("vc1={} vc2={}", phoneVC, realPhoneVC);

			if (!phoneVC.equals(realPhoneVC)) {
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

	/**
	 * 授权客户端申请的手机令牌phone-token
	 * 
	 * @param command
	 * @return
	 */
	public boolean applyToken(Command command) {
		CommandResponse commandRespone = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		try {
			ApiPhoneApplyTokenProto.ApiPhoneApplyTokenRequest request = ApiPhoneApplyTokenProto.ApiPhoneApplyTokenRequest
					.parseFrom(command.getParams());
			String userId = request.getUserId();
			String phoneId = UserInfoDao.getInstance().getUserPhoneId(userId);

			if (ValidatorPattern.isPhoneId(phoneId)) {
				String phoneIdWithGR = "";
				String phoneGlobalRoaming = UserInfoDao.getInstance().getPhoneGlobalRoaming(phoneId);
				if (StringUtils.isNotBlank(phoneGlobalRoaming)) {
					phoneIdWithGR = phoneGlobalRoaming + " " + phoneId;
				}
				String phoneToken = UUID.randomUUID().toString();

				logger.info("userId={},phoneId={},phoneCode={}", userId, phoneId, phoneToken);
				// 随机UUID
				if (PhoneVCTokenDao.getInstance().applyPhoneToken(phoneToken, phoneIdWithGR, EXPIRE_TIME)) {
					ApiPhoneApplyTokenProto.ApiPhoneApplyTokenResponse.Builder responseBuilder = ApiPhoneApplyTokenProto.ApiPhoneApplyTokenResponse
							.newBuilder();
					responseBuilder.setPhoneId(phoneId);
					responseBuilder.setGlobalRoaming(phoneGlobalRoaming);
					responseBuilder.setPhoneToken(phoneToken);
					commandRespone.setParams(responseBuilder.build().toByteArray());
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

	/**
	 * 认证站点phone—token
	 * 
	 * @param command
	 * @return
	 */
	public boolean confirmToken(Command command) {
		CommandResponse commandRespone = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		try {
			ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenRequest request = ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenRequest
					.parseFrom(command.getParams());

			String phoneToken = request.getPhoneToken();
			String phoneId = PhoneVCTokenDao.getInstance().getPhoneToken(phoneToken);

			logger.info("api.phone.confimCode phoneCode={} phoneId={}", phoneToken, phoneId);

			if (StringUtils.isNotBlank(phoneId)) {
				ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenResponse response = ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenResponse
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
