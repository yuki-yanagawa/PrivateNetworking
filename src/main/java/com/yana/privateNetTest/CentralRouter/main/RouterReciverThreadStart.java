package com.yana.privateNetTest.CentralRouter.main;

import com.yana.privateNetTest.Common.socket.ReciverWrapSocket;

class RouterReciverThreadStart extends Thread {
	ReciverWrapSocket wrapSocket;
	RouterReciverThreadStart(ReciverWrapSocket wrapSocket) {
		this.wrapSocket = wrapSocket;
	}

	@Override
	public void run() {
		while(true) {
			wrapSocket.awaitRequest();
		}
	}
}
