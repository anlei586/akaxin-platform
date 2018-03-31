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
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.crypto.HashCrypto;
import com.akaxin.common.crypto.RSACrypto;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.common.utils.UserIdUtils;
import com.akaxin.platform.operation.business.dao.SessionDao;
import com.akaxin.platform.operation.business.dao.UserDeviceDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.constant.UserKey;
import com.akaxin.proto.platform.ApiPlatformLoginProto;
import com.akaxin.proto.platform.ApiPlatformTopSecretProto.ApiPlatformTopSecretRequest;
import com.akaxin.proto.platform.ApiPlatformTopSecretProto.ApiPlatformTopSecretResponse;

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
		ErrorCode2 errCode = ErrorCode2.ERROR;
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

					int sessionExpireTime = 90 * 24 * 60 * 60;// 90天
					if (SessionDao.getInstance().addSessionMap(sessionKey, sessionMap, sessionExpireTime)) {
						ApiPlatformLoginProto.ApiPlatformLoginResponse response = ApiPlatformLoginProto.ApiPlatformLoginResponse
								.newBuilder().setUserId(globalUserId).setSessionId(sessionId).build();
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
		return commandResponse.setErrCode2(errCode);
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
		ErrorCode2 errCode = ErrorCode2.ERROR;
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

		return commandResponse.setErrCode2(errCode);
	}

}
