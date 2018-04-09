package com.akaxin.platform.operation.business.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.common.utils.ServerAddress;
import com.akaxin.platform.operation.business.dao.MuteSettingDao;
import com.akaxin.proto.platform.ApiSettingSiteMuteProto;
import com.akaxin.proto.platform.ApiSettingUpdateSiteMuteProto;

/**
 * <pre>
 * 		用户在平台相关设置信息
 * 			1.站点静音状态
 * 			2.好友静音状态
 * 			3.群组静音状态
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-03-16 13:10:01
 */
public class ApiSettingService extends AbstractApiHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ApiSettingService.class);

	/**
	 * 获取用户对站点的静音状态
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse siteMute(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiSettingSiteMuteProto.ApiSettingSiteMuteRequest request = ApiSettingSiteMuteProto.ApiSettingSiteMuteRequest
					.parseFrom(command.getParams());
			String globalUserId = command.getGlobalUserId();
			String host = request.getSiteHost();
			int port = request.getSitePort();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNoneBlank(globalUserId, host) && port > 0) {
				ServerAddress siteAddress = new ServerAddress(host, port);
				int muteResult = MuteSettingDao.getInstance().getSiteMute(globalUserId, siteAddress);
				if (muteResult >= 0) {
					boolean mute = (muteResult == 1 ? true : false);

					ApiSettingSiteMuteProto.ApiSettingSiteMuteResponse response = ApiSettingSiteMuteProto.ApiSettingSiteMuteResponse
							.newBuilder().setMute(mute).build();
					commandResponse.setParams(response.toByteArray());
					errCode = ErrorCode2.SUCCESS;
				} else {
					errCode = ErrorCode2.ERROR_DATABASE_EXECUTE;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	public CommandResponse updateSiteMute(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiSettingUpdateSiteMuteProto.ApiSettingUpdateSiteMuteRequest request = ApiSettingUpdateSiteMuteProto.ApiSettingUpdateSiteMuteRequest
					.parseFrom(command.getParams());
			String globalUserId = command.getGlobalUserId();
			String host = request.getSiteHost();
			int port = request.getSitePort();
			boolean mute = request.getMute();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNoneEmpty(globalUserId, host) && port > 0) {
				ServerAddress siteAddress = new ServerAddress(host, port);
				if (MuteSettingDao.getInstance().updateSiteMute(globalUserId, siteAddress, mute)) {
					errCode = ErrorCode2.SUCCESS;
				} else {
					errCode = ErrorCode2.ERROR_DATABASE_EXECUTE;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

}