package com.yana.privateNetTest.CentralRouter.stored.activeuser;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class StoredActiveUser {
	private static StoredActiveUser singletone = new StoredActiveUser();
	private List<ActiveUserInfo> activeUserInfos = new CopyOnWriteArrayList<>();
	private StoredActiveUser() {
	}

	private synchronized List<ActiveUserInfo> _getActiveUserList() {
		return activeUserInfos;
	}

	private synchronized void _registActiveUser(ActiveUserInfo activeUserInfo) {
		activeUserInfos.add(activeUserInfo);
	}

	public static void registerActiveUserInfo(SocketAddress socketAddress) {
		if(!(socketAddress instanceof InetSocketAddress)) {
			return;
		}
		InetSocketAddress tmpSockAddr = (InetSocketAddress)socketAddress;
		int port = tmpSockAddr.getPort();
		String ipAddr = tmpSockAddr.getAddress().getHostAddress();
		Optional<ActiveUserInfo> registActiveUserOpt = ActiveUserInfo.newInstance(ipAddr, port);
		if(!registActiveUserOpt.isPresent()) {
			return;
		}
		for(ActiveUserInfo activeUserInfo : getActiveUserList()) {
			if(activeUserInfo.isSameInfo(ipAddr, port)) {
				return;
			}
		}
		registActiveUser(registActiveUserOpt.get());
	}

	public static List<ActiveUserInfo> getActiveUserList() {
		return singletone._getActiveUserList();
	}

	private static void registActiveUser(ActiveUserInfo activeUserInfo) {
		singletone._registActiveUser(activeUserInfo);
	}
}
