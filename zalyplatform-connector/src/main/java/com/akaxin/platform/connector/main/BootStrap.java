package com.akaxin.platform.connector.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.common.monitor.JstatMonitor;
import com.akaxin.platform.common.monitor.ZalyMonitorController;
import com.akaxin.platform.connector.netty.NettyServer;
import com.akaxin.platform.operation.monitor.PushMonitor;
import com.akaxin.platform.operation.monitor.SMSMonitor;
import com.akaxin.platform.operation.push.apns.APNsPushManager;
import com.akaxin.platform.operation.utils.PushStatistics;

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

		initZalyMonitor();
		initApnsPush();
		startNettyServer(port);

		PushStatistics.start();
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
		new NettyServer() {
		}.start(ServerAddress.getLocalAddress(), port);
	}

}
