package com.yana.privateNetTest.Common.message.recv.fragment;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FragmentDataRecvMessageStore {
	private static class FragmetnCommonData {
		private final int fragmentSize;
		private final SocketAddress clientAddr;
		FragmetnCommonData(int fragmentSize, SocketAddress clientAddr) {
			this.fragmentSize = fragmentSize;
			this.clientAddr = clientAddr;
		}
	}
	private static FragmentDataRecvMessageStore recvMessStore = new FragmentDataRecvMessageStore();

	private Map<Integer, List<FragmentDataRecvMessage>> recvCollectMapByFragmentId = new HashMap<>();
	private Map<Integer, FragmetnCommonData> fragemntCommonDataByFragmentId = new HashMap<>();
	private FragmentDataRecvMessageStore() {
	}

	private synchronized void _storeRecvMessage(int fragementId, int fragmentSize, SocketAddress socketAddress, FragmentDataRecvMessage recvData) {
		if(!recvCollectMapByFragmentId.containsKey(fragementId)) {
			List<FragmentDataRecvMessage> list = new ArrayList<>();
			recvCollectMapByFragmentId.put(fragementId, list);
			fragemntCommonDataByFragmentId.put(fragementId, new FragmetnCommonData(fragmentSize, socketAddress));
		}
		recvCollectMapByFragmentId.get(fragementId).add(recvData);
	}

	static void storeRecvMessage(int fragementId, int fragmentSize, SocketAddress socketAddress, FragmentDataRecvMessage recvData) {
		recvMessStore._storeRecvMessage(fragementId, fragmentSize, socketAddress, recvData);
	}

	private synchronized List<FragmentDataRecvMessage> _getRecvMessList(int fragmentId) {
		return recvCollectMapByFragmentId.get(fragmentId);
	}

	private synchronized List<FragmentDataRecvMessage> _removeRecvMessList(int fragmentId) {
		return recvCollectMapByFragmentId.remove(fragmentId);
	}

	private int getFragmnetSize(int fragmentId) {
		return fragemntCommonDataByFragmentId.get(fragmentId).fragmentSize;
	}

	static boolean checkComplete(int fragmentId) {
		List<FragmentDataRecvMessage> recvMessages = recvMessStore._getRecvMessList(fragmentId);
		if(recvMessages == null) {
			return false;
		}
		if(recvMessStore.getFragmnetSize(fragmentId) == recvMessages.size()) {
			return true;
		}
		return false;
	}

	static List<FragmentDataRecvMessage> removeColletedFragmentData(int fragmentId) {
		return recvMessStore._removeRecvMessList(fragmentId);
	}

	static SocketAddress removeStoredSocketAddress(int fragmentId) {
		return recvMessStore.fragemntCommonDataByFragmentId.remove(fragmentId).clientAddr;
	}
}
