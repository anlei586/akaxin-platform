package com.akaxin.platform.push.netty.api;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.akaxin.platform.push.netty.bean.ConnStateBean;
import com.akaxin.platform.push.notification.IApnsPushNotification;

public interface INettyConnectionPool<T extends IApnsPushNotification> extends Closeable {

	public Entry<String, INettyHttp2Connection<T>> acquire();

	public void release(Entry<String, INettyHttp2Connection<T>> clientEntry);

	public int getAvailableConnSize();

	public int getClientQueueSize();

	public List<ConnStateBean> showClientDetails();

	public Map<String, Integer> getClientState();

	public int getResponseMapSize();

}
