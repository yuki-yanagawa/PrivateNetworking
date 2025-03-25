package com.yana.privateNetTest.LocalMachine.console.command.action;

import java.net.InetSocketAddress;

import com.yana.privateNetTest.Common.message.send.SendMessageCreator;
import com.yana.privateNetTest.Common.micromodel.ActorRef;
import com.yana.privateNetTest.Common.micromodel.LookUpActor;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.communicate.cycript.CommunicateCycript;
import com.yana.privateNetTest.LocalMachine.console.ConsoleActor;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOutputMessage;
import com.yana.privateNetTest.LocalMachine.myInfo.MyInfoCache;

class ConnectUserAction implements ConsoleCommandAction {
	private final String[] args;
	ConnectUserAction(String[] args) {
		this.args = args;
	}
	@Override
	public void execute(SenderWrapSocket socket) {
		ActorRef actor = LookUpActor.getActorRef(ConsoleActor.class);
		String[] targetActiveUserDatas = this.args[1].split(":");
		if(targetActiveUserDatas.length == 1) {
			actor.tell(new ConsoleOutputMessage("Active user addr is not correct.", ConsoleOutputMessage.OutputType.WARN));
			return;
		}
		String ipAddr = targetActiveUserDatas[0];
		int port = Integer.parseInt(targetActiveUserDatas[1]);
		InetSocketAddress inetSocketAddress = new InetSocketAddress(ipAddr, port);
		socket.setDestSocketAddress(inetSocketAddress);
		byte[] message = SendMessageCreator.reqHelloMyNameIs(MyInfoCache.getName());
		byte[] encriptedMessage = CommunicateCycript.cycriptMessage(message);
		byte[] sendMess = SendMessageCreator.commonPrive(encriptedMessage);
		socket.sendMessage(sendMess);
	}
}
