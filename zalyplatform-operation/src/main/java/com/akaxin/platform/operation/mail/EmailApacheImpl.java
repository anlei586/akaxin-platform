package com.akaxin.platform.operation.mail;

import java.io.File;
import java.util.Date;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.constant.CharsetCoding;
import com.akaxin.platform.operation.bean.MailBean;

/**
 * 通过Apache commons-mail 实现邮件发送
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-14 15:41:52
 */
class EmailApacheImpl {
	private static final Logger logger = LoggerFactory.getLogger(EmailApacheImpl.class);

	private String host;
	private int port;

	public EmailApacheImpl(String hostName, int port) {
		this.host = hostName;
		this.port = port;
	}

	// 发送文本邮件
	public String sendTextMail(MailBean bean) {
		String mailId = null;
		try {
			SimpleEmail mail = new SimpleEmail();
			mail.setHostName(this.host);
			mail.setSmtpPort(this.port);
			mail.setAuthenticator(new DefaultAuthenticator(bean.getFromId(), bean.getPasswd()));
			mail.setSSLOnConnect(true);

			mail.setFrom(bean.getFromId());
			mail.addTo(bean.getToId());

			mail.setSubject(bean.getText());
			mail.setMsg(bean.getText());

			mail.setSentDate(new Date());
			mail.setSocketTimeout(20 * 1000);
			mail.setCharset(CharsetCoding.UTF_8);

			mailId = mail.send();
		} catch (EmailException e) {
			logger.error("send text mail error", e);
		}

		return mailId;
	}

	// 发送Html邮件
	public String sendHtmlMail(MailBean bean) {
		String mailId = null;
		try {
			HtmlEmail mail = new HtmlEmail();
			mail.setHostName(this.host);
			mail.setSmtpPort(this.port);
			mail.setAuthentication(bean.getFromId(), bean.getPasswd());
			mail.setSSLOnConnect(true);

			mail.setFrom(bean.getFromId());
			mail.addTo(bean.getToId());

			mail.setSubject(bean.getTitle());
			mail.setHtmlMsg(bean.getHtmlText());

			mail.setSentDate(new Date());
			mail.setSocketTimeout(20 * 1000);
			mail.setCharset(CharsetCoding.UTF_8);

			mailId = mail.send();
		} catch (EmailException e) {
			logger.error("send html code mail error", e);
		}
		return mailId;
	}

	// 发送附带图片的邮件
	public String sendImageMail(MailBean bean) {
		String mailId = null;
		try {
			HtmlEmail mail = new HtmlEmail();
			mail.setSmtpPort(port);
			mail.setHostName(host);
			mail.setAuthentication(bean.getFromId(), bean.getPasswd());
			mail.setFrom(bean.getFromId());
			mail.addTo(bean.getToId());
			mail.setSubject(bean.getTitle());
			mail.embed(new File(""), "cid1");
			// String htmlText = "<html><body><img src='cid:cid1'/></html>";
			mail.setHtmlMsg(bean.getHtmlText());
			mail.setSentDate(new Date());
			mail.setSocketTimeout(20 * 1000);
			mail.setCharset(CharsetCoding.UTF_8);
			mailId = mail.send();
		} catch (EmailException e) {
			logger.error("send image mail error", e);
		}

		return mailId;
	}

}
