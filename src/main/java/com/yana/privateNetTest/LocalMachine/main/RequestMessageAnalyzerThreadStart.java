package com.yana.privateNetTest.LocalMachine.main;

import java.net.SocketAddress;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.yana.privateNetTest.Common.charDef.CharCodeDefnition;
import com.yana.privateNetTest.Common.connectingHandshake.ConnectineHandShake;
import com.yana.privateNetTest.Common.message.MessageDefinition;
import com.yana.privateNetTest.Common.message.recv.RecvMessage;
import com.yana.privateNetTest.Common.message.recv.RecvMessageQueue;
import com.yana.privateNetTest.Common.message.recv.fragment.FragmentActor;
import com.yana.privateNetTest.Common.micromodel.ActorRef;
import com.yana.privateNetTest.Common.micromodel.LookUpActor;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;
import com.yana.privateNetTest.LocalMachine.communicate.key.GetRouterPublickKeyFromCert;
import com.yana.privateNetTest.LocalMachine.communicate.key.KeyStoredManager;
import com.yana.privateNetTest.LocalMachine.console.ConsoleActor;
import com.yana.privateNetTest.LocalMachine.console.ConsoleOutputMessage;
import com.yana.privateNetTest.LocalMachine.handshake.CentralRouterHandShakeState;
import com.yana.privateNetTest.LocalMachine.message.categoly.DecideLocalActionCategory;
import com.yana.privateNetTest.LocalMachine.message.categoly.LocalActionCategory;

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
			readInnerActionCategory(reqParameter, destSocketAddress);
			return;

		// Inner communicate
		case DISPLAY_ACTIVE_USER:
			String activeUserList = (String)reqParameter.get(MessageDefinition.BODY);
			ActorRef actor = LookUpActor.getActorRef(ConsoleActor.class);
			actor.tell(new ConsoleOutputMessage(activeUserList));
			return;
		case NONE:
			break;
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
				String[] inner = requestMessLine[i].split(":");
				if(inner.length > 2) {
					reqNewParameter.put(inner[0].trim(), inner[1].trim() + ":" + inner[2].trim());
				} else {
					reqNewParameter.put(inner[0].trim(), inner[1].trim());
				}
			}
			String requestHeader = requestMessLine[0].trim();
			LocalActionCategory action = DecideLocalActionCategory.decideAction(requestHeader);
			responseMessageByAction(reqNewParameter, action, destSocketAddress);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
