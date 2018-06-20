package com.akaxin.platform.connector.ssl;

import java.io.InputStream;

public class SslKeyStore {

	private SslKeyStore() {
	}

	public static InputStream platformKeyStoreAsInputStream() {
		return SslKeyStore.class.getResourceAsStream("/platform.akaxin.com.jks");
	}

	public static char[] getPlatformKeyStorePassword() {
		return "4fr5gos674z".toCharArray();
	}

	public static InputStream pushKeyStoreAsInputStream() {
		return SslKeyStore.class.getResourceAsStream("/push.akaxin.com.jks");
	}

	public static char[] getPushKeyStorePassword() {
		return "f7b3468h54unv5d".toCharArray();
	}

}
