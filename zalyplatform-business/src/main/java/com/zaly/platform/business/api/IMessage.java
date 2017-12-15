package com.zaly.platform.business.api;

import com.zaly.common.command.Command;
import com.zaly.common.executor.AbstracteExecutor;

public interface IMessage {

	public Object executor(AbstracteExecutor<Command> executor, Command cmd);
}
