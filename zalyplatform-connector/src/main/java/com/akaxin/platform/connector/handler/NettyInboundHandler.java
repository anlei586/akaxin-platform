package com.akaxin.platform.connector.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.platform.business.service.MesageService;
import com.akaxin.platform.connector.codec.parser.ParserConst;
import com.google.protobuf.ByteString;
import com.zaly.proto.core.CoreProto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyInboundHandler extends SimpleChannelInboundHandler<RedisCommand> {
	private static final Logger logger = LoggerFactory.getLogger(NettyInboundHandler.class);
	private AbstracteExecutor<Command> executor;

	public NettyInboundHandler(AbstracteExecutor<Command> executor) {
		this.executor = executor;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		/**
		 * 用户建立连接到服务端，执行此方法。
		 */
		ctx.channel().attr(ParserConst.CHANNELSESSION).set(new ChannelSession(ctx.channel()));
		logger.info("client connect to platform");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("client close the connection");
		// ChannelSession channelSession =
		// ctx.channel().attr(ParserConst.CHANNELSESSION).get();
		// ChannelManager.getInstance().delChannel(channelSession.getUserId());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RedisCommand redisCommand) throws Exception {
		try {
			logger.info("-------Receive data from client-------");

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

			// 进行session认证
			Map<Integer, String> header = packageData.getHeaderMap();
			String siteSessionId = header.get(CoreProto.HeaderKey.CLIENT_SOCKET_SITE_SESSION_ID_VALUE);

			System.out.println("API.Plt 请求 sessionId  =" + siteSessionId);
			logger.info("API.Plt 请求 sessionId  =" + siteSessionId);

			command.setParams(packageData.getData().toByteArray());
			CommandResponse commandResponse = new MesageService().executor(this.executor, command);

			CoreProto.TransportPackageData.Builder packageBuilder = CoreProto.TransportPackageData.newBuilder();
			// response
			CoreProto.ErrorInfo errinfo = CoreProto.ErrorInfo.newBuilder()
					.setCode(String.valueOf(commandResponse.getErrCode()))
					.setInfo(String.valueOf(commandResponse.getErrInfo())).build();
			packageBuilder.setErr(errinfo).putAllHeader(new HashMap<Integer, String>());

			if (commandResponse.getParams() != null) {
				packageBuilder.setData(ByteString.copyFrom(commandResponse.getParams())).build();
				logger.info("commandResponse Size=" + commandResponse.getParams().length);
			}

			CoreProto.TransportPackageData resPackageData = packageBuilder.build();

			ctx.channel().writeAndFlush(new RedisCommand().add(CommandConst.VERSION).add(commandResponse.getAction())
					.add(resPackageData.toByteArray()));
		} catch (Exception e) {
			logger.error("receive from client error.", e);
		} finally {
			ctx.channel().close();
			System.out.println("关闭连接");
			logger.info("close the connection");
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
