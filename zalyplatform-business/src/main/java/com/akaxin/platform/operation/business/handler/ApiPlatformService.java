package com.akaxin.platform.operation.business.handler;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.crypto.HashCrypto;
import com.akaxin.common.crypto.RSACrypto;
import com.akaxin.platform.operation.business.dao.SessionDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.proto.platform.ApiPlatformLoginProto;
import com.zaly.platform.storage.constant.UserKey;

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
		logger.info("----------api.platform.login--------");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errCode = ErrorCode.ERROR;
		try {
			ApiPlatformLoginProto.ApiPlatformLoginRequest loginRequest = ApiPlatformLoginProto.ApiPlatformLoginRequest
					.parseFrom(command.getParams());

			String userIdPubk = loginRequest.getUserIdPubk();
			String userDeviceIdPubk = loginRequest.getUserDeviceIdPubk();
			String userDeviceIdSign = loginRequest.getUserDeviceIdSign();
			String userId = HashCrypto.SHA1(userIdPubk);

			logger.info("user_id_pubk={}", userIdPubk);
			logger.info("device_id_pubk={}", userDeviceIdPubk);
			logger.info("device_sign={}", userDeviceIdSign);

			// 验证登陆信息合法性
			// 登陆之前，涉及到使用用户公私钥验证用户本人
			// 使用user_device_id_sign验证user_device_id_pubk合法性
			// #TODO RSA 处理
			/**
			 * deviceSign = Base64(RSA(SHA1(device_id_pubk),user_id_prik))
			 */
			try {
				String sha1edDeviceIdPubk1 = HashCrypto.SHA1(userDeviceIdPubk);// sha1(devicePubk)
				byte[] base64DecodedSign = Base64.getDecoder().decode(userDeviceIdSign);// 需要被解密的内容
				PublicKey userPubKey = RSACrypto.getRSAPubKeyFromPem(userIdPubk);// 个人身份公钥，解密Sign签名，解密Key
				byte[] sha1edDeviceIdPubk2 = RSACrypto.decrypt(userPubKey, base64DecodedSign);

				if (Arrays.equals(sha1edDeviceIdPubk1.getBytes(), sha1edDeviceIdPubk2)) {
					logger.info("=======check userDevicePubk AND DeviceIdSign AND userIdPubk Sucess.");
				}
				logger.info("sha1edDeviceIdPubk1={} equals={}", sha1edDeviceIdPubk1.getBytes(),
						sha1edDeviceIdPubk1.equals(sha1edDeviceIdPubk2));
				logger.info("sha1edDeviceIdPubk2={}", sha1edDeviceIdPubk2);
			} catch (Exception e) {
				logger.error("test user device Pubk.", e);
			}
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
				errCode = ErrorCode.SUCCESS;
				commandResponse.setParams(response.toByteArray());

				errCode = ErrorCode.SUCCESS;
			}

			// 更新最新一次登陆的用户信息，用于发送push，最后登陆用户，支持接受push
			UserInfoDao.getInstance().updateUserInfo(userId, sessionMap);
			
			logger.info("------login platform finish------");
		} catch (Exception e) {
			commandResponse.setErrInfo("Login exception!");
			logger.error("login exception.", e);
		}
		command.setResponse(commandResponse.setErrCode(errCode));
		return false;
	}

}
