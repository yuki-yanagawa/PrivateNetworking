package com.yana.privateNetTest.LocalMachine.communicate.cycript;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.yana.privateNetTest.LocalMachine.communicate.key.KeyStoredManager;

public class CommunicateCycript {
	public static byte[] cycriptMessage(byte[] message) {
		SecretKeySpec secretKeySepc = KeyStoredManager.getSecretKeySpec();
		Cipher commonCipher = KeyStoredManager.getCommonCipher();
		IvParameterSpec vectorSpecParam = KeyStoredManager.getVecrotSpecParam();
		try {
			commonCipher.init(Cipher.ENCRYPT_MODE, secretKeySepc, vectorSpecParam);
			return commonCipher.doFinal(message);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
}
