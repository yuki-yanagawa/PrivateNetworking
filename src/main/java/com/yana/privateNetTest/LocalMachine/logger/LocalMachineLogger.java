package com.yana.privateNetTest.LocalMachine.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LocalMachineLogger {
	private static LocalMachineLogger localMachineLogger;
	private Logger logger;
	private FileHandler fhandler;

	private LocalMachineLogger() {
	}

	private void settingLog() throws IOException {
		String serviceName = "PrivateNetWork";
		logger = Logger.getLogger(serviceName);
		String filePattern = "log/" + serviceName + "_%g.log";
		fhandler = new FileHandler(filePattern);
		SimpleFormatter sm = new SimpleFormatter();
		fhandler.setFormatter(sm);
		logger.addHandler(fhandler);
		logger.setLevel(Level.INFO);
	}

	public static boolean start() {
		localMachineLogger = new LocalMachineLogger();
		try {
			localMachineLogger.settingLog();
		} catch(IOException e) {
			return false;
		}
		return true;
	}

	public static void info(String message) {
		localMachineLogger.logger.info(message);
	}

	public static void warn(String message) {
		localMachineLogger.logger.warning(message);
	}
}
