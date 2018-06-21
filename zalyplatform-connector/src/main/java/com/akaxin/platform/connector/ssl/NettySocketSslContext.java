package com.akaxin.platform.connector.ssl;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.platform.connector.constant.PlatformServer;

import io.netty.util.internal.SystemPropertyUtil;

/**
 * platform support ssl with get sslContext
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-21 11:15:43
 */
public class NettySocketSslContext {
	private static final Logger logger = LoggerFactory.getLogger(NettySocketSslContext.class);

	private static SSLContext sslContext;

	private NettySocketSslContext() {

	}

	public static SSLContext getSSLContext() throws Exception {
		String serverName = SystemPropertyUtil.get(PlatformServer.AKAXIN_SERVER_NAME);
		logger.info("service use sslContext for : {}", serverName);
		if (PlatformServer.AKAXIN_PLATFORM.equals(serverName)) {
			return getPlatformSSLContext();
		} else if (PlatformServer.AKAXIN_PUSH.equals(serverName)) {
			return getPushSSLContext();
		} else {
			throw new Exception("error platform server name to get ssl context");
		}

	}

	private static SSLContext getPlatformSSLContext() {
		String algorithm = SystemPropertyUtil.get("ssl.KeyManagerFactory.algorithm");
		if (algorithm == null) {
			algorithm = "SunX509";
		}

		try {
			if (sslContext == null) {
				//
				KeyStore keystore = KeyStore.getInstance("JKS");
				// need storepass
				keystore.load(SslKeyStore.platformKeyStoreAsInputStream(), SslKeyStore.getPlatformKeyStorePassword());

				// init kmf
				KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
				// need keypass
				kmf.init(keystore, SslKeyStore.getPlatformKeyStorePassword());

				// KeyStore trustKeystore = KeyStore.getInstance("JKS");
				// // need storepass
				// trustKeystore.load(SslKeyStore.keyStoreAsInputStream(),
				// SslKeyStore.getKeyStorePassword());
				// // TrustManagerFactory.getDefaultAlgorithm()
				// TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				// tmf.init(trustKeystore);

				// Initialize the SSLContext with kmf
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(kmf.getKeyManagers(), null, null);
			}
		} catch (Exception e) {
			throw new Error("Failed to initialize akaxin-platform server-side SSLContext", e);
		}

		return sslContext;
	}

	private static SSLContext getPushSSLContext() {
		String algorithm = SystemPropertyUtil.get("ssl.KeyManagerFactory.algorithm");
		if (algorithm == null) {
			algorithm = "SunX509";
		}

		try {
			if (sslContext == null) {
				//
				KeyStore keystore = KeyStore.getInstance("JKS");
				// need storepass
				keystore.load(SslKeyStore.pushKeyStoreAsInputStream(), SslKeyStore.getPushKeyStorePassword());

				// init kmf
				KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
				// need keypass
				kmf.init(keystore, SslKeyStore.getPushKeyStorePassword());

				// KeyStore trustKeystore = KeyStore.getInstance("JKS");
				// // need storepass
				// trustKeystore.load(SslKeyStore.keyStoreAsInputStream(),
				// SslKeyStore.getKeyStorePassword());
				// // TrustManagerFactory.getDefaultAlgorithm()
				// TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				// tmf.init(trustKeystore);

				// Initialize the SSLContext with kmf
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(kmf.getKeyManagers(), null, null);
			}
		} catch (Exception e) {
			throw new Error("Failed to initialize akaxin-platform server-side SSLContext", e);
		}

		return sslContext;
	}

}
