package com.akaxin.platform.operation.imessage.handler;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.platform.operation.constant.PlatformAction;
import com.akaxin.proto.client.ImPtcPushProto;
import com.akaxin.proto.core.CoreProto;
import com.google.protobuf.ByteString;

/**
 * <pre>
 * 发送Push给客户端
 * return true:服务端不会断开IM长链接
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 17:44:59
 */
public class ImPtcPushHandler extends AbstractImHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ImPtcPushHandler.class);

	@Override
	public boolean handle(Command command) {
		try {
			logger.info("---------im.ptc.push----------");
			String deviceId = command.getDeviceId();
			if (StringUtils.isEmpty(deviceId)) {
				logger.error("im.ptc.push without deviceId={}", deviceId);
				return true;
			}
			ChannelSession channelSession = ChannelManager.getChannelSession(deviceId);
			logger.info("im ptc push command={}", command.toString());
			if (channelSession != null) {
				ImPtcPushProto.ImPtcPushRequest ptcPushRequest = ImPtcPushProto.ImPtcPushRequest
						.parseFrom(command.getParams());

				CoreProto.TransportPackageData.Builder pushPackageBuilder = CoreProto.TransportPackageData.newBuilder();
				pushPackageBuilder.putAllHeader(new HashMap<Integer, String>());
				pushPackageBuilder.setData(ByteString.copyFrom(ptcPushRequest.toByteArray()));
				channelSession.getChannel().writeAndFlush(new RedisCommand().add(CommandConst.PROTOCOL_VERSION)
						.add(PlatformAction.IM_PTC_PUSH).add(pushPackageBuilder.build().toByteArray()));
			} else {
				logger.info("im.ptc.push fail,user is offline command={}", command.toString());
			}
		} catch (Exception e) {
			logger.error("im ptc push error", e);
		}
		return true;
	}

}