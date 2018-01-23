package com.akaxin.platform.operation.business.handler;

import java.util.Base64;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.platform.operation.business.dao.TempSpaceDao;
import com.akaxin.proto.platform.ApiTempDownloadProto;
import com.akaxin.proto.platform.ApiTempUploadProto;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.10.17
 *
 */
public class ApiTempHandler extends AbstractApiHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(ApiTempHandler.class);

	public boolean upload(Command command) {
		logger.info("-----api.tmp.upload-----");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		try {
			ApiTempUploadProto.ApiTempUploadRequest request = ApiTempUploadProto.ApiTempUploadRequest
					.parseFrom(command.getParams());
			String key = request.getName();
			if (StringUtils.isEmpty(key)) {
				key = UUID.randomUUID().toString();
			}
			String value = Base64.getEncoder().encodeToString(request.getContent().toByteArray());
			int expireTime = 10 * 60;// 默认10分钟
			if (TempSpaceDao.getInstance().applyTempSpace(key, value, expireTime)) {
				ApiTempUploadProto.ApiTempUploadResponse response = ApiTempUploadProto.ApiTempUploadResponse
						.newBuilder().setName(key).build();
				commandResponse.setParams(response.toByteArray());
				errorCode = ErrorCode.SUCCESS;
			}
		} catch (InvalidProtocolBufferException e) {
			commandResponse.setErrInfo("apply temp space error");
			logger.error("upload temp space error.", e);
		}
		command.setResponse(commandResponse.setErrCode(errorCode));
		return false;
	}

	public boolean download(Command command) {
		logger.info("------api.tmp.download-----");
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.ACTION_RES);
		String errorCode = ErrorCode.ERROR;
		try {
			ApiTempDownloadProto.ApiTempDownloadRequest request = ApiTempDownloadProto.ApiTempDownloadRequest
					.parseFrom(command.getParams());
			String keyName = request.getName();

			if (keyName != null && keyName.length() > 5) {
				String contentString = TempSpaceDao.getInstance().getTempValue(keyName);
				if (StringUtils.isNotEmpty(contentString)) {
					byte[] contentBytes = Base64.getDecoder().decode(contentString);
					ApiTempDownloadProto.ApiTempDownloadResponse response = ApiTempDownloadProto.ApiTempDownloadResponse
							.newBuilder().setContent(ByteString.copyFrom(contentBytes)).build();
					commandResponse.setParams(response.toByteArray());
					errorCode = ErrorCode.SUCCESS;
				}
			}

		} catch (InvalidProtocolBufferException e) {
			commandResponse.setErrInfo("download from temp space error");
			logger.error("download from temp space error.", e);
		}
		command.setResponse(commandResponse.setErrCode(errorCode));
		return false;
	}

}
