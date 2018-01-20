package com.akaxin.platform.connector.codec.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.RedisCommand;
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
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.27
 */
public class MessageEncoder extends MessageToByteEncoder<RedisCommand> {
	private static final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		promise.addListener(new GenericFutureListener<Future<? super Void>>() {

			public void operationComplete(Future<? super Void> future) throws Exception {
				if (!future.isSuccess()) {
					logger.error("write to client failure", future.cause());
				}
			}
		});
		super.write(ctx, msg, promise);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, RedisCommand msg, ByteBuf out) throws Exception {
		IProtocolBuilder builder = ProtocolBuilder.getInstance();
		builder.writeAndOut(ctx.channel(), msg, out);
	}

}
