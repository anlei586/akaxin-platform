package com.akaxin.platform.common.monitor;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class ZalyMonitor {

	public static final long INTERVAL_TIME = 1 * 1000;

	private long intervalCount = 0;// 间隔输出次数

	private List<String> headers;

	abstract public List<String> buidHeader();

	abstract public void buildBody(Map<String, String> monitor);

	abstract public long getIntervalTime();

	abstract public Logger getMonitorLogger();

	abstract public void clear();

	public List<String> getHeader() {
		if (headers != null && headers.size() > 0) {
			return headers;
		}
		headers = buidHeader();
		return headers;
	}

	public void output(List<ZalyMonitor> monitors, Map<String, String> monitorData) {
		Logger logger = getMonitorLogger();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// header
		StringBuffer sb = new StringBuffer();
		for (ZalyMonitor iMonitor : monitors) {
			List<String> monitorHeaders = iMonitor.getHeader();
			if (monitorHeaders != null) {
				for (String title : iMonitor.getHeader()) {
					sb.append(title).append("\t");
				}
			}
		}
		sb.append("time");
		if (intervalCount % 15 == 0) {
			String host = "localhost";
			logger.info(host + ":" + sdf.format(System.currentTimeMillis()));
			logger.info(sb.toString());
			intervalCount = 0;
		}

		// body
		StringBuffer stat = new StringBuffer();
		for (ZalyMonitor monitor : monitors) {
			for (String title : monitor.getHeader()) {
				String body = monitorData.get(title);
				int tabCount = (title.length() >>> 3) + 1;
				stat.append(body);
				for (int i = 0; i < tabCount; i++) {
					stat.append("\t");
				}
			}
		}

		stat.append(sdf.format(System.currentTimeMillis()));
		logger.info(stat.toString());
		intervalCount++;
	}

	protected ZalyCounter getCounter() {
		return new ZalyCounter();
	}

	protected ZalyCounter getCounter(int defaultValue) {
		return new ZalyCounter(defaultValue);
	}

}
