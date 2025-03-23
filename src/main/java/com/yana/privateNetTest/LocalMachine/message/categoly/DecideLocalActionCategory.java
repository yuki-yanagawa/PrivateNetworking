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
		if(MessageDefinition.ACK_COMMON.equals(messageHeader.trim())) {
			return LocalActionCategory.COMMON_COMMUNICATE;
		}

		if(MessageDefinition.ACK_ACTIVE_USER_LIST.equals(messageHeader.trim())) {
			return LocalActionCategory.DISPLAY_ACTIVE_USER;
		}
		return LocalActionCategory.NONE;
	}
}
