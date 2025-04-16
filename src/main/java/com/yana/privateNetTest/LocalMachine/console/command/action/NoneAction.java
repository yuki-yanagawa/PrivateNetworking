package com.yana.privateNetTest.LocalMachine.console.command.action;

import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOperator;

class NoneAction extends AbstractConsoleCommand {
	NoneAction(String[] args) {
		super(args);
	}
	@Override
	public void execute(SenderWrapSocket socket) {
		// no action
	}
}
