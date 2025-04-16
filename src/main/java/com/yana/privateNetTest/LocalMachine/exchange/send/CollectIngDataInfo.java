package com.yana.privateNetTest.LocalMachine.exchange.send;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CollectIngDataInfo {
	private static final Path PATH = Paths.get("collectDir");
	private static CollectIngDataInfo collectIngDataInfo = new CollectIngDataInfo();

	public static void setCollectingFileName(String fileName) {
		collectIngDataInfo._setCollectingFileName(fileName);
	}

	private void _setCollectingFileName(String fileName) {
		this.fileName = fileName;
	}

	private String _getFileName() {
		return this.fileName;
	}

	public static String getCollectionFileName() {
		return collectIngDataInfo._getFileName();
	}

	public static boolean writeData(byte[] data) {
		try(FileOutputStream fos = new FileOutputStream(PATH + File.separator + getCollectionFileName())) {
			fos.write(data);
			fos.flush();
		} catch(IOException e) {
			return false;
		}
		return true;
	}

	private String fileName;
}
