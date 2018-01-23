package com.akaxin.platform.test;

public class TestRandomVC {
	public static void main(String[] args) {
		while(true) {
			String phoneVC = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
			System.out.println(phoneVC);
		}
	}
}
