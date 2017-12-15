package com.zaly.platform.connector.handler;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.zaly.common.channel.ChannelSession;
import com.zaly.common.command.Command;
import com.zaly.common.command.CommandResponse;
import com.zaly.common.command.RedisCommand;
import com.zaly.common.executor.AbstracteExecutor;
import com.zaly.platform.business.service.MesageService;
import com.zaly.platform.connector.codec.parser.ParserConst;
import com.zaly.proto.core.CoreProto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyInboundHandler extends SimpleChannelInboundHandler<RedisCommand> {
	private AbstracteExecutor<Command> executor;

	public NettyInboundHandler(AbstracteExecutor<Command> executor) {

		this.executor = executor;

		System.out.println("NettyInboundHandler.executor=" + this.executor);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		/**
		 * 用户建立连接到服务端，执行此方法。
		 */

		ctx.channel().attr(ParserConst.CHANNELSESSION).set(new ChannelSession(ctx.channel()));

		System.out.println("================NettyInboundHandler.channelActive");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("================NettyInboundHandler.channelInactive");
		// ChannelSession channelSession =
		// ctx.channel().attr(ParserConst.CHANNELSESSION).get();
		// ChannelManager.getInstance().delChannel(channelSession.getUserId());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RedisCommand redisCommand) throws Exception {

		try {
			System.out.println("-------Receive data from client-------");

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

			command.setParams(packageData.getData().toByteArray());
			CommandResponse commandResponse = new MesageService().executor(this.executor, command);

			// response
			CoreProto.ErrorInfo errinfo = CoreProto.ErrorInfo.newBuilder()
					.setCode(String.valueOf(commandResponse.getErrCode())).setInfo(String.valueOf(commandResponse.getErrInfo()))
					.build();

			CoreProto.TransportPackageData.Builder packageBuilder = CoreProto.TransportPackageData.newBuilder();

			packageBuilder.setErr(errinfo).putAllHeader(new HashMap<Integer, String>());

			if (commandResponse.getParams() != null) {
				packageBuilder.setData(ByteString.copyFrom(commandResponse.getParams())).build();
				System.out.println("commandResponse Size=" + commandResponse.getParams().length);
			}

			CoreProto.TransportPackageData resPackageData = packageBuilder.build();

			ctx.channel().writeAndFlush(
					new RedisCommand().add(1).add(commandResponse.getAction()).add(resPackageData.toByteArray()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ctx.channel().close();
			System.out.println("关闭连接");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

	}

}
