package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.business.dao.UserTokenDao;
import com.akaxin.platform.operation.executor.ImOperateExecutor;
import com.akaxin.platform.operation.push.apns.ApnsPackage;
import com.akaxin.platform.operation.push.apns.PushNotificationService;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.constant.UserKey;
import com.akaxin.proto.client.ImPtcPushProto;
import com.akaxin.proto.core.ClientProto;
import com.akaxin.proto.core.CoreProto;
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
public class ApiPushHandler extends AbstractApiHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ApiPushHandler.class);

	/**
	 * <pre>
	 * 用户点击登陆站点，允许站点发送push认证
	 * client->platform
	 * </pre>
	 * 
	 * @param command
	 * @return
	 */
	public boolean auth(Command command) {
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
			logger.info("api.push.auth command={} request={}", command.toString(), request.toString());

			if (StringUtils.isNoneEmpty(userId, deviceId, siteAddress, port, userToken)) {
				// 存库
				String redisKey = RedisKeyUtils.getUserTokenKey(deviceId);
				String siteServer = siteAddress + ":" + port;
				logger.info("add user token,key:{},field:{},value:{}", redisKey, siteServer, userToken);
				if (UserTokenDao.getInstance().addUserToken(redisKey, siteServer, userToken)) {
					UserInfoDao.getInstance().updateUserField(userId, UserKey.deviceId, deviceId);
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api.push.auth error", e);
		}
		command.setResponse(commandResponse.setErrCode2(errCode));
		logger.info("api.push.auth result={}", errCode.toString());
		return errCode.isSuccess();
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
	public boolean notification(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.SUCCESS;
		command.setResponse(commandResponse.setErrCode2(errCode));
		try {
			Command pushCommand = null;
			ApiPushNotificationProto.ApiPushNotificationRequest request = ApiPushNotificationProto.ApiPushNotificationRequest
					.parseFrom(command.getParams());
			CoreProto.MsgType pushType = request.getPushType();
			PushProto.Notification notification = request.getNotification();
			String userId = notification.getUserId();
			String siteServer = notification.getSiteServer();
			String userToken = notification.getUserToken();
			String title = notification.getPushTitle();
			logger.info("api.push.notification command={} request={}", command.toString(), request.toString());

			if (StringUtils.isBlank(userId) || StringUtils.isBlank(userToken) || StringUtils.isBlank(siteServer)) {
				logger.info("request parameter error.request={}", request.toString());
				return false;
			}

			String deviceId = UserInfoDao.getInstance().getLatestDeviceId(userId);
			String userToken2 = UserTokenDao.getInstance().getUserToken(RedisKeyUtils.getUserTokenKey(deviceId),
					siteServer);
			// 如果用户令牌相同，则相等（授权校验方式）
			logger.info("api.push.notification check site_user_token:{} platform_user_token:{}", userToken, userToken2);
			if (userToken.equals(userToken2)) {
				ClientProto.ClientType clientType = UserInfoDao.getInstance().getClientType(userId);

				logger.info("api.push.notification clientType={}", clientType);

				switch (clientType) {
				case IOS:
					String pushToken = UserInfoDao.getInstance().getPushToken(userId);
					logger.info("ios push ......pushToken={}", pushToken);
					if (StringUtils.isNotBlank(pushToken)) {
						ApnsPackage apnsPack = new ApnsPackage();
						apnsPack.setToken(pushToken);
						apnsPack.setBadge(1);
						if (StringUtils.isNotBlank(siteServer)) {
							apnsPack.setTitle(title + " " + siteServer);
						} else {
							apnsPack.setTitle(title);
						}
						apnsPack.setBody("你有一条新消息");
						PushNotificationService.getInstance().apnsPushNotification(apnsPack);
					}
					break;
				case ANDROID_HUAWEI:
				case ANDROID_OPPO:
				case ANDROID_XIAOMI:
				case ANDROID:
					pushCommand = buildPushCommand(pushType, notification);
					pushCommand.setDeviceId(deviceId);
					logger.info("andorid push command={}", pushCommand.toString());
					break;
				default:
					logger.error("unknow client type:{}", clientType);
					break;
				}

				if (pushCommand != null) {
					logger.info("im push to client pushcommand={}", pushCommand.toString());
					return ImOperateExecutor.getExecutor().execute("im.ptc.push", pushCommand);
				}

			}
		} catch (Exception e) {
			logger.error("api push notification error.", e);
		}
		logger.info("api.push.notification result={}", errCode.toString());
		return false;
	}

	private Command buildPushCommand(CoreProto.MsgType pushType, PushProto.Notification notification) {
		String siteServer = notification.getSiteServer();// 192.168.0.1:2021
		String pushTitle = notification.getPushTitle();
		String alertText = notification.getPushAlert();

		switch (pushType) {
		case TEXT:
			alertText = "你收到一条阿卡信消息";
		case SECRET_TEXT:
			alertText = "【绝密】你收到一条绝密消息";
		default:
			alertText = "你收到一条阿卡信消息";
			break;
		}
		ImPtcPushProto.ImPtcPushRequest.Builder ippRequest = ImPtcPushProto.ImPtcPushRequest.newBuilder();
		ippRequest.setSiteServer(siteServer);
		if (StringUtils.isNotBlank(siteServer)) {
			pushTitle = pushTitle + " " + siteServer;
		}
		ippRequest.setPushTitle(pushTitle);
		ippRequest.setPushAlert(alertText);
		ippRequest.setPushBadge(1);
		ippRequest.setPushSound("sms-received1.caf");// 使用系统默认
		ippRequest.setPushJump("[tof|1|]");// [goto|message{group}|param][http|1|param]

		Command command = new Command();
		command.setAction("im.ptc.push");
		command.setParams(ippRequest.build().toByteArray());
		logger.info("build push command={}", command.toString());
		return command;
	}

}
