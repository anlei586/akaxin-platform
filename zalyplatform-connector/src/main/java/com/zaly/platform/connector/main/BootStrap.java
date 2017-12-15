package com.zaly.platform.connector.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaly.common.command.Command;
import com.zaly.common.executor.AbstracteExecutor;
import com.zaly.platform.business.impl.ExecutorImpl;
import com.zaly.platform.connector.netty.NettyServer;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.09.28
 *
 */
public class BootStrap {
	private static final Logger logger = LoggerFactory.getLogger(BootStrap.class);

	public static void main(String[] args) {
		int port = 8000;
		if (args != null && args.length == 1) {
			port = Integer.valueOf(args[0]);
		}
		logger.info("Start Platform Server port:{}", port);

		new NettyServer() {

			@Override
			public AbstracteExecutor<Command> loadExecutor() {
				return new ExecutorImpl().loadExecutor().getExecutor();
			}

		}.start(ServerAddress.getLocalAddress(), port);

	}

}
