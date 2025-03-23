package com.yana.privateNetTest.Common.def;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MessageHeaderDef {
	private final static Path FILE_PATH = Paths.get("messageDef/messageDef.csv");
	private static List<String> reqHeaderKey = new ArrayList<>();
	private static List<String> ackHeaderKey = new ArrayList<>();
	public static synchronized boolean readMessageDef() {
		reqHeaderKey.clear();
		ackHeaderKey.clear();
		try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH.toFile()))) {
			String readData;
			while((readData = br.readLine()) != null) {
				if(readData.trim().startsWith("#")) {
					continue;
				}
				String[] mesDefLine = readData.split(",");
				if("0".equals(mesDefLine[0].trim())) {
					reqHeaderKey.add(mesDefLine[1].trim());
				} else if("1".equals(mesDefLine[0].trim())) {
					ackHeaderKey.add(mesDefLine[1].trim());
				} else {
					//noting
				}
			}
			return true;
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
