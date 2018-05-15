package com.akaxin.platform.operation.mail;

import org.apache.commons.lang3.StringUtils;

import com.akaxin.platform.operation.bean.MailBean;

/**
 * 发送邮件服务，分装具体发送的部分，对调用方更友好
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-14 16:59:24
 */
public class EmailServiceImpl implements EmailService {

	@Override
	public String sendMail(MailBean bean) {
		String result = null;
		MailEnum me = MailEnum.AKAXIN;
		if (bean != null && StringUtils.isNotEmpty(bean.getFromId())) {
			if (bean.getFromId().endsWith("@akaxin.xyz")) {
				me = MailEnum.TECENT_AKAXIN;
			}

			EmailApacheImpl emailImp = new EmailApacheImpl(me.getHostName(), me.getPort());

			if (StringUtils.isNotEmpty(bean.getText())) {
				return emailImp.sendTextMail(bean);
			} else if (StringUtils.isNotEmpty(bean.getHtmlText())) {
				return emailImp.sendHtmlMail(bean);
			}
		}

		return result;
	}

}
