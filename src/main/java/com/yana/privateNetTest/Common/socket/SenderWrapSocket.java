package com.yana.privateNetTest.Common.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class SenderWrapSocket {
	private static final int HEADER_SIZE = 16;
	//fragment 00 or 01 byte(4byte) 00 = Nofragment 01 = fragment
	//IF fragment
	//fragmentID(4byte)
	//fragmentSize(4byte)
	//fragmentIndex(4byte)
	private final DatagramSocket socket;
	private final int senderBuffize;
	private SocketAddress destSocketAddress;
	private DatagramPacket sendDataPacket;

	public SenderWrapSocket(DatagramSocket socket, int senderBuffize) {
		this.socket = socket;
		this.senderBuffize = senderBuffize;
		this.sendDataPacket = new DatagramPacket(new byte[this.senderBuffize], this.senderBuffize);
	}

	public void setDestSocketAddress(SocketAddress destSocketAddress) {
		this.destSocketAddress = destSocketAddress;
	}

	public void sendMessage(byte[] message) {
		if(destSocketAddress == null) {
			return;
		}

		this.sendDataPacket.setSocketAddress(destSocketAddress);
		// No fragment
		if(message.length < this.senderBuffize - 4) {
			ByteBuffer buffer = ByteBuffer.allocate(message.length + 4);
			buffer.putInt(0);
			buffer.put(message);
			this.sendDataPacket.setData(buffer.array());
			try {
				this.socket.send(this.sendDataPacket);
			} catch(IOException e) {
				e.printStackTrace();
			}
			return;
		}

		// Fragment data
		int maxSendBufSize = senderBuffize - HEADER_SIZE;
		int messageRemainLength = message.length;
		int fragmentId = new Random().nextInt();
		int fragmentSize = (int)Math.ceil((double)messageRemainLength / maxSendBufSize);
		ByteBuffer buffer = ByteBuffer.allocate(senderBuffize);
		int fragmentIndex = 1;
		int currentPositon = 0;
		while(messageRemainLength > 0) {
			buffer.putInt(1);
			buffer.putInt(fragmentId);
			buffer.putInt(fragmentSize);
			buffer.putInt(fragmentIndex);
			if(messageRemainLength > maxSendBufSize) {
				buffer.put(message, currentPositon, maxSendBufSize);
				currentPositon += maxSendBufSize;
				this.sendDataPacket.setData(Arrays.copyOf(buffer.array(), HEADER_SIZE + maxSendBufSize));
			} else {
				buffer.put(message, currentPositon, messageRemainLength);
				currentPositon += messageRemainLength;
				this.sendDataPacket.setData(Arrays.copyOf(buffer.array(), HEADER_SIZE + messageRemainLength));
			}
			messageRemainLength = messageRemainLength - maxSendBufSize;
			fragmentIndex++;
			try {
				this.socket.send(this.sendDataPacket);
			} catch(IOException e) {
				e.printStackTrace();
				break;
			}
			buffer.clear();
		}
	}
}
