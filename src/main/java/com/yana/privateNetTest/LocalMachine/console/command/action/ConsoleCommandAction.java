package com.yana.privateNetTest.LocalMachine.console.command.action;

import com.yana.privateNetTest.Common.socket.SenderWrapSocket;

public interface ConsoleCommandAction {
	public void execute(SenderWrapSocket socket);
}
