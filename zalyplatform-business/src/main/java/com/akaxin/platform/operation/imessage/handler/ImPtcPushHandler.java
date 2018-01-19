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
import com.akaxin.proto.core.CoreProto;
import com.google.protobuf.ByteString;

/**
 * 发送Push给客户端
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ImPtcPushHandler extends AbstractImHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ImPtcPushHandler.class);

	@Override
	public boolean handle(Command command) {
		try {
			logger.info("---------api.ptc.push----------");
			String deviceId = command.getDeviceId();
			ChannelSession channelSession = ChannelManager.getChannelSession(deviceId);
			logger.info("im ptc push command={}", command.toString());
			if (channelSession != null) {
				ImPtcPushProto.ImPtcPushRequest ptcPushRequest = ImPtcPushProto.ImPtcPushRequest
						.parseFrom(command.getParams());

				CoreProto.TransportPackageData.Builder pushPackageBuilder = CoreProto.TransportPackageData.newBuilder();
				pushPackageBuilder.putAllHeader(new HashMap<Integer, String>());
				pushPackageBuilder.setData(ByteString.copyFrom(ptcPushRequest.toByteArray()));
				channelSession.getChannel().writeAndFlush(new RedisCommand().add(CommandConst.SITE_VERSION)
						.add("im.ptc.push").add(pushPackageBuilder.build().toByteArray()));
				return true;
			}
		} catch (Exception e) {
			logger.error("im ptc push error", e);
		}
		return false;
	}

}