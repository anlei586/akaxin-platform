package com.akaxin.platform.operation.business.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
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
public class ApiDeviceHandler extends AbstractApiHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ApiDeviceHandler.class);

	/**
	 * 上传设备唯一码,暂时不可使用
	 * 
	 * @param command
	 * @return
	 */
	@Deprecated
	public CommandResponse token(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiUserPushTokenProto.ApiUserPushTokenRequest request = ApiUserPushTokenProto.ApiUserPushTokenRequest
					.parseFrom(command.getParams());
			ClientProto.ClientType clientType = request.getClientType();
			String rom = request.getRom();
			String pushToken = request.getPushToken();
			String deviceId = command.getDeviceId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			UserBean userBean = new UserBean();
			userBean.setUserId(command.getSiteUserId());
			userBean.setClientType(clientType.getNumber());
			userBean.setRom(rom);
			userBean.setPushToken(pushToken);
			userBean.setDeviceId(deviceId);

			if (UserInfoDao.getInstance().saveUserInfo(userBean)) {
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR2_USER_SAVE_PUSHTOKEN;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

}
