package com.yana.privateNetTest.LocalMachine.console.command;

import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.console.command.action.ConsoleCommandAction;
import com.yana.privateNetTest.LocalMachine.console.command.action.ConsoleCommandActionCreator;

public class CommandAnalyzer {
	private ConsoleCommandAction action;
	private final SenderWrapSocket senderWrapSocket;
	private CommandAnalyzer(SenderWrapSocket senderWrapSocket) {
		this.senderWrapSocket = senderWrapSocket;
		this.action = ConsoleCommandActionCreator.selectedConsoleCommandAction("");
	}

	public static CommandAnalyzer newInstance(SenderWrapSocket senderWrapSocket) {
		return new CommandAnalyzer(senderWrapSocket);
	}

	public void analyzeCommand(String command) {
		this.action = ConsoleCommandActionCreator.selectedConsoleCommandAction(command);
		this.action.execute(senderWrapSocket);
	}
}
