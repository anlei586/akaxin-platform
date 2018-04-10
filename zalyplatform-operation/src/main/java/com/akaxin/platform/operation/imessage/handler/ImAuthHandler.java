package com.akaxin.platform.operation.imessage.handler;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.ServerAddressUtils;
import com.akaxin.platform.operation.business.dao.SessionDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.constant.UserKey;
import com.akaxin.proto.platform.ImPlatformAuthProto;

import io.netty.channel.Channel;

/**
 * <pre>
 * 	平台认证客户端行为
 * 	return false，认证失败，断开连接
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-10-17 15:25:44
 *
 */
public class ImAuthHandler extends AbstractImHandler<Command, Boolean> {
	private static final Logger logger = LoggerFactory.getLogger(ImAuthHandler.class);

	@Override
	public Boolean handle(Command command) {
		boolean result = false;
		try {
			ChannelSession channelSession = command.getChannelSession();
			ImPlatformAuthProto.ImPlatformAuthRequest request = ImPlatformAuthProto.ImPlatformAuthRequest
					.parseFrom(command.getParams());
			String globalUserId = request.getUserId();
			String sessionId = request.getSessionId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			String sessionKey = RedisKeyUtils.getSessionKey(sessionId);
			logger.info("auth action session redis key={}", sessionKey);

			Map<String, String> map = SessionDao.getInstance().getSessionMap(sessionKey);

			String userId = map.get(UserKey.userId);
			String deviceId = map.get(UserKey.deviceId);

			logger.info("auth userId={} deviceId={}", userId, deviceId);

			if (StringUtils.isNotBlank(userId) && userId.equals(globalUserId) && StringUtils.isNotBlank(deviceId)) {
				channelSession.setCtype(1); // 长连接
				channelSession.setUserId(globalUserId);
				channelSession.setDeviceId(deviceId);
				ChannelManager.addChannelSession(deviceId, channelSession);
				result = true;
				// 更新用户最新的设备id
				UserInfoDao.getInstance().updateUserField(globalUserId, UserKey.deviceId, deviceId);
			}
			logger.info("auth result={},sessionSize={}", result, ChannelManager.getChannelSessionSize());
			authResponse(channelSession.getChannel(), command, result);
		} catch (Exception e) {
			logger.error("im auth error.", e);
		}
		return result;
	}

	private void authResponse(Channel channel, Command command, boolean result) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ErrorCode errorCode = ErrorCode.ERROR_SESSION;
		if (result) {
			String serverAddress = ServerAddressUtils.getAddressPort();
			ImPlatformAuthProto.ImPlatformAuthResponse response = ImPlatformAuthProto.ImPlatformAuthResponse
					.newBuilder().setPlatformServer(serverAddress).build();
			commandResponse.setParams(response.toByteArray());
			errorCode = ErrorCode.SUCCESS;
		}
		ChannelWriter.write(channel, commandResponse.setErrCode(errorCode));
	}
}