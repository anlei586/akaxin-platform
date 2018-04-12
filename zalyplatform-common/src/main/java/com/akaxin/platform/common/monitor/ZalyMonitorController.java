package com.akaxin.platform.common.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.akaxin.platform.common.threads.ThreadHelper;
import com.akaxin.platform.common.utils.StringHelper;

public class ZalyMonitorController {
	private static Logger logger = Logger.getLogger(ZalyMonitorController.class);

	private List<ZalyMonitor> monitors = new ArrayList<ZalyMonitor>();
	private Map<String, String> monitorData = new ConcurrentHashMap<String, String>();

	public ZalyMonitorController() {
	}

	public ZalyMonitorController addMonitor(ZalyMonitor mon) {
		monitors.add(mon);
		return this;
	}

	public void start() {
		// 定时构建内容
		for (final ZalyMonitor monitor : monitors) {
			ThreadHelper.execute(new Runnable() {
				public void run() {
					while (true) {
						try {
							monitor.buildBody(monitorData);
							monitor.output(monitors, monitorData);
							monitor.clear();
							long interval = Math.max(monitor.getIntervalTime(), ZalyMonitor.INTERVAL_TIME);
							ThreadHelper.sleep(interval);
						} catch (Exception e) {
							logger.error(StringHelper.format("Monitor Item error! monitor: {}",
									monitor.getClass().getName()));
						}
					}
				}
			});
		}

	}

}
