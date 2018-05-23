package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.IErrorCode;
import com.akaxin.common.exceptions.ZalyException;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.platform.common.constant.ErrorCode;
import com.akaxin.platform.common.exceptions.ErrCodeException;
import com.akaxin.platform.common.utils.UserIdUtils;
import com.akaxin.platform.common.utils.ValidatorPattern;
import com.akaxin.platform.operation.business.dao.PhoneVCTokenDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.storage.bean.UserBean;
import com.akaxin.proto.core.ClientProto;
import com.akaxin.proto.platform.ApiUserPhoneProto;
import com.akaxin.proto.platform.ApiUserPushTokenProto;
import com.akaxin.proto.platform.ApiUserRealNameProto;

/**
 * service:ApiUserInfo methods:
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 */
public class ApiUserHandler extends AbstractApiHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ApiUserHandler.class);

	/**
	 * 提交用户客户端的pushtoken，用户平台发送PUSH使用
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse pushToken(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiUserPushTokenProto.ApiUserPushTokenRequest request = ApiUserPushTokenProto.ApiUserPushTokenRequest
					.parseFrom(command.getParams());
			ClientProto.ClientType clientType = request.getClientType();
			String rom = request.getRom();
			String pushToken = request.getPushToken();
			String deviceId = command.getDeviceId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			UserBean userBean = new UserBean();
			userBean.setUserId(command.getGlobalUserId());
			userBean.setClientType(clientType.getNumber());
			userBean.setRom(rom);
			userBean.setPushToken(pushToken);
			userBean.setDeviceId(deviceId);
			logger.debug("userInfoBean=" + userBean.toString());

			if (UserInfoDao.getInstance().saveUserInfo(userBean)) {
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR2_USER_SAVE_PUSHTOKEN;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * 实名认证,绑定手机号码以及设置密码
	 * 
	 */
	public CommandResponse realName(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			ApiUserRealNameProto.ApiUserRealNameRequest request = ApiUserRealNameProto.ApiUserRealNameRequest
					.parseFrom(command.getParams());
			String userIdPrik = request.getUserIdPrik();
			String userIdPubk = request.getUserIdPubk();
			String globalUserId = UserIdUtils.getV1GlobalUserId(userIdPubk);
			String phoneId = request.getPhoneId();
			String verifyCode = request.getPhoneVerifyCode();
			int vcType = request.getVcType();
			LogUtils.requestDebugLog(logger, command, request.toString());

			// 验证条件
			// 1.判断参数是否合法
			if (StringUtils.isNoneEmpty(userIdPrik, userIdPubk, globalUserId, phoneId, verifyCode)) {
				// 2.验证手机格式是否合法
				if (ValidatorPattern.isPhoneId(phoneId)) {
					String phoneId2 = UserInfoDao.getInstance().getUserPhoneId(globalUserId);
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
							String vcKey = phoneId + "_" + vcType;
							String dbVerifyCode = PhoneVCTokenDao.getInstance().getPhoneVC(vcKey);
							if (StringUtils.isNotEmpty(dbVerifyCode) && dbVerifyCode.equals(verifyCode)) {
								UserBean bean = new UserBean();
								bean.setUserId(globalUserId);
								bean.setUserIdPrik(userIdPrik);
								bean.setUserIdPubk(userIdPubk);
								bean.setPhoneId(phoneId);
								bean.setCountryCode("+86");
								logger.debug("Phone code={} realCode={} bean={}", verifyCode, dbVerifyCode,
										bean.toString());
								if (UserInfoDao.getInstance().updatePhoneInfo(bean)) {
									errorCode = ErrorCode2.SUCCESS;

									// 实名完成，则删除key
									PhoneVCTokenDao.getInstance().delPhoneVC(vcKey);
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
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 获取用户绑定的手机号码
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse phone(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode.ERROR;
		try {
			ApiUserPhoneProto.ApiUserPhoneRequest request = ApiUserPhoneProto.ApiUserPhoneRequest
					.parseFrom(command.getParams());
			String globalUserId = command.getGlobalUserId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(globalUserId)) {
				throw new ErrCodeException(ErrorCode.ERROR_PARAMETER);
			}

			String phoneId = UserInfoDao.getInstance().getUserPhoneId(globalUserId);

			if (StringUtils.isNotEmpty(phoneId)) {
				ApiUserPhoneProto.ApiUserPhoneResponse response = ApiUserPhoneProto.ApiUserPhoneResponse.newBuilder()
						.setCountryCode("+86").setPhoneId(phoneId).build();
				commandResponse.setParams(response.toByteArray());
			}
			errCode = ErrorCode.SUCCESS;
		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ErrCodeException) e).getErrCode();
			} else {
				errCode = ErrorCode.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

}
