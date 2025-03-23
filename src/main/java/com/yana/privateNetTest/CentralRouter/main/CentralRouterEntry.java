package com.yana.privateNetTest.CentralRouter.main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.yana.privateNetTest.CentralRouter.key.RouterStoredKey;
import com.yana.privateNetTest.CentralRouter.prop.RouterProperties;
import com.yana.privateNetTest.Common.message.recv.fragment.FragmentActor;
import com.yana.privateNetTest.Common.socket.ReciverWrapSocket;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;

public class CentralRouterEntry {
	private static final String PROP_KEY_ROUTER_PACKET_RECIVER_SIZE = "reciverPacketSize";
	private static final String PROP_KEY_ROUTER_PACKET_SENDER_SIZE = "senderPacketSize";
	public static void main(String[] args) {

		try {
			Class.forName("com.yana.privateNetTest.CentralRouter.prop.RouterProperties");
			RouterProperties.refresh();

			// RouterKeyStore init
			RouterStoredKey.initStoredKey();

			// FragmentActor Ready
			FragmentActor.activate();

			DatagramSocket socket = new DatagramSocket(9090);
			//DatagramPacket reciverPacket = new DatagramPacket(new byte[2048], 2048);
			int reciverPacketSize = Integer.parseInt(RouterProperties.getSettingValue(PROP_KEY_ROUTER_PACKET_RECIVER_SIZE));
			int senderPacketSize = Integer.parseInt(RouterProperties.getSettingValue(PROP_KEY_ROUTER_PACKET_SENDER_SIZE));
			ReciverWrapSocket reciverWrapSocket = new ReciverWrapSocket(socket, reciverPacketSize);
			RouterReciverThreadStart reciverThread = new RouterReciverThreadStart(reciverWrapSocket);
			reciverThread.start();
	
			// Sender Worker Thread Size = 10
			for(int i = 0; i < 1; i++) {
				new RouterRequestMessageAnalyzerThreadStart(new SenderWrapSocket(socket, senderPacketSize)).start();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main_old(String[] args) throws Exception {
		DatagramSocket socket = new DatagramSocket(9090);
		DatagramPacket recvPacket = new DatagramPacket(new byte[2048], 2048);
		DatagramPacket sendPacket = new DatagramPacket(new byte[2048], 2048);
		while(true) {
			socket.receive(recvPacket);
			String ipAddr = recvPacket.getAddress().getHostAddress();
			int port = recvPacket.getPort();
			String retStr = "ACK_JOIN\r\n" + "IPADDR:" + ipAddr + "\r\n" +
					"PORT:" + String.valueOf(port) + "\r\n\r\n";
			System.out.println(retStr);
			byte[] retBytes = retStr.getBytes();
			sendPacket.setData(retBytes, 0, retBytes.length);
			sendPacket.setSocketAddress(recvPacket.getSocketAddress());
			socket.send(sendPacket);
		}
	}
}
