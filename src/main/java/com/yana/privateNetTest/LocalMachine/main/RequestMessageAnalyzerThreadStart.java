package com.yana.privateNetTest.LocalMachine.main;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.yana.privateNetTest.Common.charDef.CharCodeDefnition;
import com.yana.privateNetTest.Common.connectingHandshake.ConnectineHandShake;
import com.yana.privateNetTest.Common.message.MessageDefinition;
import com.yana.privateNetTest.Common.message.recv.RecvMessage;
import com.yana.privateNetTest.Common.message.recv.RecvMessageQueue;
import com.yana.privateNetTest.Common.message.send.SendMessageCreator;
import com.yana.privateNetTest.Common.micromodel.ActorRef;
import com.yana.privateNetTest.Common.micromodel.LookUpActor;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.communicate.cycript.CommunicateCycript;
import com.yana.privateNetTest.LocalMachine.communicate.key.GetRouterPublickKeyFromCert;
import com.yana.privateNetTest.LocalMachine.communicate.key.KeyStoredManager;
import com.yana.privateNetTest.LocalMachine.console.ConsoleActor;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOutputMessage;
import com.yana.privateNetTest.LocalMachine.exchange.recv.ExchangeDirCollectInfo;
import com.yana.privateNetTest.LocalMachine.exchange.recv.ExchangeEnableFile;
import com.yana.privateNetTest.LocalMachine.exchange.send.CollectIngDataInfo;
import com.yana.privateNetTest.LocalMachine.handshake.CentralRouterHandShakeState;
import com.yana.privateNetTest.LocalMachine.logger.LocalMachineLogger;
import com.yana.privateNetTest.LocalMachine.message.categoly.DecideLocalActionCategory;
import com.yana.privateNetTest.LocalMachine.message.categoly.LocalActionCategory;
import com.yana.privateNetTest.LocalMachine.myInfo.MyInfoCache;

class RequestMessageAnalyzerThreadStart extends Thread {
	private SenderWrapSocket senderWrapSocket;
	RequestMessageAnalyzerThreadStart(SenderWrapSocket senderWrapSocket) {
		this.senderWrapSocket = senderWrapSocket;
	}

	@Override
	public void run() {
		while(true) {
			RecvMessage recvMessage = RecvMessageQueue.tryTake();
			byte[] messageData = recvMessage.getMessageData();
			SocketAddress address = recvMessage.getSocketAddress();
			recvMessage.clearParameter();
			anlyzeMessage(messageData, address);
		}
	}

	private void anlyzeMessage(byte[] messageData, SocketAddress address) {
		String[] rawMessageLine = new String(messageData, CharCodeDefnition.MESSAGE_CHARSET)
				.split(CharCodeDefnition.MESSAGE_LINE_SEPARATOR);
		String messageHeader = rawMessageLine[0].trim();
		Map<String, Object> reqParameter = new HashMap<>();
		for(int i = 1; i < rawMessageLine.length; i++) {
			String[] inner = rawMessageLine[i].split(":");
			reqParameter.put(inner[0].trim(), inner[1].trim());
		}
		LocalActionCategory action = DecideLocalActionCategory.decideAction(messageHeader);
		responseMessageByAction(reqParameter, action, address);
	}

