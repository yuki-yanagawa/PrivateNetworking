package com.yana.privateNetTest.CentralRouter.prop;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class RouterProperties {
	private static final Path FILE_PATH = Paths.get("centralrouter/conf/centralrouter.properties");
	private static Properties prop = new Properties();

	public static synchronized boolean refresh() {
		try(FileInputStream fis = new FileInputStream(FILE_PATH.toFile())) {
			prop.load(fis);
		} catch(IOException e) {
			return false;
		}
		return true;
	}

	public static String getSettingValue(String key) {
		Object obj = prop.get(key);
		if(obj != null) {
			return obj.toString();
		}
		return null;
	}

	public static synchronized void setProperty(String key, String value) {
		prop.setProperty(key, value);
	}

	public static synchronized void writePropertiesToFile() {
		try(FileOutputStream fos = new FileOutputStream(FILE_PATH.toFile())) {
			prop.store(fos, "UPDATE PROPERTIES");
		} catch(IOException e) {
		}
	}
}
