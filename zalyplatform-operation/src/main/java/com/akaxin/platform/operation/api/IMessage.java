package com.akaxin.platform.operation.api;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;

public interface IMessage {

	public CommandResponse doApiRequest(Command command);

	public CommandResponse doImRequest(Command command);

}