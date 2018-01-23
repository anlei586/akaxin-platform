package com.akaxin.platform.operation.push.apns;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.akaxin.apns.notification.Payload;
import com.akaxin.apns.notification.PayloadBuilder;

/**
 * APNs的消息结构包
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-23 17:24:14
 */
public class ApnsPackage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String token;
	private String title;
	private String body;
	private int badge;
	private String category;
	private String sound;

	private Map<String, Object> alertExtraFields = new HashMap<String, Object>();
	private Map<String, Object> apsExtraFields = new HashMap<String, Object>();
	private Map<String, Object> rootExtraFields = new HashMap<String, Object>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getBadge() {
		return badge;
	}

	public ApnsPackage setBadge(int badge) {
		this.badge = badge;
		return this;
	}

	public String getSound() {
		return sound;
	}

	public ApnsPackage setSound(String sound) {
		this.sound = sound;
		return this;
	}

	public String buildPayloadJson() {

		PayloadBuilder payLoadBuilder = Payload.newPayload();

		if (title != null) {
			payLoadBuilder.addAlertTitle(title);
		}
		if (body != null) {
			payLoadBuilder.addAlertBody(body);
		}
		if (badge > 0) {
			payLoadBuilder.addBadge(badge);
		}
		if (sound != null) {
			payLoadBuilder.addSound(sound);
		}

		if (alertExtraFields != null && alertExtraFields.size() > 0) {
			payLoadBuilder.addCustomAlertFields(alertExtraFields);
		}
		if (apsExtraFields != null && apsExtraFields.size() > 0) {
			payLoadBuilder.addCustomApsField(apsExtraFields);
		}
		if (rootExtraFields != null && rootExtraFields.size() > 0) {
			payLoadBuilder.addRootExtraFields(rootExtraFields);
		}

		if (StringUtils.isNotEmpty(category)) {
			payLoadBuilder.addCustomApsField("category", category);
		}

		return payLoadBuilder.build();
	}

	public Map<String, Object> getRootExtraFields() {
		return rootExtraFields;
	}

	public void setRootExtraFields(Map<String, Object> rootExtraFields) {
		this.rootExtraFields = rootExtraFields;
	}

	public void setRootExtraField(String key, Object value) {
		this.rootExtraFields.put(key, value);
	}

	public Map<String, Object> getAlertExtraFields() {
		return alertExtraFields;
	}

	public void setAlertExtraFields(Map<String, Object> alertExtraFields) {
		this.alertExtraFields = alertExtraFields;
	}

	public void setAlertExtraField(String key, Object value) {
		this.alertExtraFields.put(key, value);
	}

	public Map<String, Object> getApsExtraFields() {
		return apsExtraFields;
	}

	public void setApsExtraFields(Map<String, Object> apsExtraFields) {
		this.apsExtraFields = apsExtraFields;
	}

	public void setApsExtraField(String key, Object value) {
		this.apsExtraFields.put(key, value);
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
