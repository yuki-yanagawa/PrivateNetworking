package com.yana.privateNetTest.Common.message.recv;

import java.net.SocketAddress;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecvMessageQueue {
	private static List<RecvMessage> recvMessagesList = new CopyOnWriteArrayList<>();
	private static RecvMessageQueue recvMessageQueue = new RecvMessageQueue();
	Queue<RecvMessage> messageQueue = new ConcurrentLinkedDeque<>();

	private Object queueLock = new Object();
	private RecvMessageQueue() {
		for(int i = 0; i < 30; i++) {
			recvMessagesList.add(new RecvMessage());
		}
	}

	private RecvMessage getRequestMessageFromQueue() throws InterruptedException {
		synchronized (queueLock) {
			while(true) {
				if(messageQueue.peek() == null) {
					queueLock.wait();
				} else {
					queueLock.notifyAll();
					return messageQueue.poll();
				}
			}
		}
	}
	private void addRequestMessageToQueue(SocketAddress socketAddress, byte[] requestMessage) {
		synchronized (queueLock) {
			for(RecvMessage reqMess : recvMessagesList) {
				if(reqMess.setEnable()) {
					reqMess.setParameter(socketAddress, requestMessage);
					messageQueue.add(reqMess);
					queueLock.notifyAll();
					break;
				}
			}
		}
	}

	/**
	 * @return
	 * RequestMessage. If InterruptedException occured, then null return.
	 */
	public static RecvMessage tryTake() {
		try {
			return recvMessageQueue.getRequestMessageFromQueue();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void tryAdd(SocketAddress socketAddress, byte[] requestMessage) {
		recvMessageQueue.addRequestMessageToQueue(socketAddress, requestMessage);
	}
}
