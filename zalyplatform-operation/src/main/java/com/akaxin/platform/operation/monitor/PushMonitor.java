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
 * PUSH发送情况监控
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-13 16:13:21
 */
public class PushMonitor extends ZalyMonitor {
	private static Map<String, ZalyCounter> counterMap = new HashMap<String, ZalyCounter>();

	public static ZalyCounter COUNTER_TOTAL = new ZalyCounter();
	public static ZalyCounter COUNTER_U2_TEXT = new ZalyCounter();
	public static ZalyCounter COUNTER_U2_TEXTS = new ZalyCounter();
	public static ZalyCounter COUNTER_U2_PIC = new ZalyCounter();
	public static ZalyCounter COUNTER_U2_PICS = new ZalyCounter();
	public static ZalyCounter COUNTER_U2_AUDIO = new ZalyCounter();
	public static ZalyCounter COUNTER_U2_AUDIOS = new ZalyCounter();
	public static ZalyCounter COUNTER_G_TEXT = new ZalyCounter();
	public static ZalyCounter COUNTER_G_PIC = new ZalyCounter();
	public static ZalyCounter COUNTER_G_AUDIO = new ZalyCounter();

	public static ZalyCounter COUNTER_OTHERS = new ZalyCounter();
	public static ZalyCounter COUNTER_ERROR = new ZalyCounter();

	static {
		counterMap.put("Total", COUNTER_TOTAL);
		counterMap.put("U2_TEXT", COUNTER_U2_TEXT);
		counterMap.put("U2_TEXTS", COUNTER_U2_TEXTS);
		counterMap.put("U2_PIC", COUNTER_U2_PIC);
		counterMap.put("U2_PICS", COUNTER_U2_PICS);
		counterMap.put("U2_AUDIO", COUNTER_U2_AUDIOS);
		counterMap.put("U2_AUDIOS", COUNTER_U2_AUDIOS);
		counterMap.put("G_TEXT", COUNTER_G_TEXT);
		counterMap.put("G_PIC", COUNTER_G_PIC);
		counterMap.put("G_AUDIO", COUNTER_G_AUDIO);
		counterMap.put("Other", COUNTER_OTHERS);
		counterMap.put("Error", COUNTER_ERROR);
	}

	@Override
	public List<String> buidHeader() {
		List<String> headers = new ArrayList<String>();
		headers.add("Total");
		headers.add("U2_TEXT");
		headers.add("U2_TEXTS");
		headers.add("U2_PIC");
		headers.add("U2_PICS");
		headers.add("U2_AUDIO");
		headers.add("U2_AUDIOS");
		headers.add("G_TEXT");
		headers.add("G_PIC");
		headers.add("G_AUDIO");
		headers.add("Other");
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
		return Log2Creater.createLogger("push-monitor");
	}

	@Override
	public void clear() {
		// 这里不需要清除，每天12点清理一次
	}

}
