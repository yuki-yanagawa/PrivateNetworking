package com.yana.privateNetTest.LocalMachine.console.command.action;

enum ConsoleCommand {
	HELP("ENTER command HELP"),
	ACTIVE_USER_LIST("Get active user list from central router."),
	CONNECT_USER("connect active user. nessary argumnet is [ipaddr:portNo]."
			+ " ex) I want to connect active userA. userA ipaddr is 192.168.0.10, port is 9090."
			+ " Then, you enter \"CONNECT_USER 192.168.0.10:9090\""),
	NONE("None exeute");
	private String discriptionCommand;
	private ConsoleCommand(String discriptionCommand) {
		this.discriptionCommand = discriptionCommand;
	}

	String getDiscriptionCommand() {
		return this.discriptionCommand;
	}
}
