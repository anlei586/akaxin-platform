package com.akaxin.platform.operation.business.handler;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.common.utils.ValidatorPattern;
import com.akaxin.platform.operation.bean.SmsResult;
import com.akaxin.platform.operation.business.dao.PhoneVCTokenDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.sms.SmsSender;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.bean.UserBean;
import com.akaxin.proto.platform.ApiPhoneApplyTokenProto;
import com.akaxin.proto.platform.ApiPhoneConfirmTokenProto;
import com.akaxin.proto.platform.ApiPhoneLoginProto;
import com.akaxin.proto.platform.ApiPhoneVerifyCodeProto;

/**
 * 平台：用户手机验证码申请，以及手机号登陆
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ApiPhoneHandler extends AbstractApiHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ApiPhoneHandler.class);

	private static final int EXPIRE_TIME = 60 * 5;

	// 用户申请发送验证码VC<br>
	public CommandResponse verifyCode(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeRequest request = ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeRequest
					.parseFrom(command.getParams());
			String phoneId = request.getPhoneId();
			int vcType = request.getVcType();
			LogUtils.requestDebugLog(logger, command, request.toString());

			// 这随机生成一个4位数验证码
			String phoneVC = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
			SmsResult smsResult = SmsSender.send(phoneId, phoneVC, EXPIRE_TIME / 60);
			if (smsResult != null && smsResult.isSuccess()) {
				String phoneWithType = phoneId + "_" + vcType;
				if (PhoneVCTokenDao.getInstance().setPhoneVC(phoneWithType, phoneVC, EXPIRE_TIME)) {
					ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeResponse response = ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeResponse
							.newBuilder().setExpireTime(60).build();
					commandResponse.setParams(response.toByteArray());
					errorCode = ErrorCode2.SUCCESS;
				} else {
					errorCode = ErrorCode2.ERROR2_PHONE_GETVERIFYCODE;
				}
			} else {
				errorCode = ErrorCode2.ERROR2_PHONE_GETVERIFYCODE;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 使用手机号，配合手机验证码登陆
	 * 
	 * @param command
	 * 
	 */
	public CommandResponse login(Command command) {
		CommandResponse commandRespone = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiPhoneLoginProto.ApiPhoneLoginRequest request = ApiPhoneLoginProto.ApiPhoneLoginRequest
					.parseFrom(command.getParams());
			String phoneId = request.getPhoneId();
			String phoneVC = request.getPhoneVerifyCode();
			int vcType = request.getVcType();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (ValidatorPattern.isPhoneId(phoneId) && StringUtils.isNotBlank(phoneVC)) {
				String realPhoneVC = PhoneVCTokenDao.getInstance().getPhoneVC(phoneId + "_" + vcType);
				logger.debug("vc1={} vc2={}", phoneVC, realPhoneVC);

				if (phoneVC.equals(realPhoneVC)) {
					UserBean userBean = UserInfoDao.getInstance().getRealNameUserInfo(phoneId);
					logger.debug("phone login userBean={}", GsonUtils.toJson(userBean));

					ApiPhoneLoginProto.ApiPhoneLoginResponse response = ApiPhoneLoginProto.ApiPhoneLoginResponse
							.newBuilder().setUserIdPrik(String.valueOf(userBean.getUserIdPrik()))
							.setUserIdPubk(String.valueOf(userBean.getUserIdPubk())).build();
					commandRespone.setParams(response.toByteArray());
					errCode = ErrorCode2.SUCCESS;
				} else {
					errCode = ErrorCode2.ERROR2_PHONE_VERIFYCODE;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandRespone.setErrCode2(errCode);
	}

	/**
	 * 授权客户端申请的手机令牌phone-token
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse applyToken(Command command) {
		CommandResponse commandRespone = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiPhoneApplyTokenProto.ApiPhoneApplyTokenRequest request = ApiPhoneApplyTokenProto.ApiPhoneApplyTokenRequest
					.parseFrom(command.getParams());
			String userId = request.getUserId();
			String phoneId = UserInfoDao.getInstance().getUserPhoneId(userId);
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (ValidatorPattern.isPhoneId(phoneId) && StringUtils.isNotEmpty(userId)) {
				String phoneToken = UUID.randomUUID().toString();
				logger.debug("userId={},phoneId={},phoneToken={}", userId, phoneId, phoneToken);

				String phoneTokenKey = RedisKeyUtils.getPhoneToken(phoneToken);
				if (PhoneVCTokenDao.getInstance().applyPhoneToken(phoneTokenKey, phoneId, EXPIRE_TIME)) {
					ApiPhoneApplyTokenProto.ApiPhoneApplyTokenResponse.Builder responseBuilder = ApiPhoneApplyTokenProto.ApiPhoneApplyTokenResponse
							.newBuilder();
					responseBuilder.setPhoneId(phoneId);
					responseBuilder.setGlobalRoaming("+86");
					responseBuilder.setPhoneToken(phoneToken);
					commandRespone.setParams(responseBuilder.build().toByteArray());
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandRespone.setErrCode2(errCode);
	}

	/**
	 * 认证站点phone—token
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse confirmToken(Command command) {
		CommandResponse commandRespone = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenRequest request = ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenRequest
					.parseFrom(command.getParams());
			String phoneToken = request.getPhoneToken();
			String phoneTokenKey = RedisKeyUtils.getPhoneToken(phoneToken);
			String phoneId = PhoneVCTokenDao.getInstance().getPhoneToken(phoneTokenKey);
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (ValidatorPattern.isPhoneId(phoneId)) {
				ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenResponse response = ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenResponse
						.newBuilder().setPhoneId(phoneId).setGlobalRoaming("+86").build();
				commandRespone.setParams(response.toByteArray());
				errCode = ErrorCode2.SUCCESS;
			}
			
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandRespone.setErrCode2(errCode);
	}

}
