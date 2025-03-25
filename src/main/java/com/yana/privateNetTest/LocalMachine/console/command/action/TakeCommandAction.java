package com.yana.privateNetTest.LocalMachine.console.command.action;

import java.net.InetSocketAddress;
import java.util.Optional;

import com.yana.privateNetTest.Common.message.send.SendMessageCreator;
import com.yana.privateNetTest.Common.micromodel.ActorRef;
import com.yana.privateNetTest.Common.micromodel.LookUpActor;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.console.ConsoleActor;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOutputMessage;
import com.yana.privateNetTest.LocalMachine.myInfo.MyInfoCache;

public class TakeCommandAction implements ConsoleCommandAction {
	private final String[] args;
	TakeCommandAction(String[] args) {
		this.args = args;
	}
	@Override
	public void execute(SenderWrapSocket socket) {
		Optional<InetSocketAddress> optAddr = MyInfoCache.getConnectedUser();
		if(!optAddr.isPresent()) {
			ActorRef actor = LookUpActor.getActorRef(ConsoleActor.class);
			actor.tell(new ConsoleOutputMessage("Do not exsit connected user", ConsoleOutputMessage.OutputType.WARN));
			return;
		}
		String reqData = args[1];
		//create take message
		//byte[] takeReqMessage = SendMessageCreator.reqActiveUserList();
		InetSocketAddress socketAddress = optAddr.get();
		socket.setDestSocketAddress(socketAddress);
	}

}
