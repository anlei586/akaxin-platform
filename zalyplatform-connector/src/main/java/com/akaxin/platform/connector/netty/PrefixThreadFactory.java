package com.akaxin.platform.connector.netty;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PrefixThreadFactory implements ThreadFactory {

	private final ThreadGroup threadGroup;
	private final String prefix;
	private final boolean isDaemon;
	private final AtomicInteger sequence = new AtomicInteger(0);

	public PrefixThreadFactory(String namePrefix) {
		this(namePrefix, false);
	}

	public PrefixThreadFactory(String threadNamePrefix, boolean isDaemon) {
		SecurityManager securityManager = System.getSecurityManager();
		this.threadGroup = (securityManager == null) ? Thread.currentThread().getThreadGroup()
				: securityManager.getThreadGroup();
		this.prefix = threadNamePrefix + "-thread-";
		this.isDaemon = isDaemon;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}

	public Thread newThread(Runnable r) {
		final String name = prefix + sequence.getAndIncrement();
		Thread thread = new Thread(threadGroup, r, name, 0);
		thread.setDaemon(isDaemon);
		return thread;
	}

}
