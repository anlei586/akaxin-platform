package com.akaxin.platform.business.impl;

import com.akaxin.common.command.Command;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.executor.SimpleExecutor;
import com.akaxin.platform.business.constant.ServiceKeys;
import com.akaxin.platform.business.handler.ApiPhoneHandler;
import com.akaxin.platform.business.handler.ApiTempHandler;
import com.akaxin.platform.business.handler.ApiUserHandler;

public class ExecutorImpl {
	private AbstracteExecutor<Command> executor = new SimpleExecutor<Command>();

	public ExecutorImpl loadExecutor() {
		executor.addChain(ServiceKeys.API_USER.getName(), new ApiUserHandler());
		executor.addChain(ServiceKeys.API_PHONE.getName(), new ApiPhoneHandler());
		executor.addChain(ServiceKeys.API_TEMP_SPACE.getName(), new ApiTempHandler());
		return this;
	}

	public AbstracteExecutor<Command> getExecutor() {
		return this.executor;
	}

}
