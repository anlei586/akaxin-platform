package com.akaxin.platform.operation.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelManager;
import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.platform.common.constant.CommandConst;
import com.akaxin.platform.common.constant.ErrorCode;
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
		ErrorCode errCode = ErrorCode.ERROR;
		try {
			String action = command.getAction();
			// 过滤一些不需要session验证的action
			if ("api.platform.login".equals(action) || "api.platform.registerByPhone".equals(action)
					|| "api.push.notification".equals(action) || "api.phone.login".equals(action)
					|| "api.phone.verifyCode".equals(action) || "api.temp.download".equals(action)
					|| "api.temp.upload".equals(action) || "api.phone.confirmToken".endsWith(action)) {
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
						command.setGlobalUserId(globalUserId);
						command.setDeviceId(deviceId);
						logger.info("api request doApiRequest command={}", command.toString());
						response = ApiOperateExecutor.getExecutor().execute(command.getService(), command);
					} else {
						errCode = ErrorCode.ERROR_SESSION;
					}
				} else {
					errCode = ErrorCode.ERROR_SESSION;
				}
			}
		} catch (Exception e) {
			errCode = ErrorCode.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}

		if (response == null) {
			response = new CommandResponse().setErrCode(errCode);
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
	public CommandResponse doImRequest(Command command) {
		CommandResponse response = customResponse();
		ErrorCode errCode = ErrorCode.ERROR;
		try {
			String action = command.getAction();
			String globalUserId = command.getGlobalUserId();

			if (PlatformAction.IM_PLATFORM_HELLO.equals(action) || PlatformAction.IM_PLATFORM_AUTH.equals(action)) {
				// <im.platform.hello> return true by default
				boolean result = ImOperateExecutor.getExecutor().execute(command.getAction(), command);
				errCode = getErrorCode2(result, ErrorCode.ERROR_SESSION);
			} else {
				ChannelSession channelSession = command.getChannelSession();
				String deviceId = channelSession.getDeviceId();
				ChannelSession acsession = ChannelManager.getChannelSession(deviceId);

				if (acsession != null && globalUserId != null && globalUserId.equals(acsession.getUserId())) {
					boolean result = ImOperateExecutor.getExecutor().execute(command.getAction(), command);
					errCode = getErrorCode2(result, ErrorCode.ERROR);
				} else {
					errCode = ErrorCode.ERROR_SESSION;
				}
			}
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, e);
		}
		return response.setErrCode(errCode);
	}

	// defined by user
	protected CommandResponse customResponse() {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		return commandResponse;
	}

	private ErrorCode getErrorCode2(boolean result, ErrorCode errCode) {
		return result ? ErrorCode.SUCCESS : errCode;
	}

}