package com.akaxin.platform.operation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode;
import com.akaxin.platform.operation.api.IMessage;
import com.akaxin.platform.operation.executor.ApiOperateExecutor;
import com.akaxin.platform.operation.executor.ImOperateExecutor;

public class MesageService implements IMessage {
	private static final Logger logger = LoggerFactory.getLogger(MesageService.class);

	public CommandResponse doApiRequest(Command command) {
		logger.info("platform api request command={}", command.toString());
		try {
			ApiOperateExecutor.getExecutor().execute(command.getService(), command);
			return command.getResponse();
		} catch (Exception e) {
			logger.error("platform api request error.", e);
		}

		return new CommandResponse().setVersion(CommandConst.VERSION).setAction(CommandConst.ACTION_RES)
				.setErrCode(ErrorCode.ERROR);
	}
	
	public boolean doImRequest(Command command) {
		logger.info("do im request in operation. command={}", command.toString());
		try {
			return ImOperateExecutor.getExecutor().execute(command.getAction(), command);
		} catch (Exception e) {
			logger.error("do im request error", e);
		}
		return false;
	}

}