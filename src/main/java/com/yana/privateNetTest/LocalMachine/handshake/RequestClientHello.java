package com.yana.privateNetTest.LocalMachine.handshake;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import com.yana.privateNetTest.Common.message.send.SendMessageCreator;
import com.yana.privateNetTest.LocalMachine.prop.PropertiesReader;

class RequestClientHello {
	private static final int RECVTIME_COUNT = 5;
	private static final int SEND_RETRY_COUNT = 3;
	/**
	 * @param socket
	 * @param senderPacket
	 * @param reciverPacket
	 * @return
	 * response ACK_JOIN_HELLO
	 */
	static byte[] execute(DatagramSocket socket, DatagramPacket senderPacket, DatagramPacket reciverPacket) {
		byte[] retMess = new byte[0];
		boolean gzipAction = Boolean.parseBoolean(PropertiesReader.getSettingValue("bodyDataGzipAction"));
		byte[] sendMess = SendMessageCreator.reqClientHello(gzipAction);
		int sendRetryCount = SEND_RETRY_COUNT;
		int recvTimeCount = RECVTIME_COUNT;
		InetAddress senderInetAddr = senderPacket.getAddress();
		senderPacket.setData(sendMess);
		try {
			while(sendRetryCount >= 0) {
				socket.send(senderPacket);
				while(recvTimeCount >= 0) {
					try {
						socket.receive(reciverPacket);
						InetAddress reciverInerAddr = reciverPacket.getAddress();
						if(senderInetAddr.equals(reciverInerAddr)) {
							int len = reciverPacket.getLength();
							retMess = Arrays.copyOf(reciverPacket.getData(), len);
							break;
						}
					} catch(InterruptedIOException e) {
						e.printStackTrace();
					}
					recvTimeCount--;
				}
				if(retMess.length > 0) {
					return retMess;
				}
				sendRetryCount--;
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return retMess;
	}
}
