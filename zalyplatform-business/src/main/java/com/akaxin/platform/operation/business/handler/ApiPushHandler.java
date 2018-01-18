package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.business.dao.UserTokenDao;
import com.akaxin.platform.operation.executor.ImOperateExecutor;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.proto.client.ImPtcPushProto;
import com.akaxin.proto.core.ClientProto;
import com.akaxin.proto.core.PushProto;
import com.akaxin.proto.platform.ApiPushAuthProto;
import com.akaxin.proto.platform.ApiPushNotificationProto;
import com.google.protobuf.InvalidProtocolBufferException;
import com.zaly.platform.storage.bean.PushTokenBean;

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
	 * 用户点击登陆站点，允许站点发送push认证
	 * 
	 * @param command
	 * @return
	 */
	public boolean auth(Command command) {
		logger.info("api.push.auth command={}", command.toString());
		CommandResponse commandResponse = new CommandResponse();
		String errorCode = ErrorCode.ERROR;
		try {
			ApiPushAuthProto.ApiPushAuthRequest request = ApiPushAuthProto.ApiPushAuthRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String deviceId = command.getDeviceId();
			String siteAddress = request.getSiteAddress();
			String port = request.getSitePort();
			String name = request.getSiteName();
			String userToken = request.getUserToken();

			logger.info("api.push.auth command={}", command.toString());
			// 判断参数

			// 存库
			String redisKey = RedisKeyUtils.getUserTokenKey(deviceId);
			String tokenField = siteAddress + port;
			logger.info("add user token,key={},tokenField={}", redisKey, tokenField);
			if (UserTokenDao.getInstance().addUserToken(redisKey, tokenField, userToken)) {
				errorCode = ErrorCode.SUCCESS;
				return true;
			}

		} catch (Exception e) {
			logger.error("api.push.auth error", e);
		}
		commandResponse.setErrCode(errorCode);
		return false;
	}

	/**
	 * <pre>
	 * 	action:
	 * 		api.push.notification
	 * </pre>
	 * 
	 * @param command
	 * @return
	 */
	public boolean notification(Command command) {
		try {
			Command pushCommand = null;
			ApiPushNotificationProto.ApiPushNotificationRequest request = ApiPushNotificationProto.ApiPushNotificationRequest
					.parseFrom(command.getParams());
			String userId = request.getUserId();
			logger.info("api.push.notification request={}", request.toString());

			PushTokenBean bean = UserInfoDao.getInstance().getPushToken(userId);

			if (bean != null && StringUtils.isNumeric(bean.getClientType())) {
				ClientProto.ClientType clientType = ClientProto.ClientType
						.forNumber(Integer.valueOf(bean.getClientType()));
				switch (clientType) {
				case IOS:
					logger.info("ios push ......");
					break;
				case ANDROID:
				case ANDROID_HUAWEI:
				case ANDROID_OPPO:
				case ANDROID_XIAOMI:
					pushCommand = buildPushCommand(request);
					break;
				default:
					logger.error("unknow client type:{}", clientType);
					break;
				}
			}
			logger.info("build im push command={}", command.toString());
			if (pushCommand != null) {

				pushCommand.setDeviceId("");

				return ImOperateExecutor.getExecutor().execute("im.ptc.push", pushCommand);
			}
		} catch (InvalidProtocolBufferException e) {
			logger.error("api push notification error.", e);
		}
		return false;
	}

	private Command buildPushCommand(ApiPushNotificationProto.ApiPushNotificationRequest apnRequest) {
		String siteServer = apnRequest.getSiteServer();// 192.168.0.1:2021
		String userId = apnRequest.getUserId();
		String pushTitle = apnRequest.getPushTitle();
		String alertText = apnRequest.getPushAlert();

		// int pushBadge = apnRequest.getPushBadge();
		PushProto.PushType pushType = apnRequest.getType();

		switch (pushType) {
		case U2_MESSAGE:
			alertText = "你收到一条阿卡信消息";
		case GROUP_MESSAGE:
			alertText = "你收到一条阿卡信群消息";
		default:
			break;
		}
		ImPtcPushProto.ImPtcPushRequest.Builder ippRequest = ImPtcPushProto.ImPtcPushRequest.newBuilder();
		ippRequest.setPushTitle(pushTitle);
		ippRequest.setPushAlert(alertText);
		ippRequest.setPushBadge(1);
		ippRequest.setSiteServerAddress(siteServer);
		ippRequest.setPushSound("");
		ippRequest.setPushJump("[tof|1|]");// [goto|message{group}|param][http|1|param]

		Command command = new Command();
		command.setAction("im.ptc.push");
		command.setParams(ippRequest.build().toByteArray());
		logger.info("build push command={}", command.toString());
		return command;
	}

}
