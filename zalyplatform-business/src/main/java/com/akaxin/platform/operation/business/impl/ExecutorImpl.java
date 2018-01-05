package com.akaxin.platform.operation.business.impl;

import com.akaxin.common.command.Command;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.executor.SimpleExecutor;
import com.akaxin.platform.operation.business.constant.ServiceKeys;
import com.akaxin.platform.operation.business.handler.ApiPhoneHandler;
import com.akaxin.platform.operation.business.handler.ApiTempHandler;
import com.akaxin.platform.operation.business.handler.ApiUserHandler;

public class ExecutorImpl {
	private static AbstracteExecutor<Command> executor = new SimpleExecutor<Command>();

	static {
		executor.addChain(ServiceKeys.API_USER.getName(), new ApiUserHandler());
		executor.addChain(ServiceKeys.API_PHONE.getName(), new ApiPhoneHandler());
		executor.addChain(ServiceKeys.API_TEMP_SPACE.getName(), new ApiTempHandler());
	}

	private ExecutorImpl() {
	}

	public static AbstracteExecutor<Command> getExecutor() {
		return executor;
	}

}