	private void responseMessageByAction(Map<String, Object> reqParameter, LocalActionCategory action, SocketAddress destSocketAddress) {
		switch(action) {
		case REQ_REGIST_ME:
			Optional<PublicKey> publicKeyOpt = getFromReqBodyCert(reqParameter);
			if(!publicKeyOpt.isPresent()) {
				return;
			}
			PublicKey publicKey = publicKeyOpt.get();
			try {
				byte[] bodyBytes = KeyStoredManager.getPublicKey().getEncoded();
				Signature sign = Signature.getInstance("SHA256withRSA");
				sign.initSign(KeyStoredManager.getPrivateKey());
				sign.update(bodyBytes);
				byte[] signBodyes = sign.sign();
				senderWrapSocket.setDestSocketAddress(destSocketAddress);
				ConnectineHandShake.sendMessageReqRegsitMe(senderWrapSocket, bodyBytes, signBodyes);
			} catch(Exception e) {
				e.printStackTrace();
			}
			return;
		case HANDSHAKE_COMLETE:
			handShakeCompleteAction(reqParameter);
			return;
		case COMMON_COMMUNICATE:
			LocalMachineLogger.info("COMMON_COMMUNICATE");
			readInnerActionCategory(reqParameter, destSocketAddress);
			return;

		// Inner communicate
		case DISPLAY_ACTIVE_USER:
			LocalMachineLogger.info("DISPLAY_ACTIVE_USER");
			String activeUserList = (String)reqParameter.get(MessageDefinition.BODY);
			outputMessage(activeUserList);
			return;
		case REGIST_CONNECTED_ACTIVE_USER:
			LocalMachineLogger.info("REGIST_CONNECTED_ACTIVE_USER");
			String connectedUsername = (String)reqParameter.get(MessageDefinition.BODY);
			MyInfoCache.setConnectedUser(destSocketAddress, connectedUsername);
			outputMessage("conneceted active user info; name=" + connectedUsername + "/addr=" + destSocketAddress);
			break;
		case ACK_HELLO_MYNAMEIS:
			LocalMachineLogger.info("ACK_HELLO_MYNAMEIS");
//			byte[] ackHelloMess = CommunicateCycript.cycriptMessage(SendMessageCreator.ackHelloMyNameIs(MyInfoCache.getName()));
//			senderWrapSocket.setDestSocketAddress(destSocketAddress);
//			senderWrapSocket.sendMessage(SendMessageCreator.commonPrive(ackHelloMess));
			sendMessageP2P(SendMessageCreator.ackHelloMyNameIs(MyInfoCache.getName()), destSocketAddress);
			break;
		case ACK_LIST_ALL:
			LocalMachineLogger.info("ACK_LIST_ALL");
			byte[] tmpMesAllList = createMyAllFileList();
			if(tmpMesAllList.length == 0) {
				sendMessageP2P(failedResponseMessage(), destSocketAddress);
				break;
			}
			sendMessageP2P(tmpMesAllList, destSocketAddress);
			break;
		case DISPLAY_RESPONSE_LIST_ALL:
			LocalMachineLogger.info("DISPLAY_RESPONSE_LIST_ALL");
			outputMessage((String)reqParameter.get(MessageDefinition.BODY));
		case ACK_MY_DATA:
			LocalMachineLogger.info("ACK_MY_DATA");
			String reqDataName = (String)reqParameter.get(MessageDefinition.BODY);
			byte[] tmpReqDataMess = searcRequestData(reqDataName);
			if(tmpReqDataMess.length == 0) {
				sendMessageP2P(failedResponseMessage(), destSocketAddress);
				break;
			}
			sendMessageP2P(tmpReqDataMess, destSocketAddress);
			break;
		case COLLECT_ACK_DATA:
			LocalMachineLogger.info("COLLECT_ACK_DATA");
			String tmpResponseData = (String)reqParameter.get(MessageDefinition.BODY);
			if(collectFileData(tmpResponseData)) {
				outputMessage("requset data getting success");
			} else {
				outputMessage("requset data getting failed");
			}
			break;
		case NOTIFY_REQESUT_FAILED:
			outputMessage("request failed...");
			break;
		case NONE:
			LocalMachineLogger.info("NONE");
			break;
		}
	}

	private void sendMessageP2P(byte[] message, SocketAddress destSocketAddress) {
		byte[] sendMess = CommunicateCycript.cycriptMessage(message);
		senderWrapSocket.setDestSocketAddress(destSocketAddress);
		senderWrapSocket.sendMessage(SendMessageCreator.commonPrive(sendMess));
	}

	private byte[] failedResponseMessage() {
		return SendMessageCreator.ackFailed();
	}

	private byte[] searcRequestData(String reqDataName) {
		if(!ExchangeDirCollectInfo.searchFileName(reqDataName)) {
			return new byte[0];
		}
		ExchangeEnableFile targetFile = ExchangeDirCollectInfo.getTargetFileInfo(reqDataName);
		if(targetFile.getFileSize() > Integer.MAX_VALUE) {
			return new byte[0];
		}
		byte[] readFileData = new byte[(int)targetFile.getFileSize()];
		try(FileInputStream fis = new FileInputStream(targetFile.getFilePath().toFile())) {
			fis.read(readFileData);
		} catch(IOException e) {
			return new byte[0];
		}
		return SendMessageCreator.ackMyData(readFileData);
	}

