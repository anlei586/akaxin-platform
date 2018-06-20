package com.akaxin.platform.connector.ssl;

import java.io.InputStream;

public class SslKeyStore {

	private SslKeyStore() {
		// keytool -genkey -alias akaxinplatform -keysize 2048 -validity 365 -keyalg RSA
		// -dname "CN=akaxin.com" -keypass akaxin1529378162317 -storepass
		// akaxin1529378162317 -keystore akaxinplatform.jks
	}

	// public static KeyStore getKeyStore() {
	// KeyStore ks = null;
	// try {
	// ks = KeyStore.getInstance("JKS");
	// ks.load(new ByteArrayInputStream(JKS_CERT_BYTES), getKeyStorePassword());
	// } catch (Exception ex) {
	// throw new RuntimeException("Failed to load SSL key store.", ex);
	// }
	// return ks;
	// }
	//
	//
	// public static InputStream asInputStream() {
	// return new ByteArrayInputStream(JKS_CERT_BYTES);
	// }

	/**
	 * 直接从文件中读取
	 * 
	 * @return
	 */
	public static InputStream keyStoreAsInputStream() {
		return SslKeyStore.class.getResourceAsStream("/akaxinplatform.jks");
	}

	public static char[] getCertificatePassword() {
		return "akaxin1529378162317".toCharArray();
	}

	public static char[] getKeyStorePassword() {
		// return "mu$chatchrome123".toCharArray();
		return "akaxin1529378162317".toCharArray();
	}

	public static InputStream trustStoreAsInputStream() {
		return SslKeyStore.class.getResourceAsStream("/akaxinclient.jks");
	}

	public static char[] getTrustKeyStorePassword() {
		return "akaxin1529382054869".toCharArray();
	}
}
