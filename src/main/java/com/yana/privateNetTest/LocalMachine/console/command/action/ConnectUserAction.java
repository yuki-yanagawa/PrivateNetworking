package com.yana.privateNetTest.LocalMachine.console.command.action;

import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOperator;

class ConnectUserAction implements ConsoleCommandAction {
	private final String[] args;
	ConnectUserAction(String[] args) {
		this.args = args;
	}
	@Override
	public void execute(SenderWrapSocket socket) {
		// TODO Auto-generated method stub
		
	}
}
