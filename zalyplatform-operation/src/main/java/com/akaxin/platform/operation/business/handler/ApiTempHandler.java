package com.akaxin.platform.operation.business.handler;

import java.util.Base64;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.platform.common.constant.CommandConst;
import com.akaxin.platform.operation.business.dao.TempSpaceDao;
import com.akaxin.proto.platform.ApiTempDownloadProto;
import com.akaxin.proto.platform.ApiTempUploadProto;
import com.google.protobuf.ByteString;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ApiTempHandler extends AbstractApiHandler<Command, CommandResponse> {
	private static final Logger logger = LoggerFactory.getLogger(ApiTempHandler.class);

	public CommandResponse upload(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiTempUploadProto.ApiTempUploadRequest request = ApiTempUploadProto.ApiTempUploadRequest
					.parseFrom(command.getParams());
			String key = request.getName();
			if (StringUtils.isEmpty(key)) {
				key = UUID.randomUUID().toString();
			}
			LogUtils.requestDebugLog(logger, command, request.toString() + " key=" + key);

			String value = Base64.getEncoder().encodeToString(request.getContent().toByteArray());
			int expireTime = 10 * 60;// 默认10分钟
			if (TempSpaceDao.getInstance().applyTempSpace(key, value, expireTime)) {
				ApiTempUploadProto.ApiTempUploadResponse response = ApiTempUploadProto.ApiTempUploadResponse
						.newBuilder().setName(key).build();
				commandResponse.setParams(response.toByteArray());
				errCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	public CommandResponse download(Command command) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiTempDownloadProto.ApiTempDownloadRequest request = ApiTempDownloadProto.ApiTempDownloadRequest
					.parseFrom(command.getParams());
			String keyName = request.getName();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (keyName != null && keyName.length() > 5) {
				String contentString = TempSpaceDao.getInstance().getTempValue(keyName);
				if (StringUtils.isNotEmpty(contentString)) {
					byte[] contentBytes = Base64.getDecoder().decode(contentString);
					ApiTempDownloadProto.ApiTempDownloadResponse response = ApiTempDownloadProto.ApiTempDownloadResponse
							.newBuilder().setContent(ByteString.copyFrom(contentBytes)).build();
					commandResponse.setParams(response.toByteArray());
					errCode = ErrorCode2.SUCCESS;
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
