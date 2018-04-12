package com.akaxin.connector.test;

import com.akaxin.platform.common.monitor.ZalyMonitorController;
import com.akaxin.platform.common.test.test.PushMonitor;

public class MonitorTest {
	public static void main(String[] args) {
		ZalyMonitorController mm = new ZalyMonitorController();
		mm.addMonitor(new PushMonitor());
		mm.start();
	}
}
