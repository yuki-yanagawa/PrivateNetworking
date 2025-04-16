package com.yana.privateNetTest.LocalMachine.exchange.recv;

import java.nio.file.Path;

public class ExchangeEnableFile {
	private Path path;
	private long fileSize;

	ExchangeEnableFile(Path path, long fileSize) {
		this.path = path;
		this.fileSize = fileSize;
	}

	public Path getFilePath() {
		return this.path;
	}

	public long getFileSize() {
		return this.fileSize;
	}
}
