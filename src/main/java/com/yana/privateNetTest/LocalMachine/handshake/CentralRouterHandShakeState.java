package com.yana.privateNetTest.LocalMachine.handshake;

public class CentralRouterHandShakeState {
	private static CentralRouterHandShakeState handShakeState = new CentralRouterHandShakeState();
	private static Object lock = new Object();
	enum STATE{
		REQ_1,
		REQ_2,
		ESTABLISHED,
	}

	private STATE state;
	private STATE beforeCheckedState;
	private CentralRouterHandShakeState() {
		state = STATE.REQ_1;
		beforeCheckedState = STATE.REQ_1;
	}

	public static boolean isCompleteHandShake(boolean needWait) {
		if(handShakeState.state == STATE.ESTABLISHED) {
			return true;
		}
		if(!needWait) {
			return false;
		}
		synchronized (lock) {
			try {
				lock.wait(1000, 0);
			} catch(InterruptedException e) {
				
			}
		}
		return handShakeState.state == STATE.ESTABLISHED;
	}

	public static void setCompleteHandShake() {
		handShakeState.state = STATE.ESTABLISHED;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public static void setReqActionFirst() {
		handShakeState.state = STATE.REQ_1;
	}

	public static void setReqActionSencond() {
		handShakeState.state = STATE.REQ_2;
	}
}
