package com.akaxin.platform.connector.handler;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.platform.connector.codec.parser.ParserConst;
import com.akaxin.platform.operation.service.MesageService;
import com.akaxin.proto.core.CoreProto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-06 12:32:10
 */
public class NettyInboundHandler extends SimpleChannelInboundHandler<RedisCommand> {
	private static final Logger logger = LoggerFactory.getLogger(NettyInboundHandler.class);

	/**
	 * 用户建立连接到服务端,会激活channel
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(ParserConst.CHANNELSESSION).set(new ChannelSession(ctx.channel()));
		// logger.info("connect to platform client={}", ctx.channel().toString());
	}

	/**
	 * 关闭channel
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ChannelSession channelSession = ctx.channel().attr(ParserConst.CHANNELSESSION).get();
		if (channelSession.getCtype() == 1) {
			ChannelManager.delChannelSession(channelSession.getDeviceId());
		}
		logger.info("close netty channel connection...client={}", ctx.channel().toString());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RedisCommand redisCommand) throws Exception {
		try {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			String clientIP = insocket.getAddress().getHostAddress();
			ChannelSession channelSession = ctx.channel().attr(ParserConst.CHANNELSESSION).get();

			String version = redisCommand.getParameterByIndex(0);
			String action = redisCommand.getParameterByIndex(1);
			byte[] params = redisCommand.getBytesParamByIndex(2);

			CoreProto.TransportPackageData packageData = CoreProto.TransportPackageData.parseFrom(params);

			Command command = new Command();
			command.setSiteUserId(channelSession.getUserId());
			command.setDeviceId(channelSession.getDeviceId());
			command.setAction(action);
			command.setChannelSession(channelSession);
			command.setParams(packageData.getData().toByteArray());
			command.setHeader(packageData.getHeaderMap());

			if (!"ping".equals(command.getMethod())) {
				logger.info("client id:{} request command:{}", clientIP, command.toString());
			}

			if (RequestAction.IM.getName().equals(command.getRety())) {
				new MesageService().doImRequest(command);
			} else if (RequestAction.API.getName().equals(command.getRety())) {
				CommandResponse commandResponse = new MesageService().doApiRequest(command);
				ChannelWriter.writeAndClose(ctx.channel(), commandResponse);
			} else {
				logger.error("unknow request command = {}", command.toString());
			}

		} catch (Exception e) {
			logger.error("receive from client error.", e);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("netty server: exception caught.", cause);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		logger.info("netty server: user event triggered.");
	}

}
