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
import com.yana.privateNetTest.LocalMachine.exchange.send.CollectIngDataInfo;
import com.yana.privateNetTest.LocalMachine.myInfo.MyInfoCache;

public class TakeCommandAction extends AbstractConsoleCommand {
	TakeCommandAction(String[] args) {
		super(args);
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
		CollectIngDataInfo.setCollectingFileName(reqData);
		byte[] takeReqMessage = SendMessageCreator.reqYourData(reqData);
		byte[] encriptedMess = CommunicateCycript.cycriptMessage(takeReqMessage);
		byte[] sendMess = SendMessageCreator.commonPrive(encriptedMess);
		InetSocketAddress socketAddress = optAddr.get();
		socket.setDestSocketAddress(socketAddress);
		socket.sendMessage(sendMess);
	}

}
