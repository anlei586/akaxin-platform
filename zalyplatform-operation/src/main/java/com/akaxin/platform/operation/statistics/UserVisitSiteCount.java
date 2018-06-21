package com.akaxin.platform.operation.statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
 * 	站点信息统计：
 * 
 * 	统计所有客户端访问的站点，存储类型zset：
 * 		key:	  	site_address_count_{20180521}
 * 		score:	{currentTimeMillis}
 * 		member:	{demo.akaxin.com}:2021
 * 
 * 	统计所有站点上当天的活跃用户，存储类型zset：
 * 		key:		{demo.akaxin.com:2021}_user_{20180521}
 * 		score:	{currentTimeMillis}
 * 		member:	{siteUserId}
 *
 * 	统计所有站点，发送平台的PUSH量，存储结构为hash结构 
 * 		key	:{demo.akaxin.com:2021}_push_{20180514}
 * 		<field,value>
 * 		"u2"	:二人消息的数量
 * 		"group":群消息的数量
 * 		"other":其他类型的消息数量
 *
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-14 18:19:02
 */
public class UserVisitSiteCount {
	private static final Logger logger = Logger.getLogger(UserVisitSiteCount.class);
	private static final Logger userSiteLogger = Log2Creater.createTimeLogger("count-site");

	private static ScheduledExecutorService threadService;

	enum EmailDay {
		YESTERDAY, TODAY
	}

	public static void start() {
		// 定时输出一小时，输出一次结果
		threadService = Executors.newSingleThreadScheduledExecutor();
		threadService.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				// 获取当前时间
				Calendar cal = Calendar.getInstance();
				int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);

