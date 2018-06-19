package com.akaxin.platform.connector.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.common.monitor.JstatMonitor;
import com.akaxin.platform.common.monitor.ZalyMonitorController;
import com.akaxin.platform.connector.netty.PlatformNettySSLServer;
import com.akaxin.platform.connector.netty.PlatformNettyServer;
import com.akaxin.platform.operation.monitor.PushMonitor;
import com.akaxin.platform.operation.monitor.SMSMonitor;
import com.akaxin.platform.operation.push.apns.APNsPushManager;
import com.akaxin.platform.operation.statistics.UserVisitSiteCount;

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
		int sslPort = 8443; // ssl端口
		if (args != null && args.length == 1) {
			port = Integer.valueOf(args[0]);
		}
		logger.info("Start Platform Server port:{}", port);

		initZalyMonitor();
		initApnsPush();
		startNettyServer(port);
		startNettySSLServer(sslPort);

		UserVisitSiteCount.start();
	}

	private static void initZalyMonitor() {
		ZalyMonitorController zmc = new ZalyMonitorController();
		zmc.addMonitor(new JstatMonitor());
		zmc.addMonitor(new SMSMonitor());
		zmc.addMonitor(new PushMonitor());
		zmc.start();
	}

	private static void initApnsPush() {
		APNsPushManager.getInstance().start();
	}

	private static void startNettyServer(int port) {
		new PlatformNettyServer() {
		}.start(ServerAddress.getLocalAddress(), port);
	}

	private static void startNettySSLServer(int port) {
		new PlatformNettySSLServer() {
		}.start(ServerAddress.getLocalAddress(), port);
	}

}
