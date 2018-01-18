package com.akaxin.platform.operation.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.platform.operation.api.IMessage;
import com.akaxin.platform.operation.business.dao.SessionDao;
import com.akaxin.platform.operation.executor.ApiOperateExecutor;
import com.akaxin.platform.operation.executor.ImOperateExecutor;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.proto.core.CoreProto;
import com.zaly.platform.storage.constant.UserKey;

public class MesageService implements IMessage {
	private static final Logger logger = LoggerFactory.getLogger(MesageService.class);

	public CommandResponse doApiRequest(Command command) {
		logger.info("platform api request command={}", command.toString());
		try {
			Map<Integer, String> header = command.getHeader();
			String sessionId = header.get(CoreProto.HeaderKey.CLIENT_SOCKET_SITE_SESSION_ID_VALUE);
			String sessionKey = RedisKeyUtils.getSessionKey(sessionId);
			Map<String, String> map = SessionDao.getInstance().getSessionMap(sessionKey);
			logger.info("api auth sessionId={} map={}", sessionId, map);
			String userId = map.get(UserKey.userId);
			String deviceId = map.get(UserKey.deviceId);
			if (map != null && StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(deviceId)) {
				command.setSiteUserId(userId);
				command.setDeviceId(deviceId);
				logger.info("api request doApiRequest command={}", command.toString());
				ApiOperateExecutor.getExecutor().execute(command.getService(), command);
				return command.getResponse();
			}
		} catch (Exception e) {
			logger.error("platform api request error.", e);
		}

		return new CommandResponse().setVersion(CommandConst.VERSION).setAction(CommandConst.ACTION_RES)
				.setErrCode(ErrorCode.ERROR);
	}

	public boolean doImRequest(Command command) {
		logger.info("do im request in operation. command={}", command.toString());
		try {
			String action = command.getAction();
			if (!"im.platform.hello".equals(action) || !"im.platform.auth".equals("")) {

			}
			return ImOperateExecutor.getExecutor().execute(command.getAction(), command);
		} catch (Exception e) {
			logger.error("do im request error", e);
		}
		return false;
	}

}