package com.yana.privateNetTest.CentralRouter.message.creator;

import java.util.List;

import com.yana.privateNetTest.CentralRouter.stored.activeuser.ActiveUserInfo;
import com.yana.privateNetTest.CentralRouter.stored.activeuser.StoredActiveUser;
import com.yana.privateNetTest.Common.message.send.SendMessageCreator;

public class AckActiveUserListMessageCreator {
	public static byte[] createMessage() {
		List<ActiveUserInfo> userInfos = StoredActiveUser.getActiveUserList();
		StringBuffer sb = new StringBuffer();
		for(ActiveUserInfo userInfo : userInfos) {
			sb.append(userInfo.getConnectingAddr() + ",");
		}
		String activeUserMessage = sb.substring(0, sb.length() - 1);
		return SendMessageCreator.ackActiveUserList(activeUserMessage);
	}
}
