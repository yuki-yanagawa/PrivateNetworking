package com.yana.privateNetTest.LocalMachine.exchange.fileStoreKey;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Base64.Encoder;

public class CreateFileStoreKeyFromName {
	private static Charset defCharSet = StandardCharsets.UTF_8;
	private MessageDigest digest;
	private Encoder base64Encoder;
	private CreateFileStoreKeyFromName() {
	}

	public static CreateFileStoreKeyFromName newInstance() {
		CreateFileStoreKeyFromName createFileStoreKeyFromName = new CreateFileStoreKeyFromName();
		try {
			createFileStoreKeyFromName.digest = MessageDigest.getInstance("SHA-256");
			createFileStoreKeyFromName.base64Encoder = Base64.getEncoder();
		} catch(Exception e) {
			return null;
		}
		return createFileStoreKeyFromName;
	}

	public String createKey(String fileName) {
		return base64Encoder.encodeToString(digest.digest(fileName.getBytes(defCharSet)));
	}
}
