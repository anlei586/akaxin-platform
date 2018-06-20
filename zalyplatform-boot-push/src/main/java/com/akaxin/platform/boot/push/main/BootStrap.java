package com.akaxin.platform.boot.push.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.common.monitor.JstatMonitor;
import com.akaxin.platform.common.monitor.ZalyMonitorController;
import com.akaxin.platform.connector.constant.PlatformServer;
import com.akaxin.platform.connector.exceptions.TcpServerException;
import com.akaxin.platform.connector.netty.PlatformNettySSLServer;
import com.akaxin.platform.operation.monitor.PushMonitor;
import com.akaxin.platform.operation.monitor.SMSMonitor;
import com.akaxin.platform.operation.push.apns.APNsPushManager;
import com.akaxin.platform.operation.statistics.UserVisitSiteCount;

/**
 * 
 * 启动akaxin push对外服务
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.09.28
 *
 */
public class BootStrap {
	private static final Logger logger = LoggerFactory.getLogger(BootStrap.class);

	public static void main(String[] args) {
		int sslPort = 443; // ssl端口

		logger.info("akaxin platform push service starting...");
		try {
			System.setProperty(PlatformServer.AKAXIN_SERVER_NAME, PlatformServer.AKAXIN_PUSH);
			initZalyMonitor();
			initApnsPush();
			startPushSSLServer(sslPort);

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

	private static void startPushSSLServer(int port) throws TcpServerException {
		new PlatformNettySSLServer() {
		}.start(ServerAddress.getLocalAddress(), port);

		logger.info("start platform netty ssl server port={}", port);
	}

}
