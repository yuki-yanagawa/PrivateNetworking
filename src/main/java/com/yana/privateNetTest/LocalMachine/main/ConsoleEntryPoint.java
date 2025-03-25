package com.yana.privateNetTest.LocalMachine.main;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import com.yana.privateNetTest.Common.connectingHandshake.ConnectineHandShake;
import com.yana.privateNetTest.Common.message.recv.fragment.FragmentActor;
import com.yana.privateNetTest.Common.socket.ReciverWrapSocket;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.communicate.key.KeyStoredManager;
import com.yana.privateNetTest.LocalMachine.console.ConsoleActor;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOperator;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOutputMessageBridge;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOperator.ConsoleColor;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOperator.ConsoleIOException;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOutputMessage;
import com.yana.privateNetTest.LocalMachine.console.command.CommandAnalyzer;
import com.yana.privateNetTest.LocalMachine.handshake.CentralRouterHandShakeState;
import com.yana.privateNetTest.LocalMachine.key.generate.MyLocalKeyGenerate;
import com.yana.privateNetTest.LocalMachine.myInfo.MyInfoCache;
import com.yana.privateNetTest.LocalMachine.prop.PropertiesReader;

public class ConsoleEntryPoint {
	private static final String PROP_KEY_USERNAME = "userName";
	private static final String PROP_KEY_USERPASSWORD = "userPassWord";
	private static final String PROP_KEY_CENTRALROUTER_IPADDR = "centralRouterIpAddr";
	private static final String PROP_KEY_CENTRALROUTER_PORT = "centralRouterPort";
	private static final String PROP_KEY_USERRECIVER_PORT = "userReciverPort";
	private static final String PROP_KEY_USER_PACKET_SENDER_SIZE = "userSenderPacket";
	private static final String PROP_KEY_USER_PACKET_RECIVER_SIZE = "userReciverPacket";
	public static void main(String[] args) {
		ConsoleOperator console;
		try {
			// properties reader class
			Class.forName("com.yana.privateNetTest.LocalMachine.prop.PropertiesReader");

			console = ConsoleOperator.newInstance();
			console.clearDisplay();

			// init char
			console.printDisplayByOneChar("PrivateNeWorkCreater Ver0.1 Beta.", 20);

			// read properites
			if(!PropertiesReader.refresh()) {
				console.printDisplay("!!!WARNIG!!! USER PROPERTIES READED ERROR", ConsoleColor.RED);
				System.exit(-1);
			}
			String userName = PropertiesReader.getSettingValue("userName");
			String userPassWord = PropertiesReader.getSettingValue("userPassWord");

			if("".equals(userName.trim()) || "".equals(userPassWord.trim())) {
				userName = console.readLine("Please Enter UserName > ");
				userPassWord = console.readLine("Please Enter PassWord > ");
				PropertiesReader.setProperty(PROP_KEY_USERNAME, userName);
				PropertiesReader.setProperty(PROP_KEY_USERPASSWORD, userPassWord);
				PropertiesReader.writePropertiesToFile();
			}

			console.newLine();
			console.printDisplay("HELLO " + userName);

			// MyInfo set
			MyInfoCache.setName(userName);

			// My Key Check
			MyLocalKeyGenerate myLocalKeyGenerate = new MyLocalKeyGenerate();
			if(!myLocalKeyGenerate.exitCheck()) {
				console.printDisplay("LocalKey generating start");
				if(!myLocalKeyGenerate.generateKey()) {
					console.printDisplay("!!!WARNIG!!! LOCAL KEY GENERATE ERROR", ConsoleColor.RED);
					System.exit(-1);
				}
			}

			String routerIpAddr = PropertiesReader.getSettingValue(PROP_KEY_CENTRALROUTER_IPADDR);
			int routerPort = Integer.parseInt(PropertiesReader.getSettingValue(PROP_KEY_CENTRALROUTER_PORT));
			InetSocketAddress routerAddr = new InetSocketAddress(routerIpAddr, routerPort);

			int reciverPacketSize = Integer.parseInt(PropertiesReader.getSettingValue(PROP_KEY_USER_PACKET_RECIVER_SIZE));
			int senderPacketSize = Integer.parseInt(PropertiesReader.getSettingValue(PROP_KEY_USER_PACKET_SENDER_SIZE));
			int recvierPort = Integer.parseInt(PropertiesReader.getSettingValue(PROP_KEY_USERRECIVER_PORT));
			DatagramSocket socket = new DatagramSocket(recvierPort);
			//socket.setSoTimeout(500);

			// KeyStore Init
			KeyStoredManager.init();

			// FragmentActor Ready
			FragmentActor.activate();

			SenderWrapSocket senderWrapSocket = new SenderWrapSocket(socket, senderPacketSize);
			new ReciverThreadStart(new ReciverWrapSocket(socket, reciverPacketSize)).start();
			new RequestMessageAnalyzerThreadStart(senderWrapSocket).start();

			CentralRouterHandShakeState.setReqActionFirst();
			ConnectineHandShake.sendClientHello(senderWrapSocket, routerAddr);

			// hand shake Check
			int count = 5;
			while(!CentralRouterHandShakeState.isCompleteHandShake(true) && count > 0) {
				ConnectineHandShake.sendClientHello(senderWrapSocket, routerAddr);
				count--;
			}

			if(!CentralRouterHandShakeState.isCompleteHandShake(false)) {
				console.printDisplay("!!!WARNIG!!! CONNECT TO CENTRAL ROUTER ERRROR", ConsoleColor.RED);
				return;
			}

			// Console actor ready
			ConsoleActor.activate();
			while(true) {
				String command = console.readLine("Enter command > ");
				if("QUIT".equals(command.trim().toUpperCase())) {
					console.printDisplay("GOOD BYE...");
					break;
				}
				CommandAnalyzer commandAnalyzer = CommandAnalyzer.newInstance(senderWrapSocket);
				commandAnalyzer.analyzeCommand(command);
				ConsoleOutputMessage message = ConsoleOutputMessageBridge.getInstance().getCompletedData();
				if(message == null) {
					continue;
				}
				console.printDisplay(message.getMessage(), message.getColor());
				ConsoleOutputMessageBridge.getInstance().restetState();
			}

			// central router comminucate to inactive

			// console close
			console.close();

		} catch(ConsoleIOException e) {
			e.printStackTrace();
		} catch(IOException e) {
			
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
