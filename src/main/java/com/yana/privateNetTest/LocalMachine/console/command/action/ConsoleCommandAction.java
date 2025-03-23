package com.yana.privateNetTest.LocalMachine.console.command.action;

import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOperator;

public interface ConsoleCommandAction {
	public void execute(SenderWrapSocket socket);
}
