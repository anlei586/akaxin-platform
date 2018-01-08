package com.akaxin.platform.operation.imessage.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.utils.ServerAddressUtils;
import com.akaxin.proto.platform.ImPlatformAuthProto;

import io.netty.channel.Channel;

/**
 * 平台:处理客户端auth行为
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ImAuthHandler extends AbstractImHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ImAuthHandler.class);

	@Override
	public boolean handle(Command command) {
		try {
			logger.info("------ auth handler ------");
			ChannelSession channelSession = command.getChannelSession();
			ImPlatformAuthProto.ImPlatformAuthRequest request = ImPlatformAuthProto.ImPlatformAuthRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getUserId();
			String sessionId = request.getSessionId();

			logger.info("auth action command={}", command.toString());

			channelSession.setCtype(1); // 长连接
			channelSession.setUserId(siteUserId);
			channelSession.setDeviceId(sessionId);
			ChannelManager.addChannelSession(sessionId, channelSession);

			authResponse(channelSession.getChannel(), command, true);
			return true;
		} catch (Exception e) {
			logger.error("im auth error.", e);
		}
		return false;
	}

	private void authResponse(Channel channel, Command command, boolean result) {
		logger.info("----- auth response ------");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		if (result) {
			String serverAddress = ServerAddressUtils.getAddressPort();
			ImPlatformAuthProto.ImPlatformAuthResponse response = ImPlatformAuthProto.ImPlatformAuthResponse
					.newBuilder().setPlatformServer(serverAddress).build();
			commandResponse.setParams(response.toByteArray());
			errorCode = ErrorCode.SUCCESS;
		} else {
			commandResponse.setErrInfo("auth fail.");
		}
		ChannelWriter.write(channel, commandResponse.setErrCode(errorCode));
	}
}