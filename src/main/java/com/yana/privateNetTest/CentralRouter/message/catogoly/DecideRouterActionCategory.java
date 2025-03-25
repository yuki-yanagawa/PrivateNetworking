package com.yana.privateNetTest.CentralRouter.message.catogoly;

import com.yana.privateNetTest.Common.message.MessageDefinition;

public class DecideRouterActionCategory {

	public static RouterActionCategory decideCategory(String messageHeader) {
		if(MessageDefinition.REQ_JOIN_HELLO.equals(messageHeader.trim())) {
			return RouterActionCategory.ACK_JOIN_HELLO;
		}
		if(MessageDefinition.REQ_REGIST_ME.equals(messageHeader.trim())) {
			return RouterActionCategory.ACK_REGIST_YOU;
		}
		if(MessageDefinition.COMMON_PRIVE.equals(messageHeader.trim())) {
			return RouterActionCategory.COMMON_COMMUNICATE;
		}

		//Innner Category
		if(MessageDefinition.REQ_ACTIVE_USER_LIST.equals(messageHeader.trim())) {
			return RouterActionCategory.ACK_ACTIVE_USER_LIST;
		}
		return RouterActionCategory.NONE;
	}
}
