package com.akaxin.platform.operation.business.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.platform.operation.business.dao.UserInfoDao;
import com.akaxin.platform.storage.bean.UserBean;
import com.akaxin.proto.core.ClientProto;
import com.akaxin.proto.platform.ApiUserPushTokenProto;

/**
 * service:ApiUserInfo methods:
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 */
public class ApiDeviceHandler extends AbstractApiHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ApiDeviceHandler.class);

	/**
	 * 上传设备唯一码
	 * 
	 * @param command
	 * @return
	 */
	public boolean token(Command command) {
		logger.info("----api.device.token command={}", command.toString());
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiUserPushTokenProto.ApiUserPushTokenRequest request = ApiUserPushTokenProto.ApiUserPushTokenRequest
					.parseFrom(command.getParams());
			ClientProto.ClientType clientType = request.getClientType();
			String rom = request.getRom();
			String pushToken = request.getPushToken();
			String deviceId = command.getDeviceId();

			logger.info("api.user.pushToken deviceId={} request={}", deviceId, request.toString());

			UserBean userBean = new UserBean();
			userBean.setUserId(command.getSiteUserId());
			userBean.setClientType(clientType.getNumber());
			userBean.setRom(rom);
			userBean.setPushToken(pushToken);
			userBean.setDeviceId(deviceId);

			logger.info("userInfoBean=" + userBean.toString());

			if (UserInfoDao.getInstance().saveUserInfo(userBean)) {
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR2_USER_SAVE_PUSHTOKEN;
			}

		} catch (Exception e) {
			logger.error("api.push token error", e);
		}
		command.setResponse(commandResponse.setErrCode2(errCode));
		return true;
	}

}
