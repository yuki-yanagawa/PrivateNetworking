package com.yana.privateNetTest.LocalMachine.exchange.recv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.yana.privateNetTest.LocalMachine.exchange.fileStoreKey.CreateFileStoreKeyFromName;

/**
 * @author user
 *
 */
public class ExchangeDirCollectInfo {
	private static CreateFileStoreKeyFromName createFileStoreKeyFromName;
	private static Map<String, ExchangeEnableFile> fileMapList;

	static {
		createFileStoreKeyFromName = CreateFileStoreKeyFromName.newInstance();
		fileMapList = new ConcurrentHashMap<>();
	}

	private static synchronized void addFileToMap(String key, ExchangeEnableFile file) {
		fileMapList.put(key, file);
	}

	public static void addFile(Path filePath) {
		long size = -1;
		try {
			size = Files.size(filePath.toAbsolutePath());
		} catch(IOException e) {
			e.printStackTrace();
			size = -1;
		}
		if(size == -1) {
			return;
		}
		String key = createFileStoreKeyFromName.createKey(filePath.toFile().getName());
		ExchangeEnableFile exchangeEnableFile = new ExchangeEnableFile(filePath, size);
		addFileToMap(key, exchangeEnableFile);
	}

	public static List<ExchangeEnableFile> getEnableExchangeAllList() {
		return fileMapList.values().stream().collect(Collectors.toList());
	}

	public static void deleteFile(Path filePath) {
		String key = createFileStoreKeyFromName.createKey(filePath.toFile().getName());
		deleteFileFromMap(key);
	}

	private static synchronized void deleteFileFromMap(String key) {
		fileMapList.remove(key);
	}

	public static boolean searchFileName(String fileName) {
		String key = createFileStoreKeyFromName.createKey(fileName);
		return fileMapList.containsKey(key);
	}

	public static ExchangeEnableFile getTargetFileInfo(String targetFileName) {
		String key = createFileStoreKeyFromName.createKey(targetFileName);
		return fileMapList.get(key);
	}
}
