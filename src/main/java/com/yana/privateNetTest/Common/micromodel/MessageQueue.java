package com.yana.privateNetTest.Common.micromodel;

import java.util.ArrayDeque;
import java.util.Queue;

public class MessageQueue {
	Queue<IMessage> queue = new ArrayDeque<>();
	synchronized void putMessage(IMessage message) {
		queue.add(message);
		notifyAll();
	}

	synchronized IMessage takeMessage() {
		while(queue.size() <= 0) {
			try {
				wait();
			} catch(InterruptedException e) {
				
			}
		}
		return queue.poll();
	}
}
