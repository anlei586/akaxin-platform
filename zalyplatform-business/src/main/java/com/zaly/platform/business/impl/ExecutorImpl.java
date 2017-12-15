package com.zaly.platform.business.impl;

import com.zaly.common.command.Command;
import com.zaly.common.executor.AbstracteExecutor;
import com.zaly.common.executor.SimpleExecutor;
import com.zaly.platform.business.constant.ServiceKeys;
import com.zaly.platform.business.handler.ApiPhoneHandler;
import com.zaly.platform.business.handler.ApiTempHandler;
import com.zaly.platform.business.handler.ApiUserHandler;

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
