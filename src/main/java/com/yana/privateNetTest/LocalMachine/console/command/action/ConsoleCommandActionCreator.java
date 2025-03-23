package com.yana.privateNetTest.LocalMachine.console.command.action;

public class ConsoleCommandActionCreator {

	public static ConsoleCommandAction selectedConsoleCommandAction(String command) {
		ConsoleCommand cmd;
		if(command == null || command.isEmpty()) {
			cmd = ConsoleCommand.NONE;
		}

		String[] commandLine = command.split("\\s+");
		cmd = selectedConsoleCommand(commandLine[0]);

		switch(cmd) {
		case NONE:
			return new NoneAction();
		case HELP:
			return new HelpAction(commandLine);
		case ACTIVE_USER_LIST:
			return new GetActiveUserListAction(commandLine);
		case CONNECT_USER:
			return new ConnectUserAction(commandLine);
		default:
			return new NoneAction();
		}

	}

	private static ConsoleCommand selectedConsoleCommand(String command) {
		for(ConsoleCommand cmd : ConsoleCommand.values()) {
			if(command.trim().toUpperCase().equals(cmd.name())) {
				return cmd;
			}
		}
		return ConsoleCommand.NONE;
	}
}
