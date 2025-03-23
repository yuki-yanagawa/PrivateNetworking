package com.yana.privateNetTest.LocalMachine.communicate.key;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Optional;

import com.yana.privateNetTest.LocalMachine.prop.PropertiesReader;

public class GetRouterPublickKeyFromCert {
	public static Optional<PublicKey> getPublicKey(byte[] certBytes) {
		try {
			String checkRouterDN = PropertiesReader.getSettingValue("centralRouterSubjectDN");
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "SUN");
			try(ByteArrayInputStream bis = new ByteArrayInputStream(certBytes)) {
				for(Certificate certificate : certificateFactory.generateCertificates(bis)) {
					if(!(certificate instanceof X509Certificate)) {
						continue;
					}
					X509Certificate x509Certificate = (X509Certificate)certificate;
					boolean checkDN = checkSubjectDN(checkRouterDN, x509Certificate.getSubjectDN().getName());
					if(checkDN) {
						PublicKey publicKey = x509Certificate.getPublicKey();
						x509Certificate.verify(publicKey);
						return Optional.of(publicKey);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	private static boolean checkSubjectDN(String checkDN, String subjectDNRaw) {
		String[] dnLine = subjectDNRaw.split(",");
		for(String dn : dnLine) {
			if(!dn.trim().startsWith("CN")) {
				continue;
			}
			String[] tmps = dn.trim().split("=");
			if(tmps.length == 2) {
				if(checkDN.equals(tmps[1].trim())) {
					return true;
				}
			}
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		Path path = Paths.get("centralrouter/keydir/jiko.crt");
		try(FileInputStream fis = new FileInputStream(path.toFile())) {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			for(Certificate cert : certificateFactory.generateCertificates(fis)) {
				System.out.println(((X509Certificate) cert).getIssuerDN().getName());
				System.out.println(((X509Certificate) cert).getSubjectDN().getName());
			}
		}
	}
}
