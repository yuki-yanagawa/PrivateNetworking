package com.yana.privateNetTest.CentralRouter.message.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.yana.privateNetTest.CentralRouter.key.RouterStoredKey;
import com.yana.privateNetTest.Common.message.send.SendMessageCreator;

public class CommonCommunicateEncrypt {
	public static byte[] cycriptMessage(byte[] message) {
		SecretKey secKey = RouterStoredKey.getSecretKey();
		IvParameterSpec ivParameterSpec = RouterStoredKey.getVectorParam();
		Cipher cipher = RouterStoredKey.getCommonCipher();
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secKey, ivParameterSpec);
			byte[] encriptedMess = cipher.doFinal(message);
			return SendMessageCreator.commonPrive(encriptedMess);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
}
