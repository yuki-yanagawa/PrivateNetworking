package com.yana.privateNetTest.LocalMachine.handshake;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.yana.privateNetTest.Common.charDef.CharCodeDefnition;
import com.yana.privateNetTest.Common.message.MessageDefinition;
import com.yana.privateNetTest.LocalMachine.prop.PropertiesReader;

class GetServerPublicKeyFromAckJoinHello {
	static Optional<PublicKey> execute(byte[] ackJoinHelloBytes) {
		String rawMessage = new String(ackJoinHelloBytes, CharCodeDefnition.MESSAGE_CHARSET);
		String[] strline = rawMessage.split(CharCodeDefnition.MESSAGE_LINE_SEPARATOR);
		if(!MessageDefinition.ACK_JOIN_HELLO.equals(strline[0].trim())) {
			return Optional.empty();
		}

		Map<String, Object> settingMap = new HashMap<>();
		for(int i = 1; i < strline.length;i ++) {
			String[] settingData = strline[i].split(MessageDefinition.SETTING_SEPARATOR);
			settingMap.put(settingData[0].trim(), settingData[1].trim());
		}

		if(!settingMap.containsKey(MessageDefinition.BODY_LENGTH) || !settingMap.containsKey(MessageDefinition.BODY)) {
			return Optional.empty();
		}

		String base64EncodedCert = (String)settingMap.get(MessageDefinition.BODY);
		byte[] certBytes = Base64.getDecoder().decode(base64EncodedCert);
		try(ByteArrayInputStream bis = new ByteArrayInputStream(certBytes)) {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "SUN");
			for(Certificate cert : certificateFactory.generateCertificates(bis)) {
				if(!(cert instanceof X509Certificate)) {
					continue;
				}
				X509Certificate x509Cert = (X509Certificate)cert;
				if(checkSubjectCommonName(x509Cert.getSubjectX500Principal().getName())) {
					PublicKey pubKey = x509Cert.getPublicKey();
					//expect self cert
					x509Cert.verify(pubKey);
					return Optional.ofNullable(pubKey);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} catch(NoSuchProviderException e) {
			e.printStackTrace();
		} catch(CertificateException e) {
			e.printStackTrace();
		} catch(SignatureException e) {
			e.printStackTrace();
		} catch(InvalidKeyException e) {
			e.printStackTrace();
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

	private static boolean checkSubjectCommonName(String subjectDN) {
		String[] l = subjectDN.split(",");
		String checkCN = PropertiesReader.getSettingValue("centralRouterSubjectCommonName");
		for(String s : l) {
			if("CN".equals(s.split("=")[0].trim())) {
				if(checkCN.equals(s.split("=")[1].trim())) {
					return true;
				}
			}
		}
		return false;
	}
}
