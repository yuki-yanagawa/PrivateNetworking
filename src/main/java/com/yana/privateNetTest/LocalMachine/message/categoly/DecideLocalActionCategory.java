package com.yana.privateNetTest.LocalMachine.message.categoly;

import com.yana.privateNetTest.Common.message.MessageDefinition;

public class DecideLocalActionCategory {
	public static LocalActionCategory decideAction(String messageHeader) {
		if(MessageDefinition.ACK_JOIN_HELLO.equals(messageHeader.trim())) {
			return LocalActionCategory.REQ_REGIST_ME;
		}
		if(MessageDefinition.ACK_REGIST_YOU.equals(messageHeader.trim())) {
			return LocalActionCategory.HANDSHAKE_COMLETE;
		}
		if(MessageDefinition.COMMON_PRIVE.equals(messageHeader.trim())) {
			return LocalActionCategory.COMMON_COMMUNICATE;
		}

		if(MessageDefinition.ACK_ACTIVE_USER_LIST.equals(messageHeader.trim())) {
			return LocalActionCategory.DISPLAY_ACTIVE_USER;
		}
		if(MessageDefinition.ACK_HELLO_MYNAMEIS.equals(messageHeader.trim())) {
			return LocalActionCategory.REGIST_CONNECTED_ACTIVE_USER;
		}

		if(MessageDefinition.REQ_HELLO_MYNAMEIS.equals(messageHeader.trim())) {
			return LocalActionCategory.ACK_HELLO_MYNAMEIS;
		}
		if(MessageDefinition.REQ_LIST_ALL.equals(messageHeader.trim())) {
			return LocalActionCategory.ACK_LIST_ALL;
		}
		if(MessageDefinition.ACK_LIST_ALL.equals(messageHeader.trim())) {
			return LocalActionCategory.DISPLAY_RESPONSE_LIST_ALL;
		}
		if(MessageDefinition.REQ_YOUR_DATA.equals(messageHeader.trim())) {
			return LocalActionCategory.ACK_MY_DATA;
		}
		if(MessageDefinition.ACK_FAILED.equals(messageHeader.trim())) {
			return LocalActionCategory.NOTIFY_REQESUT_FAILED;
		}
		if(MessageDefinition.ACK_MY_DATA.equals(messageHeader.trim())) {
			return LocalActionCategory.COLLECT_ACK_DATA;
		}
		return LocalActionCategory.NONE;
	}
}
