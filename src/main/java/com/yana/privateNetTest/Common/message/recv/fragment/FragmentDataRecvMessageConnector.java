package com.yana.privateNetTest.Common.message.recv.fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class FragmentDataRecvMessageConnector {
	static byte[] getConnectedData(List<FragmentDataRecvMessage> fragmentDataRecvMessages) {
		List<byte[]> tmpList = fragmentDataRecvMessages.stream()
				.sorted(Comparator.comparing(FragmentDataRecvMessage::getFragmentIndex))
				.map(e -> e.getFragmentBytesData())
				.collect(Collectors.toList());
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			for(byte[] writeData : tmpList) {
				bos.write(writeData);
			};
			bos.flush();
			return bos.toByteArray();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
}
