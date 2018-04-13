package com.akaxin.platform.operation.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.akaxin.platform.common.logs.Log2Creater;
import com.akaxin.platform.common.monitor.ZalyCounter;
import com.akaxin.platform.common.monitor.ZalyMonitor;

/**
 * 短信发送情况监控
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-13 16:13:21
 */
public class SMSMonitor extends ZalyMonitor {

	private static Map<String, ZalyCounter> counterMap = new HashMap<String, ZalyCounter>();

	public static ZalyCounter COUNTER_TOTAL = new ZalyCounter();
	public static ZalyCounter COUNTER_SUCCESS = new ZalyCounter();
	public static ZalyCounter COUNTER_FILTER = new ZalyCounter();
	public static ZalyCounter COUNTER_ERROR = new ZalyCounter();

	static {
		counterMap.put("Total", COUNTER_TOTAL);
		counterMap.put("Success", COUNTER_SUCCESS);
		counterMap.put("Filter", COUNTER_FILTER);
		counterMap.put("Error", COUNTER_ERROR);
	}

	@Override
	public List<String> buidHeader() {
		List<String> headers = new ArrayList<String>();
		headers.add("Total");
		headers.add("Success");
		headers.add("Filter");
		headers.add("Error");
		return headers;
	}

	@Override
	public void buildBody(Map<String, String> bodyMap) {
		for (String headName : getHeader()) {
			bodyMap.put(headName, counterMap.get(headName).getCountString());
		}
	}

	@Override
	public long getIntervalTime() {
		return 1000;// 1s输出一次
	}

	@Override
	public Logger getMonitorLogger() {
		return Log2Creater.createLogger("sms-monitor");
	}

	@Override
	public void clear() {
		// 这里不需要清除，每天12点清理一次
	}

}
