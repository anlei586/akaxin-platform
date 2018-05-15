package com.akaxin.platform.operation.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.akaxin.common.utils.StringHelper;
import com.akaxin.platform.common.logs.Log2Creater;
import com.akaxin.platform.operation.bean.MailBean;
import com.akaxin.platform.operation.mail.EmailService;
import com.akaxin.platform.operation.mail.EmailServiceImpl;
import com.akaxin.platform.storage.impl.redis.client.JedisClient;

/**
 * <pre>
 * 		负责统计数据信息入库,redis持久化,过期 30天
 * 			
 *			当天访问站点地址:
 *			count_site_address_20180514	:（zset）
 *			
 *			用户使用量:
 *			count_user_20180514			:（zset）
 *			
 *			Push统计，Hash结构:
 *			key=count_push_20180514
 *				total	:Push总量（string）
 *				u2		:Push单聊（string）
 *				group	:Push群聊（string）
 *
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-14 18:19:02
 */
public class PushStatistics {
	private static final Logger logger = Logger.getLogger(PushStatistics.class);
	private static final Logger userSiteLogger = Log2Creater.createTimeLogger("count-user-site");
	// private static final Logger pushLogger =
	// Log2Creater.createTimeLogger("count-push");

	static {
		// 定时输出一分钟，输出一次结果
		Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					StringBuilder pushHtml = new StringBuilder("<body><section id='content'>"
							+ "<table width='90%' style='border-collapse: collapse; margin: 0 auto;text-align: center;'>  "
							+ "<caption><h2>阿卡信平台数据统计</h2></caption>  "
							+ "<thead><tr style='background-color: #CCE8EB;'> "
							+ "<th>序号</th><th>站点地址</th><th>用户使用量</th><th>Push总量</th><th>Push单聊</th><th>Push群聊</th><th>Push其他</th></tr></thead>");
					JedisClient jedis = new JedisClient();
					String sitekey = getDBKey("site_address_count");
					Set<String> sites = jedis.zrange(sitekey, 0, -1);

					int num = 0;
					long userCountNum = 0;
					long pushTotalNum = 0;
					long pushU2Num = 0;
					long pushGroupNum = 0;
					long pushOthersNum = 0;
					for (String siteAddress : sites) {
						long userCount = 0;
						long pushTotal = 0;
						long pushU2 = 0;
						long pushGroup = 0;
						long pushOthers = 0;
						try {
							String userkey = getDBKey(siteAddress + "_user");
							userCount = jedis.zcard(userkey);

							String pushKey = getDBKey(siteAddress + "_push");
							Map<String, String> pushMap = jedis.hgetAll(pushKey);
							if (pushMap != null) {

								if (StringUtils.isNotEmpty(pushMap.get("u2"))) {
									pushU2 = Integer.valueOf(pushMap.get("u2"));
								}
								if (StringUtils.isNotEmpty(pushMap.get("group"))) {
									pushGroup = Integer.valueOf(pushMap.get("group"));
								}

								if (StringUtils.isNotEmpty(pushMap.get("others"))) {
									pushOthers = Integer.valueOf(pushMap.get("others"));
								}
								pushTotal += (pushU2 + pushGroup + pushOthers);
							}

							userCountNum += userCount;
							pushTotalNum += pushTotal;
							pushU2Num += pushU2;
							pushGroupNum += pushGroup;
							pushOthersNum += pushOthers;
						} catch (Exception e) {
							logger.error("count platform statistics for user error", e);
						}

						String siteHtml = "<tr style='border: 1px solid #cad9ea;color: #666;height: 30px; '>  "
								+ "<td style='border: 1px solid #cad9ea;'>" + ++num + "</td>"
								+ "<td style='border: 1px solid #cad9ea;'>" + siteAddress + "</td>"
								+ "<td style='border: 1px solid #cad9ea;'>" + userCount + "</td>  "
								+ "<td style='border: 1px solid #cad9ea;'>" + pushTotal + "</td>  "
								+ "<td style='border: 1px solid #cad9ea;'>" + pushU2 + "</td>"
								+ "<td style='border: 1px solid #cad9ea;'>" + pushGroup + "</td>"
								+ "<td style='border: 1px solid #cad9ea;'>" + pushOthers + "</td></tr>";
						pushHtml.append(siteHtml);
					}

					String siteHtml = "<tr style='border: 1px solid #cad9ea;color: #666;height: 30px; '>  "
							+ "<td style='border: 1px solid #cad9ea;'>合计</td>"
							+ "<td style='border: 1px solid #cad9ea;'>" + "" + "</td>"
							+ "<td style='border: 1px solid #cad9ea;'>" + userCountNum + "</td>  "
							+ "<td style='border: 1px solid #cad9ea;'>" + pushTotalNum + "</td>  "
							+ "<td style='border: 1px solid #cad9ea;'>" + pushU2Num + "</td>"
							+ "<td style='border: 1px solid #cad9ea;'>" + pushGroupNum + "</td>"
							+ "<td style='border: 1px solid #cad9ea;'>" + pushOthersNum + "</td></tr>";
					pushHtml.append(siteHtml);

					pushHtml.append("</table></section>" + "<footer id='footer'>"
							+ "<div class='text-center padder' align='center'><p><small>北京阿卡信信息技术有限公司&copy;2018</small> </p></div>"
							+ "</footer></body>");

					EmailService emailService = new EmailServiceImpl();
					MailBean bean = new MailBean();
					bean.setFromId("an.guoyue@akaxin.xyz");
					bean.setPasswd("Agy_19950517");
					bean.setToId("an.guoyue@akaxin.xyz");
					bean.setTitle("阿卡信平台数据统计");
					bean.setHtmlText(pushHtml.toString());
					emailService.sendMail(bean);

					MailBean bean2 = new MailBean();
					bean2.setFromId("an.guoyue@akaxin.xyz");
					bean2.setPasswd("Agy_19950517");
					bean2.setToId("zhang.mingqiang@akaxin.xyz");
					bean2.setTitle("阿卡信平台数据统计");
					bean2.setHtmlText(pushHtml.toString());
					emailService.sendMail(bean2);
				} catch (Exception e) {
					logger.error("mail to siteaddress details error", e);
				}
			}

		}, 0, 1, TimeUnit.HOURS);
	}

	public static void addUserVisiteSite(String globalUserId, String siteAddress) {
		userSiteLogger.info(StringHelper.format("globalUserId={} visit siteAddress={}", globalUserId, siteAddress));

		JedisClient jedis = new JedisClient();
		try {
			String sitekey = getDBKey("site_address_count");
			jedis.zadd(sitekey, System.currentTimeMillis(), siteAddress);
		} catch (Exception e) {
			logger.error("add statistics siteaddress to db error", e);
		}

		try {
			String userkey = getDBKey(siteAddress + "_user");
			jedis.zadd(userkey, System.currentTimeMillis(), globalUserId);
		} catch (Exception e) {
			logger.error("add statistics globalUser to db error", e);
		}

	}

	/**
	 * <pre>
	 * hash结构 
	 * 		key  :count_push_20180514 
	 * 		field:u2
	 * </pre>
	 */
	public static void hincrU2Push(String globalUserId, String siteAddress) {
		try {
			String key = getDBKey(siteAddress + "_push");
			JedisClient jedis = new JedisClient();
			jedis.hincrBy(key, "u2", 1);
		} catch (Exception e) {
			logger.error("hincr u2 push error siteAddress=" + siteAddress, e);
		}

		// 回填一次站点统计数据
		addUserVisiteSite(globalUserId, siteAddress);
	}

	/**
	 * <pre>
	 * hash结构 
	 * 		key  :count_push_20180514 
	 * 		field:group
	 * </pre>
	 */
	public static void hincrGroupPush(String globalUserId, String siteAddress) {
		try {
			String key = getDBKey(siteAddress + "_push");
			JedisClient jedis = new JedisClient();
			jedis.hincrBy(key, "group", 1);
		} catch (Exception e) {
			logger.error("hincr group push siteAddress=" + siteAddress, e);
		}

		// 回填一次站点统计数据
		addUserVisiteSite(globalUserId, siteAddress);
	}

	public static void hincrOtherPush(String globalUserId, String siteAddress) {
		try {
			String key = getDBKey(siteAddress + "_push");
			JedisClient jedis = new JedisClient();
			jedis.hincrBy(key, "other", 1);
		} catch (Exception e) {
			logger.error("hincr other push error siteAddress=" + siteAddress, e);
		}

		// 回填一次站点统计数据
		addUserVisiteSite(globalUserId, siteAddress);
	}

	public static String getDBKey(String typeName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dayTime = sdf.format(new Date());
		return typeName + "_" + dayTime;
	}

}
