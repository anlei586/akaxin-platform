package com.akaxin.platform.connector.netty;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.connector.codec.protocol.MessageDecoder;
import com.akaxin.platform.connector.codec.protocol.MessageEncoder;
import com.akaxin.platform.connector.exceptions.TcpServerException;
import com.akaxin.platform.connector.handler.NettyInboundHandler;
import com.akaxin.platform.connector.ssl.NettySocketSslContext;

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
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Netty服务端支持SSL
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-19 10:38:39
 */
public abstract class PlatformNettySSLServer {
	private static Logger logger = LoggerFactory.getLogger(PlatformNettySSLServer.class);
	private ServerBootstrap bootstrap;
	private EventLoopGroup parentGroup;
	private EventLoopGroup childGroup;

	public PlatformNettySSLServer() {
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
		bootstrap.childHandler(new PlatformChannelInitializer());
	}

	private class PlatformChannelInitializer extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			SSLEngine sslEngine = NettySocketSslContext.getSSLContext().createSSLEngine();
			sslEngine.setUseClientMode(false);// 握手时，使用服务端模式
			sslEngine.setNeedClientAuth(false);// 引擎请求客户端验证。true：说明双向认证
			ch.pipeline().addFirst(new SslHandler(sslEngine));

			ch.pipeline().addLast(new MessageDecoder());
			ch.pipeline().addLast(new MessageEncoder());

			ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50, TimeUnit.SECONDS));
			ch.pipeline().addLast(new NettyInboundHandler());
		}

	}

	public void start(String address, int port) throws TcpServerException {
		try {
			ChannelFuture channelFuture = bootstrap.bind(address, port).sync();
			channelFuture.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					closeGracefully();
				}
			});
		} catch (Exception e) {
			closeGracefully();
			throw new TcpServerException("start openzaly tcp-server error", e);
		}
	}

	private void closeGracefully() {
		try {
			if (parentGroup != null) {
				// terminate all threads
				parentGroup.shutdownGracefully();
				// wait for all threads terminated
				parentGroup.terminationFuture().sync();
			}
			if (childGroup != null) {
				// terminate all threads
				childGroup.shutdownGracefully();
				// wait for all threads terminated
				childGroup.terminationFuture().sync();
			}
		} catch (Exception e) {
			logger.error("shutdown netty gracefully error.", e);
		}
	}

}
