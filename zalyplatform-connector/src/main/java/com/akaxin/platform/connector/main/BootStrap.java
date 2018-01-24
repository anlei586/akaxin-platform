package com.akaxin.platform.connector.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.connector.netty.NettyServer;
import com.akaxin.platform.operation.push.apns.APNsPushManager;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.09.28
 *
 */
public class BootStrap {
	private static final Logger logger = LoggerFactory.getLogger(BootStrap.class);

	public static void main(String[] args) {
		int port = 8000;
		if (args != null && args.length == 1) {
			port = Integer.valueOf(args[0]);
		}
		logger.info("Start Platform Server port:{}", port);

		APNsPushManager.getInstance().start();

		new NettyServer() {
		}.start(ServerAddress.getLocalAddress(), port);

	}

}
