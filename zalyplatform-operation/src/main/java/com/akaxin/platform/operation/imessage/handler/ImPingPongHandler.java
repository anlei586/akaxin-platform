package com.akaxin.platform.operation.imessage.handler;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.platform.common.constant.CommandConst;
import com.akaxin.platform.operation.constant.PlatformAction;
import com.akaxin.proto.core.CoreProto;

/**
 * ping && pong
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ImPingPongHandler extends AbstractImHandler<Command, Boolean> {
	private static final Logger logger = LoggerFactory.getLogger(ImPingPongHandler.class);

	@Override
	public Boolean handle(Command command) {
		try {
			String deviceId = command.getDeviceId();
			ChannelSession channelSession = command.getChannelSession();

			if (ChannelManager.getChannelSession(deviceId) != null) {
				CoreProto.TransportPackageData.Builder pongPackageBuilder = CoreProto.TransportPackageData.newBuilder();
				pongPackageBuilder.putAllHeader(new HashMap<Integer, String>());
				channelSession.getChannel().writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION)
						.add(PlatformAction.IM_PTC_PONG).add(pongPackageBuilder.build().toByteArray()));
			}
		} catch (Exception e) {
			logger.error("ping pong error.", e);
		}
		return true;
	}

}