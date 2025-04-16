package com.yana.privateNetTest.LocalMachine.console.command.action;

import java.net.InetSocketAddress;
import java.util.Optional;

import com.yana.privateNetTest.Common.message.send.SendMessageCreator;
import com.yana.privateNetTest.Common.micromodel.ActorRef;
import com.yana.privateNetTest.Common.micromodel.LookUpActor;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.communicate.cycript.CommunicateCycript;
import com.yana.privateNetTest.LocalMachine.console.ConsoleActor;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOutputMessage;
import com.yana.privateNetTest.LocalMachine.myInfo.MyInfoCache;

public class GetListAllCommand extends AbstractConsoleCommand {
	GetListAllCommand(String[] args) {
		super(args);
	}
	@Override
	public void execute(SenderWrapSocket socket) {
		Optional<InetSocketAddress> sockAddrOpt = MyInfoCache.getConnectedUser();
		if(!sockAddrOpt.isPresent()) {
			ActorRef actor = LookUpActor.getActorRef(ConsoleActor.class);
			actor.tell(new ConsoleOutputMessage("Do not exsit connected user", ConsoleOutputMessage.OutputType.WARN));
		}
		byte[] mess = SendMessageCreator.reqListAllData();
		byte[] encriptedMess = CommunicateCycript.cycriptMessage(mess);
		byte[] sendMess = SendMessageCreator.commonPrive(encriptedMess);
		socket.setDestSocketAddress(sockAddrOpt.get());
		socket.sendMessage(sendMess);
	}

}
