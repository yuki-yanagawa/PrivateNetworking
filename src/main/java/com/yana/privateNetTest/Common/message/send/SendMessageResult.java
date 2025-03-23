package com.yana.privateNetTest.Common.message.send;

class SendMessageResult {
	static final String SETTING_SEPARATOR = ":";

	static final String RESULT = "RESULT";

	static final String RESULT_OK = "OK";
	static final String RESULT_FAIL = "FAIL";

	static String resultOK() {
		return RESULT + SETTING_SEPARATOR + RESULT_OK;
	}

	static String resultFail() {
		return RESULT + SETTING_SEPARATOR + RESULT_FAIL;
	}
}
