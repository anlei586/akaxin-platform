package com.akaxin.platform.operation.mail;

/**
 * akaxin 邮件服务器地址种类枚举
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-14 15:47:38
 */
public enum MailEnum {

	AKAXIN(1, "smtp.akaxin.com", 25), // 阿卡信官方邮件服务器地址
	TECENT_AKAXIN(2, "smtp.exmail.qq.com", 465);// 阿卡信腾讯企业邮箱

	private int index;
	private String hostName;
	private int port;

	MailEnum(int i, String name, int port) {
		this.index = i;
		this.hostName = name;
		this.port = port;
	}

	public String getHostName() {
		return this.hostName;
	}

	public int getPort() {
		return this.port;
	}

}