				if (hourOfDay == 0) {
					sendEmail(EmailDay.YESTERDAY);
				} else if (hourOfDay >= 6) {
					sendEmail(EmailDay.TODAY);
				} else {
					logger.info("sleep time ,ignore email");
				}

			}

		}, 0, 1, TimeUnit.HOURS);
	}

	private static String getDayKey(EmailDay day, String key) {
		switch (day) {
		case YESTERDAY:
			return getYesterdayDBKey(key);
		case TODAY:
			return getTodayDBKey(key);
		default:
			break;
		}
		// 默认发送今天
		return getTodayDBKey(key);
	}

	// 20180516
	private static String getFormatTime(EmailDay day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		long timeMills = System.currentTimeMillis();

		switch (day) {
		case YESTERDAY:
			timeMills = timeMills - 24 * 60 * 60 * 1000l;
		case TODAY:
			break;
		default:
			break;
		}
		return sdf.format(new Date(timeMills));
	}

	private static void sendEmail(EmailDay day) {
		try {
			String ftime = getFormatTime(day);

			StringBuilder pushHtml = new StringBuilder("<body><section id='content'>"
					+ "<table width='90%' style='border-collapse: collapse; margin: 0 auto;text-align: center;'>  "
					+ "<caption><h2>阿卡信平台数据统计-" + ftime + "</h2></caption>  "
					+ "<thead><tr style='background-color: #CCE8EB;'> "
					+ "<th>序号</th><th>站点地址</th><th>用户使用量</th><th>Push总量</th><th>Push单聊</th><th>Push群聊</th><th>Push其他</th></tr></thead>");
			JedisClient jedis = new JedisClient();
			String sitekey = getDayKey(day, "site_address_count");
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
					String userkey = getDayKey(day, siteAddress + "_user");
					userCount = jedis.zcard(userkey);

					String pushKey = getDayKey(day, siteAddress + "_push");
					Map<String, String> pushMap = jedis.hgetAll(pushKey);
					if (pushMap != null) {

						if (StringUtils.isNotEmpty(pushMap.get("u2"))) {
							pushU2 = Integer.valueOf(pushMap.get("u2"));
						}
						if (StringUtils.isNotEmpty(pushMap.get("group"))) {
							pushGroup = Integer.valueOf(pushMap.get("group"));
						}

						if (StringUtils.isNotEmpty(pushMap.get("other"))) {
							pushOthers = Integer.valueOf(pushMap.get("other"));
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
					+ "<td style='border: 1px solid #cad9ea;'>合计</td>" + "<td style='border: 1px solid #cad9ea;'>" + ""
					+ "</td>" + "<td style='border: 1px solid #cad9ea;'>" + userCountNum + "</td>  "
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
			bean.setTitle("阿卡信平台数据统计-" + ftime);
			bean.setHtmlText(pushHtml.toString());
			emailService.sendMail(bean);

			MailBean bean2 = new MailBean();
			bean2.setFromId("an.guoyue@akaxin.xyz");
			bean2.setPasswd("Agy_19950517");
			bean2.setToId("zhang.mingqiang@akaxin.xyz");
			bean2.setTitle("阿卡信平台数据统计-" + ftime);
			bean2.setHtmlText(pushHtml.toString());
			emailService.sendMail(bean2);

			MailBean bean3 = new MailBean();
			bean3.setFromId("an.guoyue@akaxin.xyz");
			bean3.setPasswd("Agy_19950517");
			bean3.setToId("zhang.jun@akaxin.xyz");
			bean3.setTitle("阿卡信平台数据统计-" + ftime);
			bean3.setHtmlText(pushHtml.toString());
			emailService.sendMail(bean3);
		} catch (Exception e) {
			logger.error("mail to siteaddress details error", e);
		}
	}

	/**
	 * <pre>
	 * 统计所有客户端访问的站点，存储类型zset：
	 * 		key:	  	site_address_count_{20180521}
	 * 		score:	{currentTimeMillis}
	 * 		member:	{demo.akaxin.com}:2021
	 * 
	 * 统计所有站点上当天的活跃用户，存储类型zset：
	 * 		key:		{demo.akaxin.com:2021}_user_{20180521}
	 * 		score:	{currentTimeMillis}
	 * 		member:	{siteUserId}
	 * 
	 * </pre>
	 * 
	 * @param globalUserId
	 * @param siteAddress
	 */
	public static void addVisitUser(String globalUserId, String siteAddress) {
		userSiteLogger.info(StringHelper.format("globalUserId={} visit siteAddress={}", globalUserId, siteAddress));

		JedisClient jedis = new JedisClient();
		try {
			String sitekey = getTodayDBKey("site_address_count");
			jedis.zadd(sitekey, System.currentTimeMillis(), siteAddress);
		} catch (Exception e) {
			logger.error("add statistics siteaddress to db error", e);
		}

		try {
			String userkey = getTodayDBKey(siteAddress + "_user");
			jedis.zadd(userkey, System.currentTimeMillis(), globalUserId);
		} catch (Exception e) {
			logger.error("add statistics globalUser to db error", e);
		}

	}

	/**
	 * <pre>
	 * 统计所有站点，发送平台的PUSH量，存储结构为hash结构 
	 * 		key	:{demo.akaxin.com:2021}_push_{20180514}
	 * 		field:"u2"
	 * </pre>
	 */
	public static void hincrU2Push(String globalUserId, String siteAddress) {
		try {
			String key = getTodayDBKey(siteAddress + "_push");
			JedisClient jedis = new JedisClient();
			jedis.hincrBy(key, "u2", 1);
		} catch (Exception e) {
			logger.error("hincr u2 push error siteAddress=" + siteAddress, e);
		}

		// 回填一次站点统计数据
		// addUserVisitSite(globalUserId, siteAddress);
	}

	/**
	 * <pre>
	 * hash结构 
	 * 		key	:{site_address}_push_20180514
	 * 		key	:im.akaxin.com:2021_push_20180514 
	 * 		field:"group"
	 * </pre>
	 */
	public static void hincrGroupPush(String globalUserId, String siteAddress) {
		try {
			String key = getTodayDBKey(siteAddress + "_push");
			JedisClient jedis = new JedisClient();
			jedis.hincrBy(key, "group", 1);
		} catch (Exception e) {
			logger.error("hincr group push siteAddress=" + siteAddress, e);
		}

		// 回填一次站点统计数据
		// addUserVisitSite(globalUserId, siteAddress);
	}

	/**
	 * <pre>
	 * Hash Key:
	 * 		key	:{site_address}_push_20180514
	 * 		key	:im.akaxin.com:2021_push_20180514
	 * 		field:"other"
	 * </pre>
	 * 
	 * @param globalUserId
	 * @param siteAddress
	 */
	public static void hincrOtherPush(String globalUserId, String siteAddress) {
		try {
			String key = getTodayDBKey(siteAddress + "_push");
			JedisClient jedis = new JedisClient();
			jedis.hincrBy(key, "other", 1);
		} catch (Exception e) {
			logger.error("hincr other push error siteAddress=" + siteAddress, e);
		}

		// 回填一次站点统计数据
		// addUserVisitSite(globalUserId, siteAddress);
	}

	private static String getTodayDBKey(String typeName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dayTime = sdf.format(new Date());
		return typeName + "_" + dayTime;
	}

	private static String getYesterdayDBKey(String typeName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dayTime = sdf.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000l));
		return typeName + "_" + dayTime;
	}

}
