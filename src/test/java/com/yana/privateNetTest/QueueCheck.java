package com.yana.privateNetTest;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class QueueCheck {
	static Queue<String> queue = new ConcurrentLinkedDeque<String>();
	private static class QueueTaker extends Thread {
		@Override
		public void run() {
			while(true) {
				String data = queue.poll();
				if(data == null) {
					display("data null..");
				} else {
					display("data GET !!!!!! = " + data);
				}
				
				waitTime();
			}
		}

		private void waitTime() {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				
			}
		}
	}

	private static class QueueAdder extends Thread {
		@Override
		public void run() {
			int addCount = 1;
			while(true) {
				waitTime();
				queue.add("TEST1234");
				display("addData = " + String.valueOf(addCount++));
			}
		}

		private void waitTime() {
			try {
				Thread.sleep(400);
			} catch(InterruptedException e) {
				
			}
		}
	}

	public static void main(String[] args) {
		new QueueTaker().start();
		new QueueAdder().start();

	}

	private static synchronized void display(String str) {
		System.out.println(str);
	}
	
}
