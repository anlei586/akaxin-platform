package com.zaly.platform.connector.ssl;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import io.netty.util.internal.SystemPropertyUtil;

public class NettySocketSslContext {
	private static final String PROTOCOL = "TLS";

	private SSLContext serverContext;

	private NettySocketSslContext() {
		String algorithm = SystemPropertyUtil.get("ssl.KeyManagerFactory.algorithm");
		if (algorithm == null) {
			algorithm = "SunX509";
		}

		try {
			//
			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(SslKeyStore.asInputStream(), SslKeyStore.getKeyStorePassword());

			// Set up key manager factory to use our key store
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
			kmf.init(keystore, SslKeyStore.getCertificatePassword());

			// Initialize the SSLContext to work with our key managers.
			serverContext = SSLContext.getInstance(PROTOCOL);
			serverContext.init(kmf.getKeyManagers(), null, null);
		} catch (Exception e) {
			throw new Error("Failed to initialize the server-side SSLContext", e);
		}

	}

	public static NettySocketSslContext getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {
		static NettySocketSslContext instance = new NettySocketSslContext();
	}

	public SSLContext getServerContext() {
		return serverContext;
	}

}
