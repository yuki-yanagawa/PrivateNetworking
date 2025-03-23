package com.yana.privateNetTest.CentralRouter.main;

import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.yana.privateNetTest.CentralRouter.key.RouterStoredKey;
import com.yana.privateNetTest.CentralRouter.message.catogoly.DecideRouterActionCategory;
import com.yana.privateNetTest.CentralRouter.message.catogoly.RouterActionCategory;
import com.yana.privateNetTest.CentralRouter.message.creator.AckActiveUserListMessageCreator;
import com.yana.privateNetTest.CentralRouter.message.encrypt.CommonCommunicateEncrypt;
import com.yana.privateNetTest.CentralRouter.stored.activeuser.StoredActiveUser;
import com.yana.privateNetTest.Common.charDef.CharCodeDefnition;
import com.yana.privateNetTest.Common.connectingHandshake.ConnectineHandShake;
import com.yana.privateNetTest.Common.message.MessageDefinition;
import com.yana.privateNetTest.Common.message.recv.RecvMessage;
import com.yana.privateNetTest.Common.message.recv.RecvMessageQueue;
import com.yana.privateNetTest.Common.socket.SenderWrapSocket;

class RouterRequestMessageAnalyzerThreadStart  extends Thread {
	private SenderWrapSocket senderWrapSocket;
	RouterRequestMessageAnalyzerThreadStart(SenderWrapSocket senderWrapSocket) {
		this.senderWrapSocket = senderWrapSocket;
	}
	@Override
	public void run() {
		while(true) {
			RecvMessage recvMessage = RecvMessageQueue.tryTake();
			byte[] messageData = recvMessage.getMessageData();
			SocketAddress address = recvMessage.getSocketAddress();
			recvMessage.clearParameter();
			analyzeMessage(messageData, address);
		}
	}

	private void analyzeMessage(byte[] messageData, SocketAddress address) {
		System.out.println("GET REQUEST MESS. Thread = " + Thread.currentThread().getName());
		String[] requestMessLine = new String(messageData, CharCodeDefnition.MESSAGE_CHARSET)
				.split(CharCodeDefnition.MESSAGE_LINE_SEPARATOR);
		String requestHeader = requestMessLine[0].trim();
		Map<String, Object> reqParameter = new HashMap<>();
		for(int i = 1; i < requestMessLine.length; i++) {
			String[] inner = requestMessLine[i].split(":");
			reqParameter.put(inner[0].trim(), inner[1].trim());
		}
		RouterActionCategory action = DecideRouterActionCategory.decideCategory(requestHeader);
		responseMessageByAction(reqParameter, action, address);
	}

	private void responseMessageByAction(Map<String, Object> reqParameter, RouterActionCategory action, SocketAddress destSocketAddress) {
		switch(action) {
		case ACK_JOIN_HELLO:
			// Path CertFile
			Path serverCertPath = Paths.get("centralrouter/keydir/jiko.crt");
			senderWrapSocket.setDestSocketAddress(destSocketAddress);
			ConnectineHandShake.sendMessageAckJoinHello(senderWrapSocket, serverCertPath);
			break;
		case ACK_REGIST_YOU:
			String bodyBase64Str =  (String)reqParameter.get(MessageDefinition.BODY);
			String bodySignBase64Str =  (String)reqParameter.get(MessageDefinition.BODY_SIGN);
			byte[] userPubKeyBytes = Base64.getDecoder().decode(bodyBase64Str);
			byte[] signBytes = Base64.getDecoder().decode(bodySignBase64Str);
			try {
				X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(userPubKeyBytes);
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				PublicKey userPubKey = keyFactory.generatePublic(x509EncodedKeySpec);
				Signature signature = Signature.getInstance("SHA256withRSA");
				signature.initVerify(userPubKey);
				signature.update(userPubKeyBytes);
				if(!signature.verify(signBytes)) {
					return;
				}
				Cipher cipher = Cipher.getInstance(userPubKey.getAlgorithm());
				cipher.init(Cipher.ENCRYPT_MODE, userPubKey);
				byte[] encriptedSecurityKey = cipher.doFinal(RouterStoredKey.getSecretKey().getEncoded());
				byte[] encriptedVectorKeyWord = cipher.doFinal(RouterStoredKey.getVectorKeyWord().getBytes(CharCodeDefnition.CIPHER_VECTOR));
				byte[] encriptedAlgorthm = cipher.doFinal(RouterStoredKey.getCipherAlogrithm().getBytes(CharCodeDefnition.MESSAGE_CHARSET));
				senderWrapSocket.setDestSocketAddress(destSocketAddress);
				ConnectineHandShake.sendMessageAckRegistYou(senderWrapSocket, encriptedSecurityKey, encriptedVectorKeyWord, encriptedAlgorthm);

				//UserInfo regist
				StoredActiveUser.registerActiveUserInfo(destSocketAddress);
			} catch(Exception e) {
				e.printStackTrace();
				return;
			}
			break;
		case COMMON_COMMUNICATE:
			readInnerActionCategory(reqParameter, destSocketAddress);
			break;
		//Inner Action
		case ACK_ACTIVE_USER_LIST:
			byte[] innerMessage = AckActiveUserListMessageCreator.createMessage();
			byte[] sendMessage = CommonCommunicateEncrypt.cycriptMessage(innerMessage);
			senderWrapSocket.setDestSocketAddress(destSocketAddress);
			senderWrapSocket.sendMessage(sendMessage);
			break;
		case NONE:
			break;
		default:
			break;
		}
	}

	private void readInnerActionCategory(Map<String, Object> reqParameter, SocketAddress destSocketAddress) {
		String bodyBase64Str =  (String)reqParameter.get(MessageDefinition.BODY);
		byte[] encriptedMessage = Base64.getDecoder().decode(bodyBase64Str);
		SecretKey secKey = RouterStoredKey.getSecretKey();
		Cipher commonCipher = RouterStoredKey.getCommonCipher();
		IvParameterSpec vectorParamSpec = RouterStoredKey.getVectorParam();
		try {
			commonCipher.init(Cipher.DECRYPT_MODE, secKey, vectorParamSpec);
			byte[] message = commonCipher.doFinal(encriptedMessage);
			String[] requestMessLine = new String(message, CharCodeDefnition.MESSAGE_CHARSET)
					.split(CharCodeDefnition.MESSAGE_LINE_SEPARATOR);
			Map<String, Object> reqNewParameter = new HashMap<>();
			for(int i = 1; i < requestMessLine.length; i++) {
				String[] inner = requestMessLine[i].split(":");
				reqNewParameter.put(inner[0].trim(), inner[1].trim());
			}
			String requestHeader = requestMessLine[0].trim();
			RouterActionCategory action = DecideRouterActionCategory.decideCategory(requestHeader);
			responseMessageByAction(reqNewParameter, action, destSocketAddress);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
