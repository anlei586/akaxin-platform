package com.akaxin.platform.common.test.main;

import com.akaxin.platform.common.utils.ValidatorPattern;

public class ValidatePhoneId {
	public static void main(String[] args) {
		String phoneId = "20210000200";

		System.out.println(ValidatorPattern.isTestPhoneId(phoneId));
	}
}
