package com.yana.privateNetTest.CentralRouter.stored.activeuser;

import java.util.Optional;

public class ActiveUserInfo {
	private final String ipAddr;
	private final int port;

	private ActiveUserInfo(String ipAddr, int port) {
		this.ipAddr = ipAddr;
		this.port = port;
	}

	public boolean isSameInfo(String ipAddr, int port) {
		return this.ipAddr.equals(ipAddr) && this.port == port;
	}

	public String getConnectingAddr() {
		return this.ipAddr + ":" + String.valueOf(this.port);
	}

	public static Optional<ActiveUserInfo> newInstance(String ipAddr, int port) {
		if(ipAddr == null || ipAddr.isEmpty()) {
			return Optional.empty();
		}
		if(port <= 0) {
			return Optional.empty();
		}
		return Optional.of(new ActiveUserInfo(ipAddr, port));
	}
}
