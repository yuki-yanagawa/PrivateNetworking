package com.yana.privateNetTest.LocalMachine.console.command.action;

import java.net.InetSocketAddress;

import com.yana.privateNetTest.Common.message.send.SendMessageCreator;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.communicate.cycript.CommunicateCycript;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOperator;
import com.yana.privateNetTest.LocalMachine.prop.PropertiesReader;

class GetActiveUserListAction implements ConsoleCommandAction {
	private static final String PROP_KEY_CENTRALROUTER_IPADDR = "centralRouterIpAddr";
	private static final String PROP_KEY_CENTRALROUTER_PORT = "centralRouterPort";

	private final String[] args;
	private final InetSocketAddress routerAddress;
	GetActiveUserListAction(String[] args) {
		this.args = args;
		String routerIpAddr = PropertiesReader.getSettingValue(PROP_KEY_CENTRALROUTER_IPADDR);
		int routerPort = Integer.parseInt(PropertiesReader.getSettingValue(PROP_KEY_CENTRALROUTER_PORT));
		routerAddress = new InetSocketAddress(routerIpAddr, routerPort);
	}
	@Override
	public void execute(SenderWrapSocket socket) {
		byte[] message = SendMessageCreator.reqActiveUserList();
		byte[] encriptedMessage = CommunicateCycript.cycriptMessage(message);
		byte[] sendMess = SendMessageCreator.reqCommon(encriptedMessage);
		socket.setDestSocketAddress(routerAddress);
		socket.sendMessage(sendMess);
	}

}
