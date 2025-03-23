package com.yana.privateNetTest.LocalMachine.console;

import java.io.IOException;

import jline.console.ConsoleReader;

public class ConsoleOperator {
	public static class ConsoleIOException extends IOException {
		ConsoleIOException() {
			super("ConsoleIOException");
		}
	}

	public enum ConsoleColor {
		BLUE("\u001B[34m","\u001B[0m"),
		GREEN("\u001B[32m", "\u001B[0m"),
		RED("\u001B[31m", "\u001B[0m");
		private String[] instractedCharCode = new String[2];
		ConsoleColor(String head, String tail) {
			instractedCharCode[0] = head;
			instractedCharCode[1] = tail;
		}
		String getInstractedHead() {
			return instractedCharCode[0];
		}
		String getInstractedTail() {
			return instractedCharCode[1];
		}
	}

	private ConsoleReader console;
	private ConsoleOperator() throws IOException {
		this.console = new ConsoleReader();
	}

	public static ConsoleOperator newInstance() throws ConsoleIOException {
		try {
			return new ConsoleOperator();
		} catch(IOException e) {
			throw new ConsoleIOException();
		}
	}

	public void clearDisplay() throws ConsoleIOException {
		try {
			console.clearScreen();
		} catch(IOException e) {
			throw new ConsoleIOException();
		}
	}

	public void printDisplay(String data) throws ConsoleIOException{
		try {
			console.print(data);
			console.flush();
			console.accept();
		} catch(IOException e) {
			throw new ConsoleIOException();
		}
	}

	public void printDisplay(String data, ConsoleColor color) throws ConsoleIOException{
		try {
			console.print(color.getInstractedHead());
			console.print(data);
			console.print(color.getInstractedTail());
			console.flush();
			console.accept();
		} catch(IOException e) {
			throw new ConsoleIOException();
		}
	}

	public void printDisplayByOneChar(String data, long interval) throws ConsoleIOException{
		try {
			for(int i = 0; i < data.length(); i++) {
				console.print(data.substring(i, i+ 1));
				console.flush();
				waitInterval(interval);
			}
			console.accept();
		} catch(IOException e) {
			throw new ConsoleIOException();
		}
	}

	public void close() {
		console.close();
	}

	public void printDisplayByOneChar(String data, long interval, ConsoleColor color) throws ConsoleIOException{
		try {
			console.print(color.getInstractedHead());
			for(int i = 0; i < data.length(); i++) {
				console.print(data.substring(i, i+ 1));
				console.flush();
				waitInterval(interval);
			}
			console.print(color.getInstractedTail());
			console.accept();
		} catch(IOException e) {
			throw new ConsoleIOException();
		}
	}

	public String readLine(String headMessage) throws ConsoleIOException {
		String retMessage = "";
		try {
			retMessage = console.readLine(headMessage);
		} catch(IOException e) {
			throw new ConsoleIOException();
		}
		return retMessage;
	}

	public void newLine() throws ConsoleIOException {
		try {
			console.accept();
		} catch(IOException e) {
			throw new ConsoleIOException();
		}
	}

	private static void waitInterval(long interval) {
		try {
			Thread.sleep(interval);
		} catch(InterruptedException e) {
			
		}
	}
}
