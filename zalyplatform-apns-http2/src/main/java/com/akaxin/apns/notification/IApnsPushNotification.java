package com.akaxin.apns.notification;

import java.util.Date;

import com.akaxin.apns.netty.DeliveryPriority;

/**
 * @author frank@linkedkeeper.com on 2016/12/27.
 */
public interface IApnsPushNotification {
	
	String getToken();

	String getPayload();

	Date getExpiration();

	DeliveryPriority getPriority();

	String getTopic();

	void setTopic(String topic);

}
