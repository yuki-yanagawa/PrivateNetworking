package com.yana.privateNetTest.CentralRouter.key;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.yana.privateNetTest.CentralRouter.prop.RouterProperties;
import com.yana.privateNetTest.Common.charDef.CharCodeDefnition;

public class RouterStoredKey {
	private static final Path CERT_FILE_PATH = Paths.get("centralrouter/keydir/jiko.crt");
	private static final Path PRIV_PATH = Paths.get("centralrouter/keydir/jikoPriv.pkcs8");
	private static RouterStoredKey routerStoredKey = new RouterStoredKey();
	private PrivateKey privateKey;
	private SecretKey secretKey;
	private String cipherAlgorithm;
	private String vectorKeyWord;
	private IvParameterSpec vectorParamSpec;
	private Cipher commonCipher;
	private RouterStoredKey() {
	}

	private void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	private void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	private void setCipherAlgorithm(String cipherAlgorithm) {
		this.cipherAlgorithm = cipherAlgorithm;
	}

	private void setVectorKeyWord(String vectorKeyWord) {
		this.vectorKeyWord = vectorKeyWord;
	}

	private void setVectorParamSpec(IvParameterSpec vectorParamSpec) {
		this.vectorParamSpec = vectorParamSpec;
	}

	private void setCommonCipher(Cipher commonCipher) {
		this.commonCipher = commonCipher;
	}

	private static RouterStoredKey getInstance() {
		return routerStoredKey;
	}

	public static synchronized void initStoredKey() throws Exception {
		//SecretKey
		String algorithm = RouterProperties.getSettingValue("commonKeyAlgorithm");
		KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
		getInstance().setSecretKey(keyGenerator.generateKey());

		//CipherAlgorithm
		String cipherAlgorithm = RouterProperties.getSettingValue("cipherAlgorithm");
		getInstance().setCipherAlgorithm(cipherAlgorithm);

		//CommonCipher
		Cipher cipher = Cipher.getInstance(getCipherAlogrithm());
		getInstance().setCommonCipher(cipher);

		//vector random
		Random rd = new Random();
		char[] randomCode = "abcdefghijklmnopqrstuvwzyzABCDEFGHIJKLMNOPQRSTUZ0123456789".toCharArray();
		char[] createVectorCharArray = new char[16];
		for(int i = 0; i < 16; i++) {
			createVectorCharArray[i] = randomCode[rd.nextInt(randomCode.length)];
		}
		getInstance().setVectorKeyWord(new String(createVectorCharArray));

		getInstance().setVectorParamSpec(new IvParameterSpec(getVectorKeyWord().getBytes(CharCodeDefnition.CIPHER_VECTOR)));

		//PrivateKey
		int size = (int)Files.size(PRIV_PATH);
		byte[] encodedKeyBytes = new byte[size];
		try(FileInputStream fis = new FileInputStream(PRIV_PATH.toFile())) {
			fis.read(encodedKeyBytes);
		}
		String privAlgorithm = RouterProperties.getSettingValue("privAlgorithm");
		PKCS8EncodedKeySpec psKeySpec = new PKCS8EncodedKeySpec(encodedKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(privAlgorithm);
		getInstance().setPrivateKey(keyFactory.generatePrivate(psKeySpec));
	}

	public static PrivateKey getPrivateKey() {
		return getInstance().privateKey;
	}

	public static SecretKey getSecretKey() {
		return getInstance().secretKey;
	}

	public static String getCipherAlogrithm() {
		return getInstance().cipherAlgorithm;
	}

	public static String getVectorKeyWord() {
		return getInstance().vectorKeyWord;
	}

	public static IvParameterSpec getVectorParam() {
		return getInstance().vectorParamSpec;
	}

	public static Cipher getCommonCipher() {
		return getInstance().commonCipher;
	}
}
