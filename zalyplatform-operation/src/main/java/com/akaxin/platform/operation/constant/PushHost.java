package com.akaxin.platform.operation.constant;

import java.util.HashSet;
import java.util.Set;

import com.akaxin.platform.common.utils.ServerAddress;

/**
 * 配置是否允许push展示原文的host地址
 */
public class PushHost {

	private static Set<String> hostSet = new HashSet<>();

	static {
		hostSet.add("im.akaxin.com");
	}

	public static boolean isAuthedAddress(ServerAddress address) {
		if (address == null)
			return false;
		return isAuthedHost(address.getHost());
	}

	public static boolean isAuthedHost(String host) {
		if (hostSet.contains(host)) {
			return true;
		}
		return false;
	}
	
	public static void addAddress(String host) {
		if (host == null)
			return;
		hostSet.add(host);
	}

	public static void removeHost(String host) {
		if (host == null)
			return;
		hostSet.add(host);
	}
}
