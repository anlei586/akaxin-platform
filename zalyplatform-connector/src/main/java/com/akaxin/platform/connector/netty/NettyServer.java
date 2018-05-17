package com.akaxin.platform.connector.netty;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
	private ServerBootstrap bootstrap;
	private EventLoopGroup parentGroup;
	private EventLoopGroup childGroup;

	public NettyServer() {
		int needThreadNum = Runtime.getRuntime().availableProcessors() + 1;
		int parentNum = 10;
		int childNum = needThreadNum * 5 + 10;
		parentGroup = new NioEventLoopGroup(parentNum, new PrefixThreadFactory("zaly-boss-eventloopgroup"));
		childGroup = new NioEventLoopGroup(childNum, new PrefixThreadFactory("zaly-worker-eventloopgroup"));
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
		bootstrap.handler(new LoggingHandler(LogLevel.DEBUG));
		bootstrap.childHandler(new BimChannelInitializer());
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
			ch.pipeline().addLast(new NettyInboundHandler());
		}

	}

}
