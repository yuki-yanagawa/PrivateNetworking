package com.yana.privateNetTest.LocalMachine.console.command.action;

import com.yana.privateNetTest.Common.micromodel.ActorRef;
import com.yana.privateNetTest.Common.micromodel.LookUpActor;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.console.ConsoleActor;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOutputMessage;

class HelpAction implements ConsoleCommandAction {
	private static final String DISCRIPT_CMDLIST = "help cmdlist : displayed enable command list";
	private static final String DISCRIPT_TARGETCMD = "help [cmd] : displayed [cmd] exeuting result";
	private final String[] args;
	HelpAction(String[] args) {
		this.args = args;
	}
	@Override
	public void execute(SenderWrapSocket socket) {
		ActorRef actor = LookUpActor.getActorRef(ConsoleActor.class);
		if(args.length < 2) {
			actor.tell(new ConsoleOutputMessage(DISCRIPT_CMDLIST + "\r\n" + DISCRIPT_TARGETCMD));
			return;
		}

		if(args[1].trim().toUpperCase().equals("CMDLIST")) {
			StringBuilder sb = new StringBuilder();
			sb.append("[cmd] list. help [cmd] is enable.\r\n");
			for(ConsoleCommand cmd : ConsoleCommand.values()) {
				if("HELP".equals(cmd.name())) {
					continue;
				}
				sb.append(cmd.name() + ",");
			}
			String cmdList = sb.substring(0, sb.length() - 1);
			actor.tell(new ConsoleOutputMessage(cmdList));
			return;
		}

		for(ConsoleCommand cmd : ConsoleCommand.values()) {
			if(args[1].trim().toUpperCase().equals(cmd.name())) {
				actor.tell(new ConsoleOutputMessage(cmd.name() + " : " + cmd.getDiscriptionCommand()));
				return;
			}
		}
	}
}
