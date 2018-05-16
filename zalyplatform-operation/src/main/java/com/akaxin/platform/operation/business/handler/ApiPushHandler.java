package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.IErrorCode;
import com.akaxin.platform.common.constant.CommandConst;
import com.akaxin.platform.common.constant.ErrorCode;
import com.akaxin.platform.common.exceptions.ErrCodeException;
import com.akaxin.platform.common.logs.Log2Utils;
import com.akaxin.platform.common.utils.ServerAddress;
import com.akaxin.platform.common.utils.StringHelper;
import com.akaxin.platform.operation.business.dao.MuteSettingDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.business.dao.UserTokenDao;
import com.akaxin.platform.operation.constant.PushText;
import com.akaxin.platform.operation.monitor.PushMonitor;
import com.akaxin.platform.operation.push.PushNotification;
import com.akaxin.platform.operation.push.apns.ApnsPackage;
import com.akaxin.platform.operation.push.apns.PushApnsNotification;
import com.akaxin.platform.operation.push.umeng.UmengPackage;
import com.akaxin.platform.operation.push.xiaomi.XiaomiPackage;
import com.akaxin.platform.operation.utils.PushStatistics;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.constant.UserKey;
import com.akaxin.proto.core.ClientProto;
import com.akaxin.proto.core.PushProto;
import com.akaxin.proto.platform.ApiPushAuthProto;
import com.akaxin.proto.platform.ApiPushNotificationProto;

/**
 * 站点通过api请求（api.push.notification）请求平台给用户发送push
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.08 15:11:48
 * @param <Command>
 */
