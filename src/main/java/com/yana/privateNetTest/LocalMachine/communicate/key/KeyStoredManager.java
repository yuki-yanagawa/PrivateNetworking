package com.yana.privateNetTest.LocalMachine.communicate.key;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.yana.privateNetTest.LocalMachine.prop.PropertiesReader;

public class KeyStoredManager {
	private static KeyStoredManager sigltone = new KeyStoredManager();
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private SecretKeySpec secretKeySpec;
	private IvParameterSpec vectorParamSpec;
	private String cipherAlgoritm;
	private Cipher commonCipher;

	private static KeyStoredManager getInstance() {
		return sigltone;
	}

	private void registPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	private void registPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	private void registSecretKeySpec(SecretKeySpec secretKeySpec) {
		this.secretKeySpec = secretKeySpec;
	}

	private void _registVectorParamSpec(IvParameterSpec vectorParamSpec) {
		this.vectorParamSpec = vectorParamSpec;
	}

	public void _registCipherAlgoritm(String cipherAlgoritm) {
		this.cipherAlgoritm = cipherAlgoritm;
		try {
			this.commonCipher = Cipher.getInstance(this.cipherAlgoritm);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized boolean init() {
		String algorithm = PropertiesReader.getSettingValue("LocalKeyAlgorithm");
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
			KeyPair keyPair = keyPairGenerator.genKeyPair();
			KeyStoredManager tmp = getInstance();
			tmp.registPrivateKey(keyPair.getPrivate());
			tmp.registPublicKey(keyPair.getPublic());
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static PublicKey getPublicKey() {
		return getInstance().publicKey;
	}

	public static PrivateKey getPrivateKey() {
		return getInstance().privateKey;
	}

	public static void registCommonEncryptKey(SecretKeySpec secretKeySpec) {
		getInstance().registSecretKeySpec(secretKeySpec);
	}

	public static SecretKeySpec getSecretKeySpec() {
		return getInstance().secretKeySpec;
	}

	public static IvParameterSpec getVecrotSpecParam() {
		return getInstance().vectorParamSpec;
	}

	public static Cipher getCommonCipher() {
		return getInstance().commonCipher;
	}

	public static void registVectorParamSpec(IvParameterSpec vectorParamSpec) {
		getInstance()._registVectorParamSpec(vectorParamSpec);
	}

	public static void registCipherAlgorithm(String cipherAlgorithm) {
		getInstance()._registCipherAlgoritm(cipherAlgorithm);
	}
}