	private boolean collectFileData(String data) {
		byte[] dataBytes = Base64.getDecoder().decode(data);
		return CollectIngDataInfo.writeData(dataBytes);
	}

	private byte[] createMyAllFileList() {
		List<ExchangeEnableFile> list = ExchangeDirCollectInfo.getEnableExchangeAllList();
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			for(ExchangeEnableFile f : list) {
				bos.write(SendMessageCreator.ackFileNameLine(f.getFilePath().toFile().getName(), f.getFileSize()));
			}
			return SendMessageCreator.ackFileName(bos.toByteArray());
		} catch(IOException e) {
			return new byte[0];
		}
	}

	private Optional<PublicKey> getFromReqBodyCert(Map<String, Object> reqParameter) {
		String bodyBase64Str =  (String)reqParameter.get(MessageDefinition.BODY);
		byte[] certBytes = Base64.getDecoder().decode(bodyBase64Str);
		return GetRouterPublickKeyFromCert.getPublicKey(certBytes);
	}

	private void handShakeCompleteAction(Map<String, Object> reqParameter) {
		String bodyBase64Str =  (String)reqParameter.get(MessageDefinition.BODY_COMMON_KEY);
		String bodyBase64VectorStr =  (String)reqParameter.get(MessageDefinition.BODY_COMMON_VECTOR);
		String bodyBase64AlogoStr =  (String)reqParameter.get(MessageDefinition.BODY_COMMON_ALGORITHM);
		byte[] encriptedBytes = Base64.getDecoder().decode(bodyBase64Str);
		byte[] encriptedVecotorBytes = Base64.getDecoder().decode(bodyBase64VectorStr);
		byte[] encriptedAlogoBytes  = Base64.getDecoder().decode(bodyBase64AlogoStr);
		PrivateKey privateKey = KeyStoredManager.getPrivateKey();
		try {
			Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] secretKeyBytes = cipher.doFinal(encriptedBytes);
			byte[] vectorBytes = cipher.doFinal(encriptedVecotorBytes);
			byte[] algoBytes = cipher.doFinal(encriptedAlogoBytes);
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");
			IvParameterSpec vectorParamSpec = new IvParameterSpec(vectorBytes);
			String cipherAlgorithm = new String(algoBytes, CharCodeDefnition.MESSAGE_CHARSET);
			KeyStoredManager.registCommonEncryptKey(secretKeySpec);
			KeyStoredManager.registVectorParamSpec(vectorParamSpec);
			KeyStoredManager.registCipherAlgorithm(cipherAlgorithm);
			CentralRouterHandShakeState.setCompleteHandShake();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void readInnerActionCategory(Map<String, Object> reqParameter, SocketAddress destSocketAddress) {
		String bodyBase64Str =  (String)reqParameter.get(MessageDefinition.BODY);
		byte[] encriptedMessage = Base64.getDecoder().decode(bodyBase64Str);
		SecretKeySpec secKey = KeyStoredManager.getSecretKeySpec();
		IvParameterSpec vector = KeyStoredManager.getVecrotSpecParam();
		Cipher commonCipher = KeyStoredManager.getCommonCipher();
		try {
			commonCipher.init(Cipher.DECRYPT_MODE, secKey, vector);
			byte[] message = commonCipher.doFinal(encriptedMessage);
			String[] requestMessLine = new String(message, CharCodeDefnition.MESSAGE_CHARSET)
					.split(CharCodeDefnition.MESSAGE_LINE_SEPARATOR);
			Map<String, Object> reqNewParameter = new HashMap<>();
			for(int i = 1; i < requestMessLine.length; i++) {
				int index = requestMessLine[i].indexOf(":");
				reqNewParameter.put(requestMessLine[i].substring(0, index), requestMessLine[i].substring(index + 1));
			}
			String requestHeader = requestMessLine[0].trim();
			LocalActionCategory action = DecideLocalActionCategory.decideAction(requestHeader);
			responseMessageByAction(reqNewParameter, action, destSocketAddress);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void outputMessage(String message) {
		ActorRef actor = LookUpActor.getActorRef(ConsoleActor.class);
		actor.tell(new ConsoleOutputMessage(message));
	}
}
