package com.yana.privateNetTest.LocalMachine.myInfo;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Optional;

public class MyInfoCache {
	private static class UserInfo {
		String name;
		InetSocketAddress addr;
		UserInfo connectedUser;
		private UserInfo() {
			
		}
		private UserInfo(String name, InetSocketAddress addr) {
			this.name = name;
			this.addr = addr;
		}
	}

	private static UserInfo myInfo = new UserInfo();

	public static void setName(String name) {
		myInfo.name = name;
	}

	public static String getName() {
		return myInfo.name;
	}

	public synchronized static boolean setConnectedUser(SocketAddress connectedUser, String connectedUsername) {
		myInfo.connectedUser = null;
		if(!(connectedUser instanceof InetSocketAddress)) {
			return false;
		}
		InetSocketAddress tmpSocketAddr = (InetSocketAddress)connectedUser;
		myInfo.connectedUser = new UserInfo(connectedUsername, tmpSocketAddr);
		return true;
	}

	public synchronized static Optional<InetSocketAddress> getConnectedUser() {
		if(myInfo.connectedUser == null) {
			return Optional.empty();
		}
		return Optional.of(myInfo.connectedUser.addr);
	}

	public synchronized static Optional<String> getConnectedUserName() {
		if(myInfo.connectedUser == null) {
			return Optional.empty();
		}
		return Optional.of(myInfo.connectedUser.name);
	}
}