public class ApiPushHandler extends AbstractApiHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ApiPushHandler.class);

	/**
	 * <pre>
	 * 
	 * 每次访问站点同时执行 api.push.auth
	 * 用户点击登陆站点，允许站点发送push认证 client -> platform
	 * 
	 * </pre>
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse auth(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiPushAuthProto.ApiPushAuthRequest request = ApiPushAuthProto.ApiPushAuthRequest
					.parseFrom(command.getParams());
			String globalUserId = command.getGlobalUserId();
			String deviceId = command.getDeviceId();
			String siteAddress = request.getSiteAddress();
			String port = request.getSitePort();
			String name = request.getSiteName();
			String userToken = request.getUserToken();

			// info log
			Log2Utils.requestInfoLog(logger, command,
					StringHelper.format("siteAddress={} siteName={}", siteAddress + ":" + port, name));

			// data statistics
			PushStatistics.addUserVisitSite(globalUserId, siteAddress + ":" + port);

			if (StringUtils.isNoneEmpty(globalUserId, deviceId, siteAddress, port, userToken)) {
				// save db
				String redisKey = RedisKeyUtils.getUserTokenKey(deviceId);
				String siteServer = siteAddress + ":" + port;
				logger.debug("add user token,key:{},field:{},value:{}", redisKey, siteServer, userToken);
				if (UserTokenDao.getInstance().addUserToken(redisKey, siteServer, userToken)) {
					UserInfoDao.getInstance().updateUserField(globalUserId, UserKey.deviceId, deviceId);
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			Log2Utils.requestErrorLog(logger, command, e);
		}

		return commandResponse.setErrCode(errCode);
	}

	/**
	 * <pre>
	 *  site - > platform
	 * 	action:
	 * 		api.push.notification
	 * </pre>
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse notification(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiPushNotificationProto.ApiPushNotificationRequest request = ApiPushNotificationProto.ApiPushNotificationRequest
					.parseFrom(command.getParams());
			PushProto.PushType pushType = request.getPushType();
			PushProto.Notification notification = request.getNotification();
			String siteServer = notification.getSiteServer();
			String globalUserId = notification.getUserId();
			String userToken = notification.getUserToken();
			String title = notification.getPushTitle();
			String pushFromId = notification.getPushFromId(); // 发送着用户siteUserId或者群组groupId
			String pushFromName = notification.getPushFromName();// 发送者用户昵称或者群组昵称
			String pushAlter = notification.getPushAlert();

			// debug log
			Log2Utils.requestDebugLog(logger, command, request.toString());
			// qps monitor
			PushMonitor.COUNTER_TOTAL.inc();

			if (StringUtils.isAnyBlank(globalUserId, userToken, siteServer)) {
				throw new ErrCodeException(ErrorCode.ERROR_PARAMETER);
			}

			// push total statistics
//			PushStatistics.hincrPush(siteServer, globalUserId);

			// 首先判断当前用户是否对该站点屏蔽
			ServerAddress address = new ServerAddress(siteServer);
			if (MuteSettingDao.getInstance().checkSiteMute(globalUserId, address)) {
				throw new ErrCodeException(ErrorCode.SUCCESS);
			}

			title = StringHelper.getSubString(title, 20);

			// 获取最新一次登陆的用户设备ID
			String deviceId = UserInfoDao.getInstance().getLatestDeviceId(globalUserId);
			// 获取最新登陆（auth）设备对应的用户令牌（usertoken）
			String userToken2 = UserTokenDao.getInstance().getUserToken(RedisKeyUtils.getUserTokenKey(deviceId),
					siteServer);
			// 如果用户令牌相同，则相等（授权校验方式）
			logger.debug("api.push.notification check site_user_token:{} platform_user_token:{}", userToken,
					userToken2);
			if (userToken.equals(userToken2)) {
				ClientProto.ClientType clientType = UserInfoDao.getInstance().getClientType(globalUserId);
				logger.debug("api.push.notification clientType={}", clientType);

				switch (clientType) {
				case IOS:
					String pushToken = UserInfoDao.getInstance().getPushToken(globalUserId);
					logger.debug("ios push ......pushToken={}", pushToken);
					if (StringUtils.isNotBlank(pushToken)) {
						ApnsPackage apnsPack = new ApnsPackage();
						apnsPack.setToken(pushToken);
						apnsPack.setBadge(1);

						if (address.isRightAddress()) {
							apnsPack.setTitle(title + " " + address.getAddress());
						} else {
							apnsPack.setTitle(title);
						}
						apnsPack.setBody(getAlterText(address, pushFromName, pushAlter, pushType));
						apnsPack.setPushGoto(getPushGoto(globalUserId, address, pushType, pushFromId));
						PushApnsNotification.getInstance().pushNotification(apnsPack);
					}
					break;
				case ANDROID_XIAOMI:
					String xmPushToken = UserInfoDao.getInstance().getPushToken(globalUserId);
					logger.debug("xiaomi push......pushToken={}", xmPushToken);
					if (StringUtils.isNotBlank(xmPushToken)) {
						XiaomiPackage xmpack = new XiaomiPackage();
						xmpack.setPushToken(xmPushToken);
						xmpack.setBadge(1);
						if (address.isRightAddress()) {
							xmpack.setTitle(title + " " + address.getAddress());
						} else {
							xmpack.setTitle(title);
						}
						xmpack.setDescription(getAlterText(address, pushFromName, pushAlter, pushType));
						xmpack.setPushGoto(getPushGoto(globalUserId, address, pushType, pushFromId));
						PushNotification.pushXiaomiNotification(xmpack);
					}

					break;
				case ANDROID_HUAWEI:
				case ANDROID_OPPO:
				case ANDROID:
					String umengToken = UserInfoDao.getInstance().getPushToken(globalUserId);
					if (StringUtils.isNotBlank(umengToken)) {
						UmengPackage umpack = new UmengPackage();
						umpack.setPushToken(umengToken);
						if (address.isRightAddress()) {
							umpack.setTitle(title + " " + address.getAddress());
						} else {
							umpack.setTitle(title);
						}
						umpack.setText(getAlterText(address, pushFromName, pushAlter, pushType));
						umpack.setPushGoto(getPushGoto(globalUserId, address, pushType, pushFromId));
						logger.debug("andorid push to client push package={}", umpack.toString());
						PushNotification.pushUMengNotification(umpack);
					}
					break;
				default:
					logger.error("unknow client type:{}", clientType);
					break;
				}
				errCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			PushMonitor.COUNTER_ERROR.inc();
			if (e instanceof ErrCodeException) {
				errCode = ((ErrCodeException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			if (!errCode.isSuccess()) {
				Log2Utils.requestErrorLog(logger, command, e);
			}
		}
		return commandResponse.setErrCode(errCode);
	}

	// private Command buildPushCommand(PushProto.PushType pushType,
	// PushProto.Notification notification) {
	// String siteServer = notification.getSiteServer();// 192.168.0.1:2021
	// String pushTitle = notification.getPushTitle();
	// String pushFromName = notification.getPushFromName();
	// String pushAlter = notification.getPushAlert();
	// String pushFromId = notification.getPushFromId();
	//
	// ImPtcPushProto.ImPtcPushRequest.Builder ippRequest =
	// ImPtcPushProto.ImPtcPushRequest.newBuilder();
	// ippRequest.setSiteServer(siteServer);
	// ServerAddress address = new ServerAddress(siteServer);
	// if (address.isRightAddress()) {
	// pushTitle = pushTitle + " " + address.getAddress();
	// }
	// ippRequest.setPushTitle(pushTitle);
	// ippRequest.setPushAlert(getAlterText(address, pushFromName, pushAlter,
	// pushType));
	// ippRequest.setPushJump(getPushGoto(address, pushType, pushFromId));
	// ippRequest.setPushBadge(1);
	// ippRequest.setPushSound("default.caf");// 使用系统默认
	//
	// Command command = new Command();
	// command.setAction("im.ptc.push");
	// command.setParams(ippRequest.build().toByteArray());
	// logger.debug("build push command={}", command.toString());
	// return command;
	// }

	private String getAlterText(ServerAddress address, String fromName, String pushAlter, PushProto.PushType pushType) {
		// 平台是否配置允许该站点host发送明文push
		if (StringUtils.isNotEmpty(pushAlter)) {
			if (StringUtils.isNotEmpty(fromName)) {
				return fromName + ":" + pushAlter;
			}
			return pushAlter;
		}

		switch (pushType) {
		case PUSH_TEXT:
			return PushText.TEXT;
		case PUSH_GROUP_TEXT:
			return PushText.GROUP_TEXT;
		case PUSH_SECRET_TEXT:
		case PUSH_GROUP_SECRET_TEXT:
			return PushText.SECRE_TEXT;
		case PUSH_IMAGE:
			return PushText.IMAGE_TEXT;
		case PUSH_GROUP_IMAGE:
			return PushText.GROUP_IMAGE_TEXT;
		case PUSH_SECRET_IMAGE:
		case PUSH_GROUP_SECRET_IMAGE:
			return PushText.SECRE_IMAGE_TEXT;
		case PUSH_VOICE:
			return PushText.AUDIO_TEXT;
		case PUSH_GROUP_VOICE:
			return PushText.GROUP_AUDIO_TEXT;
		case PUSH_SECRET_VOICE:
			return PushText.SECRE_AUDIO_TEXT;
		case PUSH_APPLY_FRIEND_NOTICE:
			return PushText.NEW_FRIEND_APPLY;
		default:
			break;
		}
		return PushText.DEFAULT_TEXT;
	}

	/**
	 * <pre>
	 *		zaly://domain-name/goto?page="main" 主帧
	 * 		zaly://domain-name/goto?page="message"	消息帧
	 * 		zaly://domain-name/goto?page="contacts"	通讯录帧
	 * 		zaly://domain-name/goto?page="personal"	个人帧
	 * 		zaly://domain-name/goto?page="u2-msg"&siteUserId="" 个人消息帧
	 * </pre>
	 * 
	 * @param pushType
	 * @return
	 */
	private String getPushGoto(String globalUserId, ServerAddress address, PushProto.PushType pushType, String id) {
		String pageValue = getGotoType(globalUserId, address, pushType);
		String pushGoto = "zaly://" + address.getFullAddress() + "/goto?page=" + pageValue;
		if ("u2_msg".equals(pageValue)) {
			pushGoto += "&userId=" + id;
		} else if ("group_msg".equals(pageValue)) {
			pushGoto += "&groupId=" + id;
		}
		return pushGoto;
	}

	/**
	 * <pre>
	 * 0:二人消息PUSH
	 * 1:群组消息PUSH
	 * 2:通知PUSH
	 * </pre>
	 * 
	 * @param pushType
	 * @return
	 */
	private String getGotoType(String globalUserId, ServerAddress address, PushProto.PushType pushType) {
		switch (pushType) {
		case PUSH_NOTICE:
			PushMonitor.COUNTER_OTHERS.inc();
			PushStatistics.hincrOtherPush(globalUserId, address.getFullAddress());
			return "notice";
		case PUSH_GROUP_TEXT:
			PushMonitor.COUNTER_G_TEXT.inc();
			PushStatistics.hincrGroupPush(globalUserId, address.getFullAddress());
			return "group_msg";
		case PUSH_GROUP_IMAGE:
			PushMonitor.COUNTER_G_PIC.inc();
			PushStatistics.hincrGroupPush(globalUserId, address.getFullAddress());
			return "group_msg";
		case PUSH_GROUP_VOICE:
			PushMonitor.COUNTER_G_AUDIO.inc();
			PushStatistics.hincrGroupPush(globalUserId, address.getFullAddress());
			return "group_msg";
		case PUSH_TEXT:
			PushMonitor.COUNTER_U2_TEXT.inc();
			PushStatistics.hincrU2Push(globalUserId, address.getFullAddress());
			return "u2_msg";
		case PUSH_IMAGE:
			PushMonitor.COUNTER_U2_PIC.inc();
			PushStatistics.hincrU2Push(globalUserId, address.getFullAddress());
			return "u2_msg";
		case PUSH_VOICE:
			PushMonitor.COUNTER_U2_AUDIO.inc();
			PushStatistics.hincrU2Push(globalUserId, address.getFullAddress());
			return "u2_msg";
		case PUSH_SECRET_TEXT:
			PushMonitor.COUNTER_U2_TEXTS.inc();
			PushStatistics.hincrU2Push(globalUserId, address.getFullAddress());
			return "u2_msg";
		case PUSH_SECRET_IMAGE:
			PushMonitor.COUNTER_U2_PICS.inc();
			PushStatistics.hincrU2Push(globalUserId, address.getFullAddress());
			return "u2_msg";
		case PUSH_SECRET_VOICE:
			PushMonitor.COUNTER_U2_AUDIOS.inc();
			PushStatistics.hincrU2Push(globalUserId, address.getFullAddress());
			return "u2_msg";
		case PUSH_APPLY_FRIEND_NOTICE:
			PushMonitor.COUNTER_OTHERS.inc();
			PushStatistics.hincrOtherPush(globalUserId, address.getFullAddress());
			return "friend_apply";// 新的好友申请
		default:
			PushMonitor.COUNTER_OTHERS.inc();
			PushStatistics.hincrOtherPush(globalUserId, address.getFullAddress());
			return "main";
		}
	}

}
