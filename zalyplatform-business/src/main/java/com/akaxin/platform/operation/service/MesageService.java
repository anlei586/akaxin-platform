package com.akaxin.platform.operation.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.platform.operation.api.IMessage;
import com.akaxin.platform.operation.business.dao.SessionDao;
import com.akaxin.platform.operation.constant.PlatformAction;
import com.akaxin.platform.operation.executor.ApiOperateExecutor;
import com.akaxin.platform.operation.executor.ImOperateExecutor;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.constant.UserKey;
import com.akaxin.proto.core.CoreProto;

public class MesageService implements IMessage {
	private static final Logger logger = LoggerFactory.getLogger(MesageService.class);

	public CommandResponse doApiRequest(Command command) {
		logger.info("doplatform api request command={}", command.toString());
		try {
			String action = command.getAction();

			if ("api.platform.login".equals(action) || "api.push.notification".equals(action)
					|| "api.phone.login".equals(action) || "api.phone.verifyCode".equals(action)
					|| "api.temp.download".equals(action) || "api.temp.upload".equals(action)) {
				ApiOperateExecutor.getExecutor().execute(command.getService(), command);
				return command.getResponse();
			} else {
				Map<Integer, String> header = command.getHeader();
				String sessionId = header.get(CoreProto.HeaderKey.CLIENT_SOCKET_SITE_SESSION_ID_VALUE);
				String sessionKey = RedisKeyUtils.getSessionKey(sessionId);
				logger.info("api auth sessionKey={}", sessionKey);
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
			}
		} catch (Exception e) {
			logger.error("platform api request error.", e);
		}

		return new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION).setAction(CommandConst.ACTION_RES)
				.setErrCode(ErrorCode.ERROR);
	}

	/**
	 * <pre>
	 * 	return false，平台断开长链接 
	 * 	return true，操作完成，保持长链接
	 * </pre>
	 */
	public boolean doImRequest(Command command) {
		try {
			String action = command.getAction();
			if (PlatformAction.IM_PLATFORM_HELLO.equals(action) || PlatformAction.IM_PLATFORM_AUTH.equals(action)) {
				return ImOperateExecutor.getExecutor().execute(command.getAction(), command);
			} else {
				ChannelSession channelSession = command.getChannelSession();
				String deviceId = channelSession.getDeviceId();
				ChannelSession acsession = ChannelManager.getChannelSession(deviceId);
				logger.info("do platform auth command={} ", command.toString());
				if (acsession != null) {
					logger.info("do platform auth userId={} ", acsession.getUserId());
				}
				if (acsession != null && acsession.getUserId() != null
						&& acsession.getUserId().equals(command.getSiteUserId())) {
					return ImOperateExecutor.getExecutor().execute(command.getAction(), command);
				}
			}
		} catch (Exception e) {
			logger.error("do im request error", e);
		}
		return false;
	}

}