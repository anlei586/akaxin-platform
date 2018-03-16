package com.akaxin.platform.operation.executor;

import com.akaxin.common.command.Command;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.executor.SimpleExecutor;
import com.akaxin.platform.operation.business.handler.ApiPhoneHandler;
import com.akaxin.platform.operation.business.handler.ApiPlatformService;
import com.akaxin.platform.operation.business.handler.ApiPushHandler;
import com.akaxin.platform.operation.business.handler.ApiSettingService;
import com.akaxin.platform.operation.business.handler.ApiTempHandler;
import com.akaxin.platform.operation.business.handler.ApiUserHandler;
import com.akaxin.platform.operation.constant.RequestKeys;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-06 11:40:45
 */
public class ApiOperateExecutor {
	private static AbstracteExecutor<Command> executor = new SimpleExecutor<Command>();

	static {
		executor.addChain(RequestKeys.API_PLATFORM.getName(), new ApiPlatformService());
		executor.addChain(RequestKeys.API_USER.getName(), new ApiUserHandler());
		executor.addChain(RequestKeys.API_PHONE.getName(), new ApiPhoneHandler());
		executor.addChain(RequestKeys.API_TEMP_SPACE.getName(), new ApiTempHandler());
		executor.addChain(RequestKeys.API_PUSH.getName(), new ApiPushHandler());
		executor.addChain(RequestKeys.API_SETTING.getName(), new ApiSettingService());
	}

	private ApiOperateExecutor() {
	}

	public static AbstracteExecutor<Command> getExecutor() {
		return executor;
	}

}
