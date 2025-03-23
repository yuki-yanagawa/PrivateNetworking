package com.yana.privateNetTest.Common.message.send;

import java.util.Base64;

import com.yana.privateNetTest.Common.message.MessageDefinition;

class SendMessageBody {
	static final String SETTING_SEPARATOR = ":";

	static String createBodyLength(byte[] bodyData) {
		return(MessageDefinition.BODY_LENGTH + SETTING_SEPARATOR + String.valueOf(bodyData.length));
	}
	static String createBodyData(byte[] bodyData) {
		String base64BodyStr = Base64.getEncoder().encodeToString(bodyData);
		return (MessageDefinition.BODY + SETTING_SEPARATOR + base64BodyStr);
	}

	static String createBodySignData(byte[] bodySignData) {
		String base64BodyStr = Base64.getEncoder().encodeToString(bodySignData);
		return (MessageDefinition.BODY_SIGN + SETTING_SEPARATOR + base64BodyStr);
	}

	static String createBodyData(String bodyData) {
		return (MessageDefinition.BODY + SETTING_SEPARATOR + bodyData);
	}
}
