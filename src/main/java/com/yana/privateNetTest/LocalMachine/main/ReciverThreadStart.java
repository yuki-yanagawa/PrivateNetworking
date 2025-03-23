package com.yana.privateNetTest.LocalMachine.main;

import com.yana.privateNetTest.Common.socket.ReciverWrapSocket;

class ReciverThreadStart extends Thread {
	private ReciverWrapSocket wrapSocket;
	public ReciverThreadStart(ReciverWrapSocket wrapSocket) {
		this.wrapSocket = wrapSocket;
	}

	@Override
	public void run() {
		while(true) {
			wrapSocket.awaitRequest();
		}
	}
}