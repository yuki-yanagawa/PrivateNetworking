package com.yana.privateNetTest.LocalMachine.console;

import com.yana.privateNetTest.Common.micromodel.IMessage;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOperator.ConsoleColor;

public class ConsoleOutputMessage implements IMessage {
	public enum OutputType {
		INFO,
		WARN,
	};
	private OutputType outputType;
	private String message;
	public ConsoleOutputMessage(String message) {
		this.outputType = OutputType.INFO;
		this.message = message;
	}
	public ConsoleOutputMessage(String message, OutputType outputType) {
		this.outputType = outputType;
		this.message = message;
	}
	@Override
	public void execute() {
		ConsoleOutputMessageBridge.getInstance().setBridingData(this);
	}

	public String getMessage() {
		return this.message;
	}

	public ConsoleOperator.ConsoleColor getColor() {
		switch(outputType){
		case INFO:
			return ConsoleColor.BLUE;
		case WARN:
			return ConsoleColor.RED;
		default:
			return ConsoleColor.BLUE;
		}
	}
}
