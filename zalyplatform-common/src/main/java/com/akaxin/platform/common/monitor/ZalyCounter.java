package com.akaxin.platform.common.monitor;

import java.util.concurrent.atomic.AtomicLong;

public class ZalyCounter {
	private final AtomicLong count;

	public ZalyCounter() {
		count = new AtomicLong(0);
	}

	public ZalyCounter(int num) {
		this.count = new AtomicLong(num);
	}

	// add
	public long inc() {
		return count.incrementAndGet();
	}

	// add
	public long inc(long num) {
		return count.addAndGet(num);
	}

	// dec
	public long dec() {
		return count.decrementAndGet();
	}

	// dec
	public long dec(long num) {
		return count.getAndAdd(-num);
	}

	// get
	public long getCount() {
		return count.get();
	}

	// get
	public String getCountString() {
		return String.valueOf(count.get());
	}

	public void clear() {
		count.set(0);
	}
}
