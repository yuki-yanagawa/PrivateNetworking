package com.yana.privateNetTest.Common.message.send;

import java.nio.charset.Charset;

public class SendMessageFileData {
	static final String SETTING_SEPARATOR = ":";
	static final String FILE_NAME = "FILE_NAME";
	static final String FILE_SIZE = "FILE_SIZE";

	static byte[] createFileInfoLine(String fileName, long fileSize, String separator, Charset charset) {
		return ((FILE_NAME + SETTING_SEPARATOR + fileName) + separator 
				+ (FILE_SIZE + SETTING_SEPARATOR + String.valueOf(fileSize)) + separator).getBytes(charset);
	}
}
