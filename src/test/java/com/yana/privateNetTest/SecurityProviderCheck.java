package com.yana.privateNetTest;

import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;

public class SecurityProviderCheck {
	public static void main(String[] args) {
		for(Provider p : Security.getProviders()) {
			for(Service s : p.getServices()) {
				System.out.println(p.getName() + " " + s.getType() + " " + s.getAlgorithm());
			}
		}
	}
}
