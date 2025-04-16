package com.yana.privateNetTest.LocalMachine.console;

import com.yana.privateNetTest.Common.micromodel.Furture;

public class ConsoleOutputMessageBridge implements Furture<ConsoleOutputMessage> {
	private static ConsoleOutputMessageBridge bridge = new ConsoleOutputMessageBridge();
	private ConsoleOutputMessageBridge(){
	}
	enum STATE{
		WAIT,READY
	};

	public static ConsoleOutputMessageBridge getInstance() {
		return bridge;
	}

	private STATE state = STATE.WAIT;
	private ConsoleOutputMessage bridingData;
	@Override
	public synchronized ConsoleOutputMessage getCompletedData(long timeOut) {
		if(STATE.WAIT == state) {
			try {
				wait(timeOut);
			} catch(InterruptedException e) {
				
			}
		}
		if(STATE.WAIT == state) {
			return null;
		}
		return bridingData;
	}

	public synchronized void restetState() {
		state = STATE.WAIT;
		this.bridingData = null;
	}

	public synchronized void setBridingData(ConsoleOutputMessage bridingData) {
		this.bridingData = bridingData;
		state = STATE.READY;
		this.notifyAll();
	}

}
