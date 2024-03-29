package com.akaxin.platform.connector.handler;

import java.net.InetSocketAddress;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.command.RedisCommand;
import com.akaxin.platform.common.constant.CommandConst;
import com.akaxin.platform.common.constant.ErrorCode;
import com.akaxin.platform.common.constant.RequestAction;
import com.akaxin.platform.common.logs.Log2Utils;
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
	private static int API = 0;
	private static int IM = 1;

	/**
	 * 用户建立连接到服务端,会激活channel
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(ParserConst.CHANNELSESSION).set(new ChannelSession(ctx.channel()));
	}

	/**
	 * 关闭channel
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ChannelSession channelSession = ctx.channel().attr(ParserConst.CHANNELSESSION).get();
		if (IM == channelSession.getCtype()) {
			ChannelManager.delChannelSession(channelSession.getDeviceId());
		}
		logger.debug("close netty channel connection...client={}", ctx.channel().toString());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RedisCommand redisCommand) throws Exception {
		try {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			String clientIP = insocket.getAddress().getHostAddress();
			ChannelSession channelSession = ctx.channel().attr(ParserConst.CHANNELSESSION).get();

			// 网络协议的版本，第一版本为1.0
			String version = redisCommand.getParameterByIndex(0);
			String action = redisCommand.getParameterByIndex(1);
			byte[] params = redisCommand.getBytesParamByIndex(2);

			CoreProto.TransportPackageData packageData = CoreProto.TransportPackageData.parseFrom(params);
			Map<Integer, String> header = packageData.getHeaderMap();
			Command command = new Command();
			command.setClientIp(clientIP);
			command.setGlobalUserId(channelSession.getUserId());
			command.setDeviceId(channelSession.getDeviceId());
			command.setAction(action);
			command.setChannelSession(channelSession);
			command.setParams(packageData.getData().toByteArray());
			command.setHeader(header);
			if (header != null) {
				command.setClientVersion(header.get(CoreProto.HeaderKey.CLIENT_SOCKET_VERSION_VALUE));
			}
			command.setStartTime(System.currentTimeMillis());

			if (!"ping".equals(command.getMethod())) {
				logger.debug("client id:{} request command:{}", clientIP, command.toString());
			}

			if (RequestAction.IM.getName().equals(command.getRety())) {
				CommandResponse response = new MesageService().doImRequest(command);
				// IM 连接
				if (ErrorCode.ERROR_SESSION.equals(response.getErrCode())) {
					ctx.close();
				}
				Log2Utils.requestResultLog(logger, command, response);
			} else if (RequestAction.API.getName().equals(command.getRety())) {
				CommandResponse response = new MesageService().doApiRequest(command);
				if (response == null) {
					response = customResponse(ErrorCode.ERROR);
				}

				// 兼容"error.alter" AND "error.alert"
				String errCode = response.getErrCode();
				int pbVersion = command.getProtoVersion();

				if (pbVersion < 5 && "error.alert".equals(errCode)) {
					response.setErrCode("error.alter");
				}

				// send and close socket
				ChannelWriter.writeAndClose(ctx.channel(), response);
				Log2Utils.requestResultLog(logger, command, response);
			} else {
				logger.error("unknow request command = {}", command.toString());
			}

		} catch (Exception e) {
			logger.error("receive data from client error.", e);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("netty server: exception caught.", cause);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// logger.info("netty server: user event triggered.");
	}

	// defined by user
	protected CommandResponse customResponse(ErrorCode errCode) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		commandResponse.setErrCode(errCode);
		return commandResponse;
	}

}
