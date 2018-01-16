package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.operation.executor.ImOperateExecutor;
import com.akaxin.proto.client.ImPtcPushProto;
import com.akaxin.proto.core.ClientProto;
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
			String alertText = request.getPushAlert();
			String siteName = request.getSiteServerName();
			String siteAddress = request.getSiteServerAddress();// 192.168.0.1:8080
			String userDeviceId = request.getDeviceId();
			String userId = request.getUserId();
			logger.info("alter={} siteName={} siteAddress={}", alertText, siteName, siteAddress);

			PushTokenBean bean = UserInfoDao.getInstance().getPushToken(userId);

			if (bean != null && StringUtils.isNumeric(bean.getClientType())) {
				ClientProto.ClientType clientType = ClientProto.ClientType
						.forNumber(Integer.valueOf(bean.getClientType()));
				switch (clientType) {
				case IOS:
					break;
				case ANDROID:
				case ANDROID_HUAWEI:
				case ANDROID_OPPO:
				case ANDROID_XIAOMI:
					pushCommand = buildPushCommand(siteAddress, siteName, alertText);
					break;
				default:
					logger.error("unknow client type:{}", clientType);
					break;
				}
			}
			logger.info("build im push command={}", command.toString());
			if (pushCommand != null) {
				pushCommand.setDeviceId(userDeviceId);
				return ImOperateExecutor.getExecutor().execute("im.ptc.push", pushCommand);
			}
		} catch (InvalidProtocolBufferException e) {
			logger.error("api push notification error.", e);
		}
		return false;
	}

	private Command buildPushCommand(String siteAddress, String siteName, String alertText) {
		ImPtcPushProto.ImPtcPushRequest.Builder ippRequest = ImPtcPushProto.ImPtcPushRequest.newBuilder();
		ippRequest.setPushAlert(siteName + ":" + alertText);
		ippRequest.setPushBadge(1);
		ippRequest.setSiteServerAddress(siteAddress);
		ippRequest.setPushSound("");
		ippRequest.setPushJump("[tof|1|]");// [goto|message{group}|param][http|1|param]

		Command command = new Command();
		command.setAction("im.ptc.push");
		command.setParams(ippRequest.build().toByteArray());
		logger.info("build push command={}", command.toString());
		return command;
	}

}
