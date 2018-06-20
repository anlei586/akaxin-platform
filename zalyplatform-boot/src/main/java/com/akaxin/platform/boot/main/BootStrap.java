package com.akaxin.platform.boot.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.common.monitor.JstatMonitor;
import com.akaxin.platform.common.monitor.ZalyMonitorController;
import com.akaxin.platform.connector.constant.PlatformServer;
import com.akaxin.platform.connector.exceptions.TcpServerException;
import com.akaxin.platform.connector.netty.PlatformNettySSLServer;
import com.akaxin.platform.connector.netty.PlatformNettyServer;
import com.akaxin.platform.operation.monitor.PushMonitor;
import com.akaxin.platform.operation.monitor.SMSMonitor;
import com.akaxin.platform.operation.push.apns.APNsPushManager;
import com.akaxin.platform.operation.statistics.UserVisitSiteCount;

/**
 * 
 * 启动akaxin platform对外服务
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

		if (args != null) {
			if (args.length == 1) {
				port = Integer.valueOf(args[0]);
			} else if (args.length == 2) {
				port = Integer.valueOf(args[0]);
				sslPort = Integer.valueOf(args[1]);
			}
		}

		logger.info("akaxin platform is starting...");
		try {
			System.setProperty(PlatformServer.AKAXIN_SERVER_NAME, PlatformServer.AKAXIN_PLATFORM);

			initZalyMonitor();
			initApnsPush();
			startNettyServer(port);
			startNettySSLServer(sslPort);

			UserVisitSiteCount.start();
		} catch (TcpServerException e) {
			logger.error("start netty server error", e);
			System.exit(-100);
		}
	}

	private static void initZalyMonitor() {
		ZalyMonitorController zmc = new ZalyMonitorController();
		zmc.addMonitor(new JstatMonitor());
		zmc.addMonitor(new SMSMonitor());
		zmc.addMonitor(new PushMonitor());
		zmc.start();

		logger.info("platform init zaly monitor");
	}

	private static void initApnsPush() {
		APNsPushManager.getInstance().start();
		logger.info("platform start apns manager finish");
	}

	private static void startNettyServer(int port) throws TcpServerException {
		new PlatformNettyServer() {
		}.start(ServerAddress.getLocalAddress(), port);

		logger.info("start platform netty server port={}", port);
	}

	private static void startNettySSLServer(int port) throws TcpServerException {
		new PlatformNettySSLServer() {
		}.start(ServerAddress.getLocalAddress(), port);

		logger.info("start platform netty ssl server port={}", port);
	}

}
