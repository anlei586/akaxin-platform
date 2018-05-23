package com.akaxin.platform.operation.business.handler;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.IErrorCode;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.platform.common.constant.CommandConst;
import com.akaxin.platform.common.constant.ErrorCode;
import com.akaxin.platform.common.exceptions.ErrCodeException;
import com.akaxin.platform.common.utils.GsonUtils;
import com.akaxin.platform.common.utils.ValidatorPattern;
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
		IErrorCode errCode = ErrorCode.ERROR;
		try {
			ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeRequest request = ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeRequest
					.parseFrom(command.getParams());
			String phoneId = request.getPhoneId();
			int vcType = request.getVcType();
			String countryCode = request.getCountryCode();
			String siteAddress = request.getSiteAddress();
			LogUtils.requestDebugLog(logger, command, request.toString());

			// 统计日志
			logger.info("api action={} countryCode={} phoneId={} type={} siteAddress", command.getAction(), countryCode,
					phoneId, vcType, siteAddress);

			if (!ValidatorPattern.isPhoneId(phoneId)) {
				throw new ErrCodeException(ErrorCode.ERROR2_PHONE_FORMATTING);
			}

			if (StringUtils.isEmpty(countryCode)) {
				countryCode = "+86";
			}

			// 这随机生成一个4位数验证码
			String phoneVC = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
			SmsResult smsResult = SmsSender.send(phoneId, phoneVC, EXPIRE_TIME / 60);
			if (smsResult != null && smsResult.isSuccess()) {
				String phoneWithType = phoneId + "_" + vcType;
				if (PhoneVCTokenDao.getInstance().setPhoneVC(phoneWithType, phoneVC, EXPIRE_TIME)) {
					ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeResponse response = ApiPhoneVerifyCodeProto.ApiPhoneVerifyCodeResponse
							.newBuilder().setExpireTime(60).build();
					commandResponse.setParams(response.toByteArray());
					errCode = ErrorCode.SUCCESS;
				} else {
					errCode = ErrorCode.ERROR2_PHONE_GETVERIFYCODE;
				}
			} else {
				errCode = ErrorCode.ERROR2_PHONE_GETVERIFYCODE;
			}
		} catch (Exception e) {
			if (e instanceof ErrCodeException) {
				errCode = ((ErrCodeException) e).getErrCode();
			} else {
				errCode = ErrorCode.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
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
				String vcKey = phoneId + "_" + vcType;
				String dbPhoneVC = PhoneVCTokenDao.getInstance().getPhoneVC(vcKey);
				logger.debug("vc1={} vc2={}", phoneVC, dbPhoneVC);

				if (phoneVC.equals(dbPhoneVC)) {
					UserBean userBean = UserInfoDao.getInstance().getRealNameUserInfo(phoneId);
					logger.debug("phone login userBean={}", GsonUtils.toJson(userBean));

					if (userBean != null
							&& StringUtils.isNoneEmpty(userBean.getUserIdPrik(), userBean.getUserIdPubk())) {
						ApiPhoneLoginProto.ApiPhoneLoginResponse response = ApiPhoneLoginProto.ApiPhoneLoginResponse
								.newBuilder().setUserIdPrik(userBean.getUserIdPrik())
								.setUserIdPubk(userBean.getUserIdPubk()).build();
						commandRespone.setParams(response.toByteArray());
						errCode = ErrorCode2.SUCCESS;

						// 使用完成以后，过期该验证码
						PhoneVCTokenDao.getInstance().delPhoneVC(vcKey);
					}
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
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiPhoneApplyTokenProto.ApiPhoneApplyTokenRequest request = ApiPhoneApplyTokenProto.ApiPhoneApplyTokenRequest
					.parseFrom(command.getParams());
			String globalUserId = request.getGlobalUserId();
			String siteAddress = request.getSiteAddress();// demo.akaxin.com:2021
			String phoneId = UserInfoDao.getInstance().getUserPhoneId(globalUserId);
			LogUtils.requestDebugLog(logger, command, request.toString());
			logger.info("action={} phoneId={}", command.getAction(), phoneId);

			if (StringUtils.isEmpty(globalUserId)) {
				throw new ErrCodeException(ErrorCode.ERROR_PARAMETER);
			}

			if (ValidatorPattern.isNotPhoneId(phoneId)) {
				throw new ErrCodeException(ErrorCode.ERROR2_PHONE_FORMATTING);
			}

			String phoneToken = UUID.randomUUID().toString();
			if (StringUtils.isNotEmpty(siteAddress)) {
				phoneToken = siteAddress + "_" + UUID.randomUUID().toString();
			}
			logger.debug("userId={},phoneId={},phoneToken={}", globalUserId, phoneId, phoneToken);

			String phoneTokenKey = RedisKeyUtils.getPhoneToken(phoneToken);
			if (PhoneVCTokenDao.getInstance().applyPhoneToken(phoneTokenKey, phoneId, EXPIRE_TIME)) {
				ApiPhoneApplyTokenProto.ApiPhoneApplyTokenResponse.Builder responseBuilder = ApiPhoneApplyTokenProto.ApiPhoneApplyTokenResponse
						.newBuilder();
				responseBuilder.setPhoneId(phoneId);
				responseBuilder.setCountryCode("+86");
				responseBuilder.setPhoneToken(phoneToken);
				commandRespone.setParams(responseBuilder.build().toByteArray());
				errCode = ErrorCode2.SUCCESS;
			}

		} catch (ErrCodeException e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandRespone.setErrCode(errCode);
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
			String siteAddress = request.getSiteAddress();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isEmpty(phoneToken)) {
				throw new ErrCodeException(ErrorCode.ERROR_PARAMETER);
			}

			String ptKey = phoneToken;
			if (StringUtils.isNotEmpty(siteAddress)) {
				ptKey = siteAddress + "_" + phoneToken;
			}
			String dbKey = RedisKeyUtils.getPhoneToken(ptKey);
			String phoneId = PhoneVCTokenDao.getInstance().getPhoneToken(dbKey);

			if (ValidatorPattern.isNotPhoneId(phoneId)) {
				// 重新在查询一次，兼容老版本
				dbKey = RedisKeyUtils.getPhoneToken(phoneToken);
				phoneId = PhoneVCTokenDao.getInstance().getPhoneToken(dbKey);
			}

			if (ValidatorPattern.isPhoneId(phoneId) || ValidatorPattern.isTestPhoneId(phoneId)) {
				// 通过手机号，查询用户账号公钥
				String userIdPubk = UserInfoDao.getInstance().getUserIdPubkByPhoneId(phoneId);
				ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenResponse.Builder responseBuilder = ApiPhoneConfirmTokenProto.ApiPhoneConfirmTokenResponse
						.newBuilder();
				responseBuilder.setPhoneId(phoneId);
				responseBuilder.setCountryCode("+86");
				if (StringUtils.isNotEmpty(userIdPubk)) {
					responseBuilder.setUserIdPubk(userIdPubk);
				}
				commandRespone.setParams(responseBuilder.build().toByteArray());
				errCode = ErrorCode2.SUCCESS;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandRespone.setErrCode2(errCode);
	}

}
