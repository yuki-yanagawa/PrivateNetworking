package com.yana.privateNetTest.Common.connectingHandshake;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import com.yana.privateNetTest.Common.message.send.SendMessageCreator;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;

public class ConnectineHandShake {
	public static void sendClientHello(SenderWrapSocket wrapSocket, SocketAddress destSocketAddress) {
		byte[] sendMess = SendMessageCreator.reqClientHello();
		wrapSocket.setDestSocketAddress(destSocketAddress);
		wrapSocket.sendMessage(sendMess);
	}

	public static void sendMessageAckJoinHello(SenderWrapSocket wrapSocket, Path serverCertPath) {
		try {
			int size = (int)Files.size(serverCertPath);
			byte[] serverCertBytes = new byte[size];
			try(FileInputStream fis = new FileInputStream(serverCertPath.toFile())) {
				fis.read(serverCertBytes);
			}
			wrapSocket.sendMessage(SendMessageCreator.ackServerHello(serverCertBytes));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendMessageAckRegistYou(SenderWrapSocket wrapSocket, 
			byte[] encriptedCommpnSecretKey, byte[] encriptedVectorKeyWord, byte[] encriptedAlgorthm) {
		wrapSocket.sendMessage(SendMessageCreator.ackRegistYou(encriptedCommpnSecretKey, encriptedVectorKeyWord, encriptedAlgorthm));
	}

	public static void sendMessageReqRegsitMe(SenderWrapSocket wrapSocket, byte[] pubKeyBytes, byte[] pubKeySignBytes) {
		wrapSocket.sendMessage(SendMessageCreator.reqResitMe(pubKeyBytes, pubKeySignBytes));
	}
}
