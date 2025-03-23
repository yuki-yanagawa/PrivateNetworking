package com.yana.privateNetTest.Common.micromodel;

class ScheduleThread extends Thread {
	private MessageQueue messageQueue;
	ScheduleThread(MessageQueue messageQueue) {
		this.messageQueue = messageQueue;
	}

	@Override
	public void run() {
		while(true) {
			IMessage message = messageQueue.takeMessage();
			message.execute();
		}
	}
}
