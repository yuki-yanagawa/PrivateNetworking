package com.yana.privateNetTest.LocalMachine.handshake;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.PublicKey;
import java.util.Optional;

public class ConnectEstablishedHandShake {
	public static boolean execute(DatagramSocket socket, DatagramPacket senderPacket, DatagramPacket reciverPacket) {
		byte[] ackJoinHelloBytes = RequestClientHello.execute(socket, senderPacket, reciverPacket);
		if(ackJoinHelloBytes.length == 0) {
			return false;
		}
		Optional<PublicKey> optPubKey = GetServerPublicKeyFromAckJoinHello.execute(ackJoinHelloBytes);
		if(!optPubKey.isPresent()) {
			return false;
		}
		return true;
	}
}
