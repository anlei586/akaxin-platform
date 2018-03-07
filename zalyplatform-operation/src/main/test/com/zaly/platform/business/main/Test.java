package com.zaly.platform.business.main;

import com.zaly.common.command.Command;
import com.zaly.common.command.CommandResponse;
import com.zaly.common.executor.AbstracteExecutor;
import com.zaly.platform.business.impl.ExecutorImpl;
import com.zaly.platform.business.service.MesageService;

public class Test {
	public static void main(String[] args) {
		Command command = new Command();
//		command.setServiceMethod("ApiUploadUserInfo.uploadUserInfo");
//		command.setParams("hello");

		System.out.println(command.toString());

		AbstracteExecutor<Command> e = new ExecutorImpl().loadExecutor().getExecutor();
		// invoke interface of business
		CommandResponse rs = (CommandResponse) new MesageService().executor(e, command);

	}
}
