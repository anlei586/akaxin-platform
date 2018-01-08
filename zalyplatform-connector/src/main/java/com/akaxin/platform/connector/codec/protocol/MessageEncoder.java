package com.akaxin.platform.connector.codec.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.RedisCommand;
import com.akaxin.common.utils.LogUtils;
import com.akaxin.platform.connector.codec.parser.IProtocolBuilder;
import com.akaxin.platform.connector.codec.parser.ProtocolBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 编码器
 * 
 * @author Sam
 * @since 2017.09.27
 * 
 */
public class MessageEncoder extends MessageToByteEncoder<RedisCommand> {
	private static final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		promise.addListener(new GenericFutureListener<Future<? super Void>>() {

			public void operationComplete(Future<? super Void> future) throws Exception {
				if (future.isSuccess()) {
					logger.info("write to client success");
				} else {
					logger.warn("write to client success");
				}

			}
		});

		super.write(ctx, msg, promise);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, RedisCommand msg, ByteBuf out) throws Exception {
		String version = msg.getParameterByIndex(0);
		String action = msg.getParameterByIndex(1);
		byte[] params = msg.getBytesParamByIndex(2);

		LogUtils.printNetLog(logger, "2C", version, action, "", "", params.length, params);

		IProtocolBuilder builder = ProtocolBuilder.getInstance();
		builder.writeAndOut(ctx.channel(), msg, out);
	}

}
