package com.akaxin.platform.common.utils;

import java.net.Inet4Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtils {
	private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

	public static String getLocalAddress() {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (Exception e) {
			logger.error("unknown host exception", e);
		}
		return null;
	}
}
