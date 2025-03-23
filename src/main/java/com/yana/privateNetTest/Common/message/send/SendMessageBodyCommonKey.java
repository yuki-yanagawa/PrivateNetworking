package com.yana.privateNetTest.Common.message.send;

import java.util.Base64;

import com.yana.privateNetTest.Common.message.MessageDefinition;

class SendMessageBodyCommonKey {
	static final String SETTING_SEPARATOR = ":";
	static String createBodyCommonKey(byte[] encriptedCommonKey) {
		String base64BodyStr = Base64.getEncoder().encodeToString(encriptedCommonKey);
		return (MessageDefinition.BODY_COMMON_KEY + SETTING_SEPARATOR + base64BodyStr);
	}

	static String createBodyCommonVector(byte[] encriptedCommonVecotor) {
		String base64BodyStr = Base64.getEncoder().encodeToString(encriptedCommonVecotor);
		return (MessageDefinition.BODY_COMMON_VECTOR + SETTING_SEPARATOR + base64BodyStr);
	}

	static String createBodyCommonAlogrithm(byte[] encriptedCommonAlgorithm) {
		String base64BodyStr = Base64.getEncoder().encodeToString(encriptedCommonAlgorithm);
		return (MessageDefinition.BODY_COMMON_ALGORITHM + SETTING_SEPARATOR + base64BodyStr);
	}
}
