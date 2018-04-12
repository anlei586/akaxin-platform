package com.akaxin.platform.common.test.main;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestInetAddress {
	public static void main(String args[]) throws UnknownHostException {
		String address = "demo.akaxin.com";
		InetAddress netAdd =InetAddress.getByName(address);
		System.out.println(netAdd.getHostName());
		System.out.println(netAdd.getHostAddress());
		System.out.println();
	}
}
