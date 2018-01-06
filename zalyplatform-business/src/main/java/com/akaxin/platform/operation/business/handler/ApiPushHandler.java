package com.akaxin.platform.operation.business.handler;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.08 15:11:48
 * @param <Command>
 */
public class ApiPushHandler<Command> extends AbstractApiHandler<Command> {

	public boolean handle(Command cmd) {
		// TODO Auto-generated method stub
		System.out.println("send push to user");
		return false;
	}

}
