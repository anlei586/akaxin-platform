package com.akaxin.platform.common.test.test;

import com.akaxin.platform.common.monitor.ZalyMonitorController;

public class MonitorTest {
	public static void main(String[] args) {
		ZalyMonitorController mm = new ZalyMonitorController();
		mm.addMonitor(new PushMonitor()).start();
	}
}
