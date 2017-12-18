package com.akaxin.platform.connector.netty;

import java.util.concurrent.TimeUnit;

import com.akaxin.common.command.Command;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.platform.connector.codec.protocol.MessageDecoder;
import com.akaxin.platform.connector.codec.protocol.MessageEncoder;
import com.akaxin.platform.connector.handler.NettyInboundHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public abstract class NettyServer {

	private AbstracteExecutor<Command> executor;
	private ServerBootstrap bootstrap;
	private EventLoopGroup parentGroup;
	private EventLoopGroup childGroup;

	public NettyServer() {
		parentGroup = new NioEventLoopGroup(10, new PrefixThreadFactory("zaly-boss-eventloopgroup"));
		int childThreadNum = Runtime.getRuntime().availableProcessors() + 1;
		childGroup = new NioEventLoopGroup(childThreadNum, new PrefixThreadFactory("zaly-worker-eventloopgroup"));
		bootstrap = new ServerBootstrap();
		bootstrap.group(parentGroup, childGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 2000);
		bootstrap.option(ChannelOption.SO_REUSEADDR, true);
		bootstrap.option(ChannelOption.SO_RCVBUF, 256 * 1024);// 256byte 字节
		bootstrap.option(ChannelOption.SO_SNDBUF, 256 * 1024);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT); // 动态缓冲区
		bootstrap.handler(new LoggingHandler(LogLevel.INFO));
		bootstrap.childHandler(new BimChannelInitializer());

		executor = loadExecutor();
	}

	public void start(String address, int port) {
		try {
			if (bootstrap != null) {
				ChannelFuture channelFuture = bootstrap.bind(address, port).sync();
				channelFuture.channel().closeFuture().sync();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				parentGroup.shutdownGracefully();
				childGroup.shutdownGracefully();
				parentGroup.terminationFuture().sync();
				childGroup.terminationFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class BimChannelInitializer extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			// SSLEngine sslEngine =
			// NettySocketSslContext.getInstance().getServerContext().createSSLEngine();

			ch.pipeline().addLast(new MessageDecoder());
			ch.pipeline().addLast(new MessageEncoder());

			// ch.pipeline().addLast(new SslHandler(sslEngine));

			ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50, TimeUnit.SECONDS));
			ch.pipeline().addLast(new NettyInboundHandler(executor));
		}

	}

	public abstract AbstracteExecutor<Command> loadExecutor();
}
