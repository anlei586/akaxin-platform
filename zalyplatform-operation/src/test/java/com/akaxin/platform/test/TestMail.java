package com.akaxin.platform.test;

import com.akaxin.platform.operation.bean.MailBean;
import com.akaxin.platform.operation.mail.EmailService;
import com.akaxin.platform.operation.mail.EmailServiceImpl;

public class TestMail {

	public static void main(String[] args) {

		EmailService email = new EmailServiceImpl();

		MailBean bean = new MailBean();
		bean.setFromId("an.guoyue@akaxin.xyz");
		bean.setPasswd("Agy_19950517");
		bean.setToId("an.guoyue@akaxin.xyz");
		bean.setTitle("hahahahah");
		bean.setHtmlText("<body><section id='content'>"
				+ "<table width='90%' style='border-collapse: collapse; margin: 0 auto;text-align: center;'>  "
				+ "<caption><h2>阿卡信平台数据统计</h2></caption>  "
				+ "<thead><tr style='background-color: #CCE8EB;'> "
				+ "<th>站点地址</th><th>用户使用量</th><th>Push总量</th><th>Push单聊</th><th>Push群聊</th></tr></thead>"
				+ "<tr style='border: 1px solid #cad9ea;color: #666;height: 30px; '>  "
				+ "<td style='border: 1px solid #cad9ea;'>109</td>"
				+ "<td style='border: 1px solid #cad9ea;'>13 </td>  "
				+ "<td style='border: 1px solid #cad9ea;'>1.34</td>  "
				+ "<td style='border: 1px solid #cad9ea;'>213</td>"
				+ "<td style='border: 1px solid #cad9ea;'>213</td>   "
				+ "</tr></table></section>"
				+ "<footer id='footer'>"
				+ "<div class='text-center padder' align='center'><p><small>北京阿卡信信息技术有限公司&copy;2018</small> </p></div>"
				+ "</footer></body>");

		System.out.println(email.sendMail(bean));
	}
}
