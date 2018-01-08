package com.akaxin.platform.operation.imessage.handler;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.proto.client.ImPtcPushProto;
import com.google.protobuf.ByteString;
import com.zaly.proto.core.CoreProto;

/**
 * ping && pong
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ImPingPongHandler extends AbstractImHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ImPingPongHandler.class);

	@Override
	public boolean handle(Command command) {
		try {
			String deviceId = command.getDeviceId();
			ChannelSession channelSession = command.getChannelSession();

			if (ChannelManager.getChannelSession(deviceId) != null) {
				CoreProto.TransportPackageData.Builder pongPackageBuilder = CoreProto.TransportPackageData.newBuilder();
				pongPackageBuilder.putAllHeader(new HashMap<Integer, String>());
				channelSession.getChannel().writeAndFlush(new RedisCommand().add(CommandConst.SITE_VERSION)
						.add("im.ptc.pong").add(pongPackageBuilder.build().toByteArray()));

				ImPtcPushProto.ImPtcPushRequest request = ImPtcPushProto.ImPtcPushRequest.newBuilder()
						.setPushAlert("test im push").setPushBadge(1).setSiteServerName("test server").build();

				pongPackageBuilder.setData(ByteString.copyFrom(request.toByteArray()));
				channelSession.getChannel().writeAndFlush(new RedisCommand().add(CommandConst.SITE_VERSION)
						.add("im.ptc.push").add(pongPackageBuilder.build().toByteArray()));
			}
			return true;
		} catch (Exception e) {
			logger.error("ping pong error.", e);
		}
		return false;
	}

}