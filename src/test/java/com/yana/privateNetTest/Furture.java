package com.yana.privateNetTest;

public class Furture<T> {
	T resultData;
	boolean ready = false;
	public synchronized void setResult(T data) {
		resultData = data;
		ready = true;
	}

	public synchronized T getResult() {
		while(!ready) {
			try {
				wait();
			} catch(InterruptedException e) {
				
			}
		}
		return resultData;
	}
}
