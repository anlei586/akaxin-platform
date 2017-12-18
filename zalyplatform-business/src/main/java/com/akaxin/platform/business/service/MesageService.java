package com.akaxin.platform.business.service;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.platform.business.api.IMessage;

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
