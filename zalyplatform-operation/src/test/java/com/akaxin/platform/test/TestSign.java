package com.akaxin.platform.test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import com.akaxin.platform.common.crypto.RSACrypto;

public class TestSign {
	public static void main(String[] args) throws Exception {
		String userIdPubk = null;
		PrivateKey privateKey = null;
		PublicKey userPubKey = RSACrypto.getRSAPubKeyFromPem(userIdPubk);// 个人身份公钥，解密Sign签名，解密Key
		Signature userSign = Signature.getInstance("SHA512withRSA");
		userSign.initSign(privateKey);//用户身份密码签名
		userSign.update(userIdPubk.getBytes());// 签名个人/设备pubk
		String signBase64 = Base64.getEncoder().encodeToString(userSign.sign());
	}
}
