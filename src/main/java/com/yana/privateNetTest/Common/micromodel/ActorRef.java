package com.yana.privateNetTest.Common.micromodel;

public abstract class ActorRef {
	protected MessageQueue messageQueue;
	protected ScheduleThread scheduleThread;

	protected ActorRef() {
		messageQueue = new MessageQueue();
		scheduleThread = new ScheduleThread(messageQueue);
		scheduleThread.start();
	}

	public void tell(IMessage message) {
		messageQueue.putMessage(message);
	}

	abstract public <T> Furture<T> ask();
}
