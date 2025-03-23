package com.yana.privateNetTest.Common.message.recv;

import java.net.SocketAddress;

public class RecvMessage {
	protected SocketAddress socketAddress;
	protected byte[] messageData;
	boolean setting;
	RecvMessage() {
		messageData = new byte[0];
		setting = false;
	}

	boolean setEnable() {
		return !setting;
	}

	void setParameter(SocketAddress socketAddress, byte[] messageData) {
		this.socketAddress = socketAddress;
		this.messageData = messageData;
		setting = true;
	}

	public void clearParameter() {
		this.socketAddress = null;
		this.messageData = new byte[0];
		setting = false;
	}

	public byte[] getMessageData() {
		return messageData;
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}
}
