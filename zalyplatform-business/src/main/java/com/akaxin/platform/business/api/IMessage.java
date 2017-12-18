package com.akaxin.platform.business.api;

import com.akaxin.common.command.Command;
import com.akaxin.common.executor.AbstracteExecutor;

public interface IMessage {

	public Object executor(AbstracteExecutor<Command> executor, Command cmd);
}
