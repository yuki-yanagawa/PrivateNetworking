package com.yana.privateNetTest.Common.message.send;

class SendMessageGzipCommunication {
	static final String SETTING_SEPARATOR = ":";

	static String GZIP_ADJUT = "GZIPADJUST";

	static String GZIP_ADJUT_OK = "OK";
	static String GZIP_ADJUT_NO = "NO";

	static String creatAdjustOK() {
		return GZIP_ADJUT + SETTING_SEPARATOR + GZIP_ADJUT_OK;
	}
}
