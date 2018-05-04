package com.akaxin.platform.operation.push.umeng;

import com.akaxin.platform.common.utils.GsonUtils;

public class UmengPackage {
	private String pushToken;
	private String title;
	private String text;
	/// 通知栏提示文字
	private String ticker;
	private String pushGoto;

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getPushGoto() {
		return pushGoto;
	}

	public void setPushGoto(String pushGoto) {
		this.pushGoto = pushGoto;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
