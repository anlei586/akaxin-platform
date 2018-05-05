package com.akaxin.platform.operation.business.handler;

import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
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
import com.akaxin.platform.common.crypto.HashCrypto;
import com.akaxin.platform.common.crypto.RSACrypto;
import com.akaxin.platform.common.exceptions.ErrCodeException;
import com.akaxin.platform.common.utils.StringHelper;
import com.akaxin.platform.common.utils.UserIdUtils;
import com.akaxin.platform.operation.business.dao.SessionDao;
import com.akaxin.platform.operation.business.dao.UserDeviceDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.constant.UserKey;
import com.akaxin.proto.platform.ApiPlatformLoginProto;
import com.akaxin.proto.platform.ApiPlatformLogoutProto;
import com.akaxin.proto.platform.ApiPlatformTopSecretProto.ApiPlatformTopSecretRequest;
import com.akaxin.proto.platform.ApiPlatformTopSecretProto.ApiPlatformTopSecretResponse;
import com.akaxin.proto.site.ApiPlatformRegisterByPhoneProto.ApiPlatformRegisterByPhoneRequest;
import com.akaxin.proto.site.ApiPlatformRegisterByPhoneProto.ApiPlatformRegisterByPhoneResponse;

/**
 * 
 * 客户端登陆平台
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-06 11:08:19
 */
