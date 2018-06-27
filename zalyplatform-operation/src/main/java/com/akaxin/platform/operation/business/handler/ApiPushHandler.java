package com.akaxin.platform.operation.business.handler;

import java.util.List;
import java.util.Map;

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
import com.akaxin.platform.operation.statistics.PushCount;
import com.akaxin.platform.operation.statistics.UserVisitSiteCount;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.constant.UserKey;
import com.akaxin.proto.core.ClientProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.PushProto;
import com.akaxin.proto.platform.ApiPushAuthProto;
import com.akaxin.proto.platform.ApiPushNotificationProto;
import com.akaxin.proto.platform.ApiPushNotificationsProto;

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
			Map<Integer, String> header = command.getHeader();
			String clientType = header.get(CoreProto.HeaderKey.CLIENT_SOCKET_TYPE_VALUE);
			String globalUserId = command.getGlobalUserId();
			String deviceId = command.getDeviceId();
			String siteAddress = request.getSiteAddress();
			String port = request.getSitePort();
			String name = request.getSiteName();
			String userToken = request.getUserToken();
			// info log
			Log2Utils.requestInfoLog(logger, command, StringHelper.format("siteAddress={} siteName={} userToken={}",
					siteAddress + ":" + port, name, userToken));

			if (StringUtils.isAnyEmpty(globalUserId, deviceId, siteAddress, port, userToken)) {
				throw new ErrCodeException(ErrorCode.ERROR_PARAMETER);
			}

			// data statistics
			UserVisitSiteCount.addVisitUser(globalUserId, siteAddress + ":" + port);

			// save db
			String redisKey = RedisKeyUtils.getUserTokenKey(deviceId);
			String siteServer = siteAddress + ":" + port;
			logger.debug("add user token,key:{},field:{},value:{}", redisKey, siteServer, userToken);
			if (UserTokenDao.getInstance().addUserToken(redisKey, siteServer, userToken)) {
				UserInfoDao.getInstance().updateUserField(globalUserId, UserKey.deviceId, deviceId);
				if (StringUtils.isNumeric(clientType)) {
					UserInfoDao.getInstance().updateUserField(globalUserId, UserKey.clientType, clientType);
					logger.debug("update user device clientType={}",
							ClientProto.ClientType.forNumber(Integer.valueOf(clientType)));
				}
				errCode = ErrorCode2.SUCCESS;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			Log2Utils.requestErrorLog(logger, command, e);
		} catch (ErrCodeException e) {
			errCode = e.getErrCode();
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
			String toGlobalUserId = notification.getUserId();
			String userToken = notification.getUserToken();// 单人发送
			List<String> userTokenList = notification.getUserTokensList();// 批量发送
			String title = notification.getPushTitle();
			String pushFromId = notification.getPushFromId(); // 发送着用户siteUserId
			String pushFromName = notification.getPushFromName();// 发送者用户昵称或者群组昵称
			String pushAlter = notification.getPushAlert();

			// debug log
			Log2Utils.requestDebugLog(logger, command, request.toString());
			// qps monitor
			PushMonitor.COUNTER_TOTAL.inc();

			if (StringUtils.isAnyBlank(toGlobalUserId, siteServer)) {
				throw new ErrCodeException(ErrorCode.ERROR_PARAMETER);
			}

			if (StringUtils.isEmpty(userToken) && (userTokenList == null || userTokenList.size() == 0)) {
				throw new ErrCodeException(ErrorCode.ERROR_PUSH_USERTOKEN);
			}

			// 首先判断当前用户是否对该站点屏蔽
			ServerAddress address = new ServerAddress(siteServer);
			UserVisitSiteCount.addVisitUser(toGlobalUserId, address.getFullAddress());

			errCode = pushToOneUser(toGlobalUserId, userToken, userTokenList, pushType, address, title, null, pushAlter,
					pushFromId, pushFromName);

		} catch (Exception e) {
			PushMonitor.COUNTER_ERROR.inc();
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			Log2Utils.requestErrorLog(logger, command, e);
		} catch (ErrCodeException e) {
			PushMonitor.COUNTER_ERROR.inc();
			errCode = e.getErrCode();
			Log2Utils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * api.push.notifications 支持批量发送，后期同意使用此方法发送push
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse notifications(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiPushNotificationsProto.ApiPushNotificationsRequest request = ApiPushNotificationsProto.ApiPushNotificationsRequest
					.parseFrom(command.getParams());
			PushProto.PushType pushType = request.getPushType();
			PushProto.Notifications notifications = request.getNotifications();
			PushProto.PushFromUser fromUser = request.getPushFromUser();
			List<com.akaxin.proto.core.PushProto.PushToUser> toUserList = request.getPushToUserList();

			String siteServer = notifications.getSiteServer();
			String title = notifications.getPushTitle();
			String subTitle = notifications.getPushSubtitle();
			String pushAlter = notifications.getPushAlert();

			String pushFromId = fromUser.getSiteUserId();
			String fromGlobalUserId = fromUser.getGlobalUserId();
			String pushFromName = fromUser.getPushFromName();// 发送者用户昵称或者群组昵称

			// debug log
			Log2Utils.requestDebugLog(logger, command, request.toString());

			// qps monitor
			PushMonitor.COUNTER_TOTAL.inc();
			// 参数校验
			if (StringUtils.isAnyBlank(fromGlobalUserId, title, siteServer) || toUserList == null) {
				throw new ErrCodeException(ErrorCode.ERROR_PARAMETER);
			}

			// 首先判断当前用户是否对该站点屏蔽
			ServerAddress address = new ServerAddress(siteServer);
			// 回填一次发送者的用户访问站点记录
			UserVisitSiteCount.addVisitUser(fromGlobalUserId, address.getFullAddress());

			for (PushProto.PushToUser pushToUser : toUserList) {
				String userToken = pushToUser.getUserToken();
				String toGlobalUserId = pushToUser.getGlobalUserId();
				List<String> userTokenList = pushToUser.getUserTokensList();
				pushToOneUser(toGlobalUserId, userToken, userTokenList, pushType, address, title, subTitle, pushAlter,
						pushFromId, pushFromName);
			}

		} catch (Exception e) {
			PushMonitor.COUNTER_ERROR.inc();
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			Log2Utils.requestErrorLog(logger, command, e);
		} catch (ErrCodeException e) {
			PushMonitor.COUNTER_ERROR.inc();
			errCode = e.getErrCode();
			Log2Utils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	private IErrorCode pushToOneUser(String globalUserId, String userToken, List<String> userTokenList,
			PushProto.PushType pushType, ServerAddress address, String title, String subTitle, String pushAlter,
			String pushFromId, String pushFromName) {
		IErrorCode errCode = ErrorCode.ERROR;
		try {
			// 统计push的数量
			PushCount.addPushMonitor(globalUserId, address, pushType);

			// 判断用户是否对站点静音
			if (MuteSettingDao.getInstance().checkSiteMute(globalUserId, address)) {
				logger.warn("globalUserId={} set address={} mute", globalUserId, address.getFullAddress());
				return ErrorCode.ERROR_PUSH_MUTE;
			}

			// 截取title在合法长度范围内
			title = StringHelper.getSubString(title, 30);

			// 获取最新一次登陆的用户设备ID
			String deviceId = UserInfoDao.getInstance().getLatestDeviceId(globalUserId);
			// 获取最新登陆（auth）设备对应的用户令牌（usertoken）
			logger.debug("api.push.notification deviceId={} userTokenKey={} siteServer={}", deviceId,
					RedisKeyUtils.getUserTokenKey(deviceId), address.getFullAddress());
			String rightUserToken = UserTokenDao.getInstance().getUserToken(RedisKeyUtils.getUserTokenKey(deviceId),
					address.getFullAddress());
			// 如果用户令牌相同，则相等（授权校验方式）
			logger.debug("api.push.notification check site_user_token:{} platform_user_token:{}", userToken,
					rightUserToken);

			boolean isRightToken = isRightUserTokens(globalUserId, deviceId, userToken, userTokenList, rightUserToken);

			logger.debug("globalUserId={} deviceId={} check right userToken:{}", globalUserId, deviceId, isRightToken);

			if (!isRightToken) {
				return ErrorCode.ERROR_PUSH_RIGHT_USERTOKEN;
			}

			ClientProto.ClientType clientType = UserInfoDao.getInstance().getClientType(globalUserId);
			// logger.debug("api.push.notification clientType={}", clientType);

			switch (clientType) {
			case IOS:
				String pushToken = UserInfoDao.getInstance().getPushToken(globalUserId);
				logger.debug("{} push ......pushToken={}", clientType, pushToken);
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
			case ANDROID_XIAOMI:
				String xmPushToken = UserInfoDao.getInstance().getPushToken(globalUserId);
				logger.debug("{} push......pushToken={}", clientType, xmPushToken);
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
			case ANDROID_HUAWEI:
			case ANDROID_OPPO:
			case ANDROID:
				String umengToken = UserInfoDao.getInstance().getPushToken(globalUserId);
				logger.debug("{} umeng push......pushToken={}", clientType, umengToken);
				if (StringUtils.isNotBlank(umengToken)) {
					UmengPackage umpack = new UmengPackage();
					umpack.setPushToken(umengToken);
					if (address.isRightAddress()) {
						umpack.setTitle(title + " " + address.getAddress());
					} else {
						umpack.setTitle(title);
					}
					umpack.setText(getAlterText(address, pushFromName, pushAlter, pushType));
					umpack.setPushGoto(getPushGoto(address, pushType, pushFromId));
					PushNotification.pushUMengNotification(umpack);
				}
				break;
			default:
				errCode = ErrorCode.ERROR_PUSH_NO_CLIENTTYPE;
				logger.debug("unknow clientType : {} error push......", clientType);
				break;
			}
			errCode = ErrorCode2.SUCCESS;

		} catch (Exception e) {
			logger.error("send push to one user error", e);
		}

		return errCode;
	}

	private boolean isRightUserTokens(String globalUserId, String deviceId, String userToken, List<String> tokenList,
			String rightToken) {
		if (StringUtils.isEmpty(rightToken)) {
			logger.error("globalUserId={} deviceId={} platform usertoken is null", globalUserId, deviceId);
			return false;
		}

		if (rightToken.equals(userToken)) {
			return true;
		}

		if (tokenList != null && tokenList.size() > 0) {
			for (String token : tokenList) {
				if (rightToken.equals(token)) {
					return true;
				}
			}
		}

		return false;
	}

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
		case PUSH_SECRET_VOICE:
			return "u2_msg";
		case PUSH_APPLY_FRIEND_NOTICE:
			return "friend_apply";// 新的好友申请
		default:
			return "main";
		}
	}

}
