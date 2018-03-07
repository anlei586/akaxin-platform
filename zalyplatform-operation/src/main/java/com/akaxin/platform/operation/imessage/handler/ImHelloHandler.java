package com.akaxin.platform.operation.imessage.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.proto.site.ImSiteHelloProto;

import io.netty.channel.Channel;

/**
 * 平台:处理客户端auth行为
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ImHelloHandler extends AbstractImHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ImHelloHandler.class);

	@Override
	public boolean handle(Command command) {
		try {
			logger.info("im.platform.hello command={}", command.toString());
			ChannelSession channelSession = command.getChannelSession();
			helloResponse(channelSession.getChannel());
			return true;
		} catch (Exception e) {
			logger.error("im auth error.", e);
		}
		return false;
	}

	private void helloResponse(Channel channel) {
		logger.info("------ hello response ------");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ImSiteHelloProto.ImSiteHelloResponse response = ImSiteHelloProto.ImSiteHelloResponse.newBuilder()
				.setSiteVersion(CommandConst.SITE_VERSION).build();
		commandResponse.setErrCode(ErrorCode.SUCCESS);
		commandResponse.setParams(response.toByteArray());
		ChannelWriter.write(channel, commandResponse);
	}
}