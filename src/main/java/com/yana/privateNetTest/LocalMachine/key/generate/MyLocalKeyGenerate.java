package com.yana.privateNetTest.LocalMachine.key.generate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Optional;

public class MyLocalKeyGenerate {
	private static final Path DIR_PATH = Paths.get("localmachine/keydir");
	private static final String PRIV_KEY_NAME = "id_local";
	private static final String PUB_KEY_NAME = "id_local.pub";
	private static final String KEY_ALGO = "RSA";

	public boolean exitCheck() {
		if(!DIR_PATH.toFile().exists()) {
			DIR_PATH.toFile().mkdir();
			return false;
		}
		boolean exitPriv = false;
		boolean exitPub = false;
		for(File f : DIR_PATH.toFile().listFiles()) {
			if(PRIV_KEY_NAME.equals(f.getName())) {
				exitPriv = true;
			}
			if(PUB_KEY_NAME.equals(f.getName())) {
				exitPub = true;
			}
		}
		return exitPriv && exitPub;
	}

	public boolean generateKey() {
		Encoder encoder = Base64.getEncoder();
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGO);
			KeyPair keyPair = keyPairGenerator.genKeyPair();
			PrivateKey priv = keyPair.getPrivate();
			PublicKey pub = keyPair.getPublic();
			String dirPath = DIR_PATH.toFile().getAbsolutePath() + File.separator;
			try(FileOutputStream fos = new FileOutputStream(dirPath + PRIV_KEY_NAME)) {
				fos.write(encoder.encodeToString(priv.getEncoded()).getBytes());
				fos.flush();
			}
			try(FileOutputStream fos = new FileOutputStream(dirPath + PUB_KEY_NAME)) {
				fos.write(encoder.encodeToString(pub.getEncoded()).getBytes());
				fos.flush();
			}
			try {
				PublicKey tmpPub = readPublicKey().get();
				if(tmpPub == pub) {
					System.out.println("first check");
				}
				if(tmpPub.equals(pub)) {
					System.out.println("second check");
				}
				PrivateKey tmpPriv = readPrivateKey().get();
				if(tmpPriv == priv) {
					System.out.println("p first check");
				}
				if(tmpPriv.equals(priv)) {
					System.out.println("p second check");
				}
			} catch(Exception e) {
				
			}
			return true;
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Optional<PublicKey> readPublicKey() throws Exception {
		String pubPathStr = DIR_PATH.toFile().getAbsolutePath() + File.separator + PUB_KEY_NAME;
		Path pubPath = Paths.get(pubPathStr);
		int size = (int)Files.size(pubPath);
		byte[] readFileBuf = new byte[size];
		try(FileInputStream fis = new FileInputStream(pubPath.toFile())) {
			fis.read(readFileBuf);
		}
		byte[] pubKeyBytes = Base64.getDecoder().decode(readFileBuf);
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pubKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGO);
		PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
		return Optional.ofNullable(publicKey);
	}

	public Optional<PrivateKey> readPrivateKey() throws Exception {
		String privPathStr = DIR_PATH.toFile().getAbsolutePath() + File.separator + PRIV_KEY_NAME;
		Path privPath = Paths.get(privPathStr);
		int size = (int)Files.size(privPath);
		byte[] readFileBuf = new byte[size];
		try(FileInputStream fis = new FileInputStream(privPath.toFile())) {
			fis.read(readFileBuf);
		}
		byte[] privKeyBytes = Base64.getDecoder().decode(readFileBuf);
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGO);
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		return Optional.ofNullable(privateKey);
	}

	//read debug
	public static void main(String[] args) {
//		for(Provider p : Security.getProviders()) {
//			for(Service s : p.getServices()) {
//				System.out.println(p.getName() + " " + s.getType() + " " + s.getAlgorithm());
//			}
//		}
	}
}
