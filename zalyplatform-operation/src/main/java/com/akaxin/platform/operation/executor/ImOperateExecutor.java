package com.akaxin.platform.operation.executor;

import com.akaxin.common.command.Command;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.executor.SimpleExecutor;
import com.akaxin.platform.operation.constant.PlatformAction;
import com.akaxin.platform.operation.imessage.handler.ImAuthHandler;
import com.akaxin.platform.operation.imessage.handler.ImHelloHandler;
import com.akaxin.platform.operation.imessage.handler.ImPingPongHandler;
import com.akaxin.platform.operation.imessage.handler.ImPtcPushHandler;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-06 11:40:45
 */
public class ImOperateExecutor {
	private static AbstracteExecutor<Command> executor = new SimpleExecutor<Command>();

	static {
		// AbstractHandlerChain<Command> imChain = new SimpleHandlerChain<Command>();
		// imChain.addHandler(new ImHelloHandler());
		// imChain.addHandler(new ImAuthHandler());

		executor.addChain(PlatformAction.IM_PLATFORM_HELLO, new ImHelloHandler());
		executor.addChain(PlatformAction.IM_PLATFORM_AUTH, new ImAuthHandler());
		executor.addChain(PlatformAction.IM_CTP_PING, new ImPingPongHandler());
		executor.addChain(PlatformAction.IM_PTC_PUSH, new ImPtcPushHandler());
		// executor.addChain(RequestKeys.IM_ACTION.getName(), imChain);
	}

	private ImOperateExecutor() {
	}

	public static AbstracteExecutor<Command> getExecutor() {
		return executor;
	}

}
