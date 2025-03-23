package com.yana.privateNetTest.Common.message.recv.fragment;

import java.net.SocketAddress;
import java.util.List;

import com.yana.privateNetTest.Common.message.recv.RecvMessageQueue;
import com.yana.privateNetTest.Common.micromodel.IMessage;

public class FragmentDataRecvMessage implements IMessage {
	private final int fragmentId;
	private final int fragmentIndex;
	private final byte[] fragmentBytesData;
	//private Map<Integer, byte[]> fragmentDataByIndex = new ConcurrentHashMap<>();

	public FragmentDataRecvMessage(int fragmentId, int fragmentSize, int fragmentIndex, byte[] recvData, SocketAddress socketAddress) {
		this.fragmentId = fragmentId;
		this.fragmentIndex = fragmentIndex;
		this.fragmentBytesData = recvData;
		FragmentDataRecvMessageStore.storeRecvMessage(this.fragmentId, fragmentSize, socketAddress, this);
	}

	public int getFragmentIndex() {
		return fragmentIndex;
	}

	public byte[] getFragmentBytesData() {
		return fragmentBytesData;
	}

	@Override
	public void execute() {
		if(FragmentDataRecvMessageStore.checkComplete(this.fragmentId)) {
			List<FragmentDataRecvMessage> recvCollectedData = FragmentDataRecvMessageStore.removeColletedFragmentData(this.fragmentId);
			SocketAddress clientAddr = FragmentDataRecvMessageStore.removeStoredSocketAddress(fragmentId);
			byte[] recvData = FragmentDataRecvMessageConnector.getConnectedData(recvCollectedData);
			RecvMessageQueue.tryAdd(clientAddr, recvData);
		}
	}

}