public class ApiPlatformService extends AbstractApiHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ApiPlatformService.class);

	/**
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse registerByPhone(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiPlatformRegisterByPhoneRequest request = ApiPlatformRegisterByPhoneRequest
					.parseFrom(command.getParams());
			String userIdPrik = request.getUserIdPrik();
			String userIdPubk = request.getUserIdPubk();
			String pushToken = request.getPushToken();
			String phontVC = request.getPhoneVerifyCode();
			String phoneId = request.getPhoneId();
			String countryCode = request.getCountryCode();
			int vcType = request.getVcType();

			// 1.校验参数

			// 2.验证手机号与验证码

			// 3.验证手机是否绑定

			// 4-1 成功：保存注册信息
			// 4-2 失败：返回手机号对应公司要
			ApiPlatformRegisterByPhoneResponse.Builder responseBuilder = ApiPlatformRegisterByPhoneResponse
					.newBuilder();
			responseBuilder.setUserIdPrik("");
			responseBuilder.setUserIdPubk("");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commandResponse;
	}

	/**
	 * <pre>
	 * 用户登陆站点
	 * 	1.保存身份信息
	 * 	2.验证身份，生成sessionId
	 * </pre>
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse login(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiPlatformLoginProto.ApiPlatformLoginRequest loginRequest = ApiPlatformLoginProto.ApiPlatformLoginRequest
					.parseFrom(command.getParams());
			String userIdPubk = loginRequest.getUserIdPubk();
			String userIdSignBase64 = loginRequest.getUserIdSignBase64();
			String userDeviceIdPubk = loginRequest.getUserDeviceIdPubk();
			String userDeviceIdSignBase64 = loginRequest.getUserDeviceIdSignBase64();
			LogUtils.requestDebugLog(logger, command, loginRequest.toString());

			if (StringUtils.isNoneEmpty(userIdPubk, userIdSignBase64, userDeviceIdPubk, userDeviceIdSignBase64)) {
				String globalUserId = UserIdUtils.getV1GlobalUserId(userIdPubk);

				PublicKey userPubKey = RSACrypto.getRSAPubKeyFromPem(userIdPubk);// 个人身份公钥，解密Sign签名，解密Key
				Signature userSign = Signature.getInstance("SHA512withRSA");
				userSign.initVerify(userPubKey);
				userSign.update(userIdPubk.getBytes());// 原文
				boolean userSignResult = userSign.verify(Base64.getDecoder().decode(userIdSignBase64));
				logger.debug("userSignResult={}", userSignResult);

				if (userSignResult) {
					Signature userDeviceSign = Signature.getInstance("SHA512withRSA");
					userDeviceSign.initVerify(userPubKey);
					userDeviceSign.update(userDeviceIdPubk.getBytes());// 原文
					userSignResult = userDeviceSign.verify(Base64.getDecoder().decode(userDeviceIdSignBase64));
				}

				logger.info("deviceSignResult={}", userSignResult);
				if (userSignResult) {
					// 随机生成sessionid
					String sessionId = UUID.randomUUID().toString();
					String deviceId = HashCrypto.MD5(userDeviceIdPubk);

					// 绑定新的sessionId和deviceId
					bindNewDeviceAndSession(globalUserId, deviceId, sessionId);

					// 设置用户的session入库
					String sessionKey = RedisKeyUtils.getSessionKey(sessionId);
					Map<String, String> sessionMap = new HashMap<String, String>();
					sessionMap.put(UserKey.userId, globalUserId);
					sessionMap.put(UserKey.deviceId, deviceId);
					sessionMap.put(UserKey.TIME, System.currentTimeMillis() + "");

					int sessionExpireTime = 1 * 24 * 60 * 60;// 90天
					if (SessionDao.getInstance().addSessionMap(sessionKey, sessionMap, sessionExpireTime)) {
						ApiPlatformLoginProto.ApiPlatformLoginResponse response = ApiPlatformLoginProto.ApiPlatformLoginResponse
								.newBuilder().setGlobalUserId(globalUserId).setSessionId(sessionId).build();
						commandResponse.setParams(response.toByteArray());
						errCode = ErrorCode2.SUCCESS;
					}

					// 更新最新一次登陆的用户信息，用于发送push，最后登陆用户，支持接受push
					UserInfoDao.getInstance().updateUserInfo(globalUserId, sessionMap);
				} else {
					errCode = ErrorCode2.ERROR2_LOGGIN_ERRORSIGN;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;// 参数错误
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	// 绑定新的sessionid与deviceid之间的关系
	private void bindNewDeviceAndSession(String globalUserId, String deviceId, String sessionId) {
		try {
			// 删除老的sessionId，防止session库积累过多
			String deviceKey = RedisKeyUtils.getUserDeviceKey(deviceId);
			String oldSessionId = UserDeviceDao.getInstance().getDeviceField(deviceKey, UserKey.sessionId);
			if (StringUtils.isNotEmpty(oldSessionId)) {
				String oldSessionKey = RedisKeyUtils.getSessionKey(oldSessionId);
				SessionDao.getInstance().deleteSessionKey(oldSessionKey);
			}

			Map<String, String> deviceMap = new HashMap<String, String>();
			deviceMap.put(UserKey.userId, globalUserId);
			deviceMap.put(UserKey.sessionId, sessionId);
			deviceMap.put(UserKey.TIME, System.currentTimeMillis() + "");
			UserDeviceDao.getInstance().addDevicemap(deviceKey, deviceMap);
		} catch (Exception e) {
			logger.error(StringHelper.format("userId={} bind new session={} and device={} error", globalUserId,
					sessionId, deviceId), e);
		}
	}

	public CommandResponse topSecret(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiPlatformTopSecretRequest request = ApiPlatformTopSecretRequest.parseFrom(command.getParams());
			boolean supportTS = true;
			LogUtils.requestDebugLog(logger, command, request.toString());

			ApiPlatformTopSecretResponse response = ApiPlatformTopSecretResponse.newBuilder()
					.setOpenTopSecret(supportTS).build();
			commandResponse.setParams(response.toByteArray());
			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}

		return commandResponse.setErrCode(errCode);
	}

	public CommandResponse logout(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiPlatformLogoutProto.ApiPlatformLogoutRequest request = ApiPlatformLogoutProto.ApiPlatformLogoutRequest
					.parseFrom(command.getParams());
			String globalUserId = command.getGlobalUserId();
			String userIdPubk = request.getUserIdPubk();
			String deviceIdPubk = request.getDeviceIdPubk();
			String userId = UserIdUtils.getV1GlobalUserId(userIdPubk);
			String deviceId = HashCrypto.MD5(deviceIdPubk);
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(globalUserId, userIdPubk, deviceIdPubk, deviceId)) {
				logger.error("globalUserId={} action={} userId={} userIdPubk={} deviceId={} deviceIdPubk={}",
						globalUserId, command.getAction(), userId, userIdPubk, deviceId, deviceIdPubk);
				throw new ErrCodeException(ErrorCode.ERROR_PARAMETER);
			}

			if (!globalUserId.equals(userId)) {
				logger.error("globalUserId={} userId={} action={}", globalUserId, userId, command.getAction());
				throw new ErrCodeException(ErrorCode.ERROR_USER_ID);
			}

			// 获取用户当前的deviceId
			String latestDeviceId = UserInfoDao.getInstance().getLatestDeviceId(globalUserId);
			if (deviceId.equals(latestDeviceId)) {// remove push token
				if (UserInfoDao.getInstance().delUserField(userId, UserKey.pushToken)) {
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.SUCCESS;
			}

		} catch (Exception e) {
			if (e instanceof ErrCodeException) {
				errCode = ((ErrCodeException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

}
