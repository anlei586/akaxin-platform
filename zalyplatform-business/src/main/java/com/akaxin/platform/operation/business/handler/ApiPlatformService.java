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
import com.akaxin.common.utils.UserIdUtils;
import com.akaxin.platform.operation.business.dao.SessionDao;
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
public class ApiPlatformService extends AbstractApiHandler<Command> {
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
	public boolean login(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiPlatformLoginProto.ApiPlatformLoginRequest loginRequest = ApiPlatformLoginProto.ApiPlatformLoginRequest
					.parseFrom(command.getParams());
			String userIdPubk = loginRequest.getUserIdPubk();
			String userIdSignBase64 = loginRequest.getUserIdSignBase64();
			String userDeviceIdPubk = loginRequest.getUserDeviceIdPubk();
			String userDeviceIdSignBase64 = loginRequest.getUserDeviceIdSignBase64();
			logger.info("api.platform.login command={} request={}", command.toString(), loginRequest.toString());
			logger.info("user_id_pubk={}", userIdPubk);
			logger.info("userIdSignBase64={}", userIdSignBase64);
			logger.info("device_id_pubk={}", userDeviceIdPubk);
			logger.info("userDeviceIdSignBase64={}", userDeviceIdSignBase64);

			if (StringUtils.isNoneEmpty(userIdPubk, userIdSignBase64, userDeviceIdPubk, userDeviceIdSignBase64)) {
				String userId = UserIdUtils.getV1GlobalUserId(userIdPubk);
				logger.info("globalUserId={}", userId);

				PublicKey userPubKey = RSACrypto.getRSAPubKeyFromPem(userIdPubk);// 个人身份公钥，解密Sign签名，解密Key
				Signature userSign = Signature.getInstance("SHA512withRSA");
				userSign.initVerify(userPubKey);
				userSign.update(userIdPubk.getBytes());// 原文
				boolean userSignResult = userSign.verify(Base64.getDecoder().decode(userIdSignBase64));
				logger.info("userSignResult={}", userSignResult);
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

					String sessionKey = RedisKeyUtils.getSessionKey(sessionId);
					Map<String, String> sessionMap = new HashMap<String, String>();
					sessionMap.put(UserKey.userId, userId);
					sessionMap.put(UserKey.deviceId, deviceId);

					if (SessionDao.getInstance().addSession(sessionKey, sessionMap)) {
						ApiPlatformLoginProto.ApiPlatformLoginResponse response = ApiPlatformLoginProto.ApiPlatformLoginResponse
								.newBuilder().setUserId(userId).setSessionId(sessionId).build();
						commandResponse.setParams(response.toByteArray());
						errCode = ErrorCode2.SUCCESS;
					}

					// 更新最新一次登陆的用户信息，用于发送push，最后登陆用户，支持接受push
					UserInfoDao.getInstance().updateUserInfo(userId, sessionMap);
				} else {
					errCode = ErrorCode2.ERROR2_LOGGIN_ERRORSIGN;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;// 参数错误
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api.platform.login exception.", e);
		}
		command.setResponse(commandResponse.setErrCode2(errCode));
		logger.info("api.platform.login result={}", errCode.toString());
		return errCode.isSuccess();
	}

	public boolean topSecret(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiPlatformTopSecretRequest request = ApiPlatformTopSecretRequest.parseFrom(command.getParams());
			logger.info("api.platform.topSecret command={} request={}", command.toString(), request.toString());

			// Random random = new Random();
			// boolean supportTS = random.nextBoolean();
			boolean supportTS = true;
			logger.info("api.platform.topSecret supportTS={}", supportTS);
			ApiPlatformTopSecretResponse response = ApiPlatformTopSecretResponse.newBuilder()
					.setOpenTopSecret(supportTS).build();
			commandResponse.setParams(response.toByteArray());
			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api.platform.topSecret exception.", e);
		}
		command.setResponse(commandResponse.setErrCode2(errCode));

		logger.info("api.platform.topSecret result={}", errCode.toString());
		return errCode.isSuccess();
	}

}
