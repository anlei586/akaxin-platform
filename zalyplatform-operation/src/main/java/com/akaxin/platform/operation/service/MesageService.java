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
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.platform.operation.api.IMessage;
import com.akaxin.platform.operation.business.dao.SessionDao;
import com.akaxin.platform.operation.constant.PlatformAction;
import com.akaxin.platform.operation.executor.ApiOperateExecutor;
import com.akaxin.platform.operation.executor.ImOperateExecutor;
import com.akaxin.platform.operation.utils.RedisKeyUtils;
import com.akaxin.platform.storage.constant.UserKey;
import com.akaxin.proto.core.CoreProto;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-31 15:25:03
 */
public class MesageService implements IMessage {
	private static final Logger logger = LoggerFactory.getLogger(MesageService.class);

	/**
	 * 业务层处理API请求
	 */
	public CommandResponse doApiRequest(Command command) {
		CommandResponse response = null;
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			String action = command.getAction();
			// 过滤一些不需要session验证的action
			if ("api.platform.login".equals(action) || "api.push.notification".equals(action)
					|| "api.phone.login".equals(action) || "api.phone.verifyCode".equals(action)
					|| "api.temp.download".equals(action) || "api.temp.upload".equals(action)
					|| "api.phone.confirmToken".endsWith(action)) {
				response = ApiOperateExecutor.getExecutor().execute(command.getService(), command);
			} else {
				Map<Integer, String> header = command.getHeader();
				String sessionId = header.get(CoreProto.HeaderKey.CLIENT_SOCKET_SITE_SESSION_ID_VALUE);
				String sessionKey = RedisKeyUtils.getSessionKey(sessionId);
				logger.info("api auth sessionKey={}", sessionKey);

				if (StringUtils.isNotEmpty(sessionId)) {
					Map<String, String> map = SessionDao.getInstance().getSessionMap(sessionKey);
					logger.info("api auth sessionId={} map={}", sessionId, map);
					String globalUserId = map.get(UserKey.userId);
					String deviceId = map.get(UserKey.deviceId);
					if (map != null && StringUtils.isNotBlank(globalUserId) && StringUtils.isNotBlank(deviceId)) {
						command.setSiteUserId(globalUserId);
						command.setDeviceId(deviceId);
						logger.info("api request doApiRequest command={}", command.toString());
						response = ApiOperateExecutor.getExecutor().execute(command.getService(), command);
					} else {
						errCode = ErrorCode2.ERROR_SESSION;
					}
				} else {
					errCode = ErrorCode2.ERROR_SESSION;
				}
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}

		if (response == null) {
			response = new CommandResponse().setErrCode2(errCode);
		}
		response.setVersion(CommandConst.PROTOCOL_VERSION).setAction(CommandConst.ACTION_RES);

		return response;
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
				if (acsession != null && acsession.getUserId() != null
						&& acsession.getUserId().equals(command.getSiteUserId())) {
					return ImOperateExecutor.getExecutor().execute(command.getAction(), command);
				} else {
					// errCode = ErrorCode2.ERROR_SESSION;
					logger.info("do im platform auth fail command={} ", command.toString());
				}
			}
		} catch (Exception e) {
			logger.error("do im request error", e);
		}
		return false;
	}

}