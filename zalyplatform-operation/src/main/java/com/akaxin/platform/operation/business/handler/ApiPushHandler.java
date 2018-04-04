package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.ServerAddress;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.platform.operation.business.dao.MuteSettingDao;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.business.dao.UserTokenDao;
import com.akaxin.platform.operation.constant.PushText;
import com.akaxin.platform.operation.exceptions.RequestException;
import com.akaxin.platform.operation.executor.ImOperateExecutor;
import com.akaxin.platform.operation.push.PushNotification;
import com.akaxin.platform.operation.push.apns.ApnsPackage;
import com.akaxin.platform.operation.push.apns.PushApnsNotification;
import com.akaxin.platform.operation.push.xiaomi.XiaomiPackage;
import com.akaxin.platform.operation.utils.PushAuthLog;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.constant.UserKey;
import com.akaxin.proto.client.ImPtcPushProto;
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
	 * 用户点击登陆站点，允许站点发送push认证
	 * client->platform
	 * 
	 * </pre>
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse auth(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;

		try {
			ApiPushAuthProto.ApiPushAuthRequest request = ApiPushAuthProto.ApiPushAuthRequest
					.parseFrom(command.getParams());
			String userId = command.getSiteUserId();
			String deviceId = command.getDeviceId();
			String siteAddress = request.getSiteAddress();
			String port = request.getSitePort();
			String name = request.getSiteName();
			String userToken = request.getUserToken();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNoneEmpty(userId, deviceId, siteAddress, port, userToken)) {
				// save db
				String redisKey = RedisKeyUtils.getUserTokenKey(deviceId);
				String siteServer = siteAddress + ":" + port;
				logger.debug("add user token,key:{},field:{},value:{}", redisKey, siteServer, userToken);
				if (UserTokenDao.getInstance().addUserToken(redisKey, siteServer, userToken)) {
					UserInfoDao.getInstance().updateUserField(userId, UserKey.deviceId, deviceId);
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}

			PushAuthLog.getInstance().printLog("api.push.auth command={} request={} result={}", command.toString(),
					request.toString(), errCode.toString());

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}

		return commandResponse.setErrCode2(errCode);
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
		ErrorCode2 errCode = ErrorCode2.ERROR;

		try {
			ApiPushNotificationProto.ApiPushNotificationRequest request = ApiPushNotificationProto.ApiPushNotificationRequest
					.parseFrom(command.getParams());
			PushProto.PushType pushType = request.getPushType();
			PushProto.Notification notification = request.getNotification();
			String siteServer = notification.getSiteServer();
			String userId = notification.getUserId();
			String userToken = notification.getUserToken();
			String title = notification.getPushTitle();
			String pushFromId = notification.getPushFromId(); // 发送着用户siteUserId或者群组groupId
			String pushFromName = notification.getPushFromName();// 发送者用户昵称或者群组昵称
			String pushAlter = notification.getPushAlert();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyBlank(userId, userToken, siteServer)) {
				throw new RequestException(ErrorCode2.ERROR_PARAMETER);
			}

			// 首先判断当前用户是否对该站点屏蔽
			ServerAddress address = new ServerAddress(siteServer);
			if (MuteSettingDao.getInstance().checkSiteMute(userId, address)) {
				throw new RequestException(ErrorCode2.SUCCESS);
			}

			title = StringHelper.getSubString(title, 20);

			// 获取最新一次登陆的用户设备ID
			String deviceId = UserInfoDao.getInstance().getLatestDeviceId(userId);
			// 获取最新登陆（auth）设备对应的用户令牌（usertoken）
			String userToken2 = UserTokenDao.getInstance().getUserToken(RedisKeyUtils.getUserTokenKey(deviceId),
					siteServer);
			// 如果用户令牌相同，则相等（授权校验方式）
			logger.debug("api.push.notification check site_user_token:{} platform_user_token:{}", userToken,
					userToken2);
			if (userToken.equals(userToken2)) {
				ClientProto.ClientType clientType = UserInfoDao.getInstance().getClientType(userId);
				logger.debug("api.push.notification clientType={}", clientType);

				switch (clientType) {
				case IOS:
					String pushToken = UserInfoDao.getInstance().getPushToken(userId);
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
						apnsPack.setPushGoto(getPushGoto(address, pushType, pushFromId));
						PushApnsNotification.getInstance().pushNotification(apnsPack);
					}
					break;
				case ANDROID_HUAWEI:
				case ANDROID_OPPO:
				case ANDROID_XIAOMI:
					String xmPushToken = UserInfoDao.getInstance().getPushToken(userId);
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
						xmpack.setPushGoto(getPushGoto(address, pushType, pushFromId));
						PushNotification.pushXiaomiNotification(xmpack);
					}

					break;
				case ANDROID:
					Command pushCommand = buildPushCommand(pushType, notification);
					pushCommand.setDeviceId(deviceId);
					logger.debug("andorid push to client pushcommand={}", pushCommand.toString());
					ImOperateExecutor.getExecutor().execute("im.ptc.push", pushCommand);
				default:
					logger.error("unknow client type:{}", clientType);
					break;
				}
				errCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			if (e instanceof RequestException) {
				errCode = ((RequestException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			if (!errCode.isSuccess()) {
				LogUtils.requestErrorLog(logger, command, e);
			}
		}
		return commandResponse.setErrCode2(errCode);
	}

	private Command buildPushCommand(PushProto.PushType pushType, PushProto.Notification notification) {
		String siteServer = notification.getSiteServer();// 192.168.0.1:2021
		String pushTitle = notification.getPushTitle();
		String pushFromName = notification.getPushFromName();
		String pushAlter = notification.getPushAlert();
		String pushFromId = notification.getPushFromId();

		ImPtcPushProto.ImPtcPushRequest.Builder ippRequest = ImPtcPushProto.ImPtcPushRequest.newBuilder();
		ippRequest.setSiteServer(siteServer);
		ServerAddress address = new ServerAddress(siteServer);
		if (address.isRightAddress()) {
			pushTitle = pushTitle + " " + address.getAddress();
		}
		ippRequest.setPushTitle(pushTitle);
		ippRequest.setPushAlert(getAlterText(address, pushFromName, pushAlter, pushType));
		ippRequest.setPushJump(getPushGoto(address, pushType, pushFromId));
		ippRequest.setPushBadge(1);
		ippRequest.setPushSound("default.caf");// 使用系统默认

		Command command = new Command();
		command.setAction("im.ptc.push");
		command.setParams(ippRequest.build().toByteArray());
		logger.debug("build push command={}", command.toString());
		return command;
	}

	private String getAlterText(ServerAddress address, String fromName, String pushAlter, PushProto.PushType pushType) {
		// 平台是否配置允许该站点host发送明文push
		// if (PushHost.isAuthedAddress(address)) {
		if (StringUtils.isNotEmpty(pushAlter)) {
			if (StringUtils.isNotEmpty(fromName)) {
				return fromName + ":" + pushAlter;
			}
			return pushAlter;
		}
		// }

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
	private String getPushGoto(ServerAddress address, PushProto.PushType pushType, String id) {
		String pageValue = getGotoType(pushType);
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
	private String getGotoType(PushProto.PushType pushType) {
		switch (pushType) {
		case PUSH_NOTICE:
			return "notice";
		case PUSH_GROUP_TEXT:
		case PUSH_GROUP_IMAGE:
		case PUSH_GROUP_VOICE:
			return "group_msg";
		case PUSH_TEXT:
		case PUSH_IMAGE:
		case PUSH_VOICE:
		case PUSH_SECRET_TEXT:
		case PUSH_SECRET_IMAGE:
		case PUSH_GROUP_SECRET_VOICE:
			return "u2_msg";
		case PUSH_APPLY_FRIEND_NOTICE:
			return "friend_apply";// 新的好友申请
		default:
			return "main ";
		}
	}

}
