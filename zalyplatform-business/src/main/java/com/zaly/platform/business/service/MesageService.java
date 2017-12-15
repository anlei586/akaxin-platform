package com.zaly.platform.business.service;

import com.zaly.common.command.Command;
import com.zaly.common.command.CommandResponse;
import com.zaly.common.constant.CommandConst;
import com.zaly.common.executor.AbstracteExecutor;
import com.zaly.platform.business.api.IMessage;

public class MesageService implements IMessage {

	public CommandResponse executor(AbstracteExecutor<Command> executor, Command command) {
		try {
			System.out.println("MesageService = " + command.toString());

			executor.execute(command.getService(), command);

			return command.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new CommandResponse().setVersion(CommandConst.VERSION).setAction(CommandConst.ACTION_RES)
				.setErrCode(-100 + "");

	}

}
