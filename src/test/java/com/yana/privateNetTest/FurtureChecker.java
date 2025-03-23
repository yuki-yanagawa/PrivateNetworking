package com.yana.privateNetTest;

import jline.console.ConsoleReader;

public class FurtureChecker {
	public static void main(String[] args) throws Exception {
		ConsoleReader cr = new ConsoleReader();
		cr.setPrompt("1234 > ");
		String sample = cr.readLine();
		System.out.println(sample);
		Furture<String> test = new Furture<>();
		test.getResult();
	}
}
