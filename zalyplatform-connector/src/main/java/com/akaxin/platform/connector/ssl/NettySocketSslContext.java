package com.akaxin.platform.connector.ssl;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import io.netty.util.internal.SystemPropertyUtil;

public class NettySocketSslContext {

	private static SSLContext serverContext;

	private NettySocketSslContext() {

	}

	public static SSLContext getSSLContext() {
		String algorithm = SystemPropertyUtil.get("ssl.KeyManagerFactory.algorithm");
		if (algorithm == null) {
			algorithm = "SunX509";
		}

		try {
			if (serverContext == null) {
				//
				KeyStore keystore = KeyStore.getInstance("JKS");
				// need storepass
				keystore.load(SslKeyStore.resourcesAsInputStream(), SslKeyStore.getKeyStorePassword());

				// init kmf
				KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
				// need keypass
				kmf.init(keystore, SslKeyStore.getCertificatePassword());

				// Initialize the SSLContext with kmf
				serverContext = SSLContext.getInstance("TLS");
				serverContext.init(kmf.getKeyManagers(), null, null);
			}
		} catch (Exception e) {
			throw new Error("Failed to initialize akaxin-platform server-side SSLContext", e);
		}

		return serverContext;
	}

}
