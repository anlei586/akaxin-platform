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
import com.akaxin.proto.platform.ApiPushNotificationProto;
import com.google.protobuf.ByteString;
import com.zaly.proto.core.CoreProto;

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
			logger.error("---------api.ptc.push----------");
			String deviceId = command.getDeviceId();
			ChannelSession channelSession = ChannelManager.getChannelSession(deviceId);

			if (channelSession != null) {
				ApiPushNotificationProto.ApiPushNotificationRequest apnr = ApiPushNotificationProto.ApiPushNotificationRequest
						.parseFrom(command.getParams());

				CoreProto.TransportPackageData.Builder pushPackageBuilder = CoreProto.TransportPackageData.newBuilder();
				pushPackageBuilder.putAllHeader(new HashMap<Integer, String>());

				ImPtcPushProto.ImPtcPushRequest.Builder ipprBuilder = ImPtcPushProto.ImPtcPushRequest.newBuilder();

				if (apnr.getUserId() != null) {

				}
				ipprBuilder.setPushAlert("test im push");
				ipprBuilder.setPushBadge(apnr.getPushBadge());
				if (apnr.getSiteServerName() != null) {
					ipprBuilder.setSiteServerName(apnr.getSiteServerName());
				}

				pushPackageBuilder.setData(ByteString.copyFrom(ipprBuilder.build().toByteArray()));
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