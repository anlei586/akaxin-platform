package com.akaxin.platform.operation.imessage.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.platform.ImPlatformHelloProto;
import com.akaxin.proto.site.ImSiteHelloProto;

import io.netty.channel.Channel;

/**
 * 平台:处理客户端auth行为
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ImHelloHandler extends AbstractImHandler<Command, Boolean> {
	private static final Logger logger = LoggerFactory.getLogger(ImHelloHandler.class);

	@Override
	public Boolean handle(Command command) {
		try {
			ImPlatformHelloProto.ImPlatformHelloRequest request = ImPlatformHelloProto.ImPlatformHelloRequest
					.parseFrom(command.getParams());
			LogUtils.requestDebugLog(logger, command, request.toString());

			ChannelSession channelSession = command.getChannelSession();
			helloResponse(channelSession.getChannel());

			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}
		return false;
	}

	private void helloResponse(Channel channel) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		String siteVersion = CommandConst.SITE_VERSION;
		ImSiteHelloProto.ImSiteHelloResponse response = ImSiteHelloProto.ImSiteHelloResponse.newBuilder()
				.setSiteVersion(siteVersion).build();
		commandResponse.setErrCode(ErrorCode.SUCCESS);
		commandResponse.setParams(response.toByteArray());
		// must use channel in command before im connection authed
		ChannelWriter.write(channel, commandResponse);
	}
}