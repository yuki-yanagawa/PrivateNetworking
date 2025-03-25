package com.yana.privateNetTest.Common.message;

public class MessageDefinition {
	//REQ
	public static final String REQ_JOIN_HELLO = "REQ_JOIN_HELLO";
	public static final String REQ_REGIST_ME = "REQ_REGIST_ME";
	//ACK
	public static final String ACK_JOIN_HELLO = "ACK_JOIN_HELLO";
	public static final String ACK_REGIST_YOU = "ACK_REGIST_YOU";

	public static final String COMMON_PRIVE = "COMMON_PRIVE";
	//REQ_COMMON_PRIVE
	public static final String REQ_ACTIVE_USER_LIST = "REQ_ACTIVE_USER_LIST";
	public static final String REQ_HELLO_MYNAMEIS = "REQ_HELLO_MYNAMEIS";
	public static final String REQ_LIST_ALL = "REQ_LIST_ALL";

	//ACK COMMON INNER
	public static final String ACK_ACTIVE_USER_LIST = "ACK_ACTIVE_USER_LIST";
	public static final String ACK_HELLO_MYNAMEIS = "ACK_HELLO_MYNAMEIS";

	//SETTING SEPARATOR
	public static final String SETTING_SEPARATOR = ":";

	//BODY SETTING
	public static final String BODY_LENGTH = "BODY_LENGTH";
	public static final String BODY = "BODY";
	public static final String BODY_SIGN = "BODY_SIGN";

	//COMMONKEY
	public static final String BODY_COMMON_KEY = "BODY_COMMON_KEY";
	public static final String BODY_COMMON_VECTOR = "BODY_COMMON_VECTOR";
	public static final String BODY_COMMON_ALGORITHM = "BODY_COMMON_ALGORITHM";

	//FRAGMENT SETTING
	public static final String FRAGMNET_ID = "FRAGMENT_ID";
	public static final String FRAGMENT_DEVIDE_SIZE = "FRAGMENT_DEVIDE_SIZE";
	public static final String FRAGMENT_INDEX = "FRAGMENT_INDEX";
}
