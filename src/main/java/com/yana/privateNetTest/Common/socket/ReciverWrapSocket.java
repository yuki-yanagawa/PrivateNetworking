package com.yana.privateNetTest.Common.socket;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.yana.privateNetTest.Common.message.recv.RecvMessageQueue;
import com.yana.privateNetTest.Common.message.recv.fragment.FragmentActor;
import com.yana.privateNetTest.Common.message.recv.fragment.FragmentDataRecvMessage;
import com.yana.privateNetTest.Common.micromodel.ActorRef;
import com.yana.privateNetTest.Common.micromodel.LookUpActor;

public class ReciverWrapSocket {
	private static final int HEADER_SIZE = 16;
	private static final int RECV_COUNT = 5;
	//fragment 00 or 01 byte(4byte) 00 = Nofragment 01 = fragment
	//IF fragment
	//fragmentID(4byte)
	//fragmentSize(4byte)
	//fragmentIndex(4byte)
	private final DatagramSocket socket;
	private final DatagramPacket reciverPacket;
	private final int recvBufSize;
	public ReciverWrapSocket(DatagramSocket socket, int recvBufSize) {
		this.socket = socket;
		this.recvBufSize = recvBufSize;
		this.reciverPacket = new DatagramPacket(new byte[recvBufSize], this.recvBufSize);
	}

	public void awaitRequest() {
		//int recvCount = RECV_COUNT;
		while(true) {
			try {
				socket.receive(reciverPacket);
				int size = reciverPacket.getLength();
				byte[] recvData = Arrays.copyOf(reciverPacket.getData(), size);
				addMessageQueue(reciverPacket.getSocketAddress(), recvData);
			} catch(InterruptedIOException e) {
				//recvCount--;
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void addMessageQueue(SocketAddress socketAddress, byte[] recvData) {
		ByteBuffer buffer = ByteBuffer.wrap(recvData);

		int usedFragment = buffer.getInt();
		//Not Fragment data
		if(usedFragment == 0) {
			RecvMessageQueue.tryAdd(socketAddress, Arrays.copyOfRange(recvData, buffer.position(), recvData.length));
			return;
		}

		//Fragment data put another queue
		int fragmentId = buffer.getInt();
		int fragmentSize = buffer.getInt();
		int fragmentIndex = buffer.getInt();
		ActorRef actor = LookUpActor.getActorRef(FragmentActor.class);
		actor.tell(new FragmentDataRecvMessage(fragmentId, fragmentSize, fragmentIndex,
				Arrays.copyOfRange(recvData, buffer.position(), recvData.length),socketAddress));
	}
}
