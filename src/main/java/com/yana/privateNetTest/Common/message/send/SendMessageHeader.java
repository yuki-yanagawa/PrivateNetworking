package com.yana.privateNetTest.Common.message.send;

import com.yana.privateNetTest.Common.message.MessageDefinition;

class SendMessageHeader {
	static final String COMMON_PRIVE = MessageDefinition.COMMON_PRIVE;

	static final String REQ_JOIN_HELLO = MessageDefinition.REQ_JOIN_HELLO;
	static final String REQ_REGIST_ME = MessageDefinition.REQ_REGIST_ME;

	
	static final String REQ_LIST_ALL = MessageDefinition.REQ_LIST_ALL;
	static final String REQ_ACTIVE_USER_LIST = MessageDefinition.REQ_ACTIVE_USER_LIST;
	static final String REQ_HELLO_MYNAMEIS = MessageDefinition.REQ_HELLO_MYNAMEIS; 
	static final String REQ_YOUR_DATA = MessageDefinition.REQ_YOUR_DATA;

	static final String ACK_JOIN_HELLO = MessageDefinition.ACK_JOIN_HELLO;
	static final String ACK_REGIST_YOU = MessageDefinition.ACK_REGIST_YOU;


	static final String ACK_ACTIVE_USER_LIST = MessageDefinition.ACK_ACTIVE_USER_LIST;
	static final String ACK_HELLO_MYNAMEIS = MessageDefinition.ACK_HELLO_MYNAMEIS;
	static final String ACK_LIST_ALL = MessageDefinition.ACK_LIST_ALL;
	static final String ACK_MY_DATA = MessageDefinition.ACK_MY_DATA;
	static final String ACK_FAILED = MessageDefinition.ACK_FAILED;
}
