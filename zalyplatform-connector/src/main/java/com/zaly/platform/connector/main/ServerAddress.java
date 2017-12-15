package com.zaly.platform.connector.main;

public class ServerAddress {
	public static String getLocalAddress() {

		// try {
		// return Inet4Address.getLocalHost().getHostAddress();
		// } catch (UnknownHostException e) {
		// e.printStackTrace();
		// }

		return "0.0.0.0";
	}

	public static int getPort() {
		return 8448;
	}
}
