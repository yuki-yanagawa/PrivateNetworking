package com.yana.privateNetTest.Common.message.send;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.yana.privateNetTest.Common.charDef.CharCodeDefnition;

public class SendMessageCreator {
	private static final String LINE_SEPARATOR = CharCodeDefnition.MESSAGE_LINE_SEPARATOR;
	private static final Charset CHARSET = CharCodeDefnition.MESSAGE_CHARSET;

	public static byte[] reqClientHello() {
		return (SendMessageHeader.REQ_JOIN_HELLO + LINE_SEPARATOR).getBytes(CHARSET);
	}

	public static byte[] reqClientHello(boolean gzipAction) {
		if(!gzipAction) {
			return reqClientHello();
		}
		return (SendMessageHeader.REQ_JOIN_HELLO + LINE_SEPARATOR
				+ SendMessageGzipCommunication.creatAdjustOK() + LINE_SEPARATOR).getBytes(CHARSET);
	}

	public static byte[] ackServerHello(byte[] serverCertBytes) {
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			bos.write((SendMessageHeader.ACK_JOIN_HELLO + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBody.createBodyLength(serverCertBytes) + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBody.createBodyData(serverCertBytes) + LINE_SEPARATOR).getBytes(CHARSET));
			//bos.write(serverCertBytes);
			bos.flush();
			return bos.toByteArray();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public static byte[] ackServerHello(byte[] serverCertBytes, int flagmentNo) {
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			bos.write((SendMessageHeader.ACK_JOIN_HELLO + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBody.createBodyLength(serverCertBytes) + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBody.createBodyData(serverCertBytes) + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write(serverCertBytes);
			bos.flush();
			return bos.toByteArray();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public static byte[] reqResitMe(byte[] bodyData, byte[] bodySignData) {
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			bos.write((SendMessageHeader.REQ_REGIST_ME + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBody.createBodyLength(bodyData) + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBody.createBodyData(bodyData) + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBody.createBodySignData(bodySignData) + LINE_SEPARATOR).getBytes(CHARSET));
			bos.flush();
			return bos.toByteArray();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public static byte[] ackRegistYou(byte[] commonSecretKeyEncripted, byte[] encriptedVectorKeyWord, byte[] encriptedAlgorthm) {
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			bos.write((SendMessageHeader.ACK_REGIST_YOU + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBodyCommonKey.createBodyCommonKey(commonSecretKeyEncripted) + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBodyCommonKey.createBodyCommonVector(encriptedVectorKeyWord) + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBodyCommonKey.createBodyCommonAlogrithm(encriptedAlgorthm) + LINE_SEPARATOR).getBytes(CHARSET));
			bos.flush();
			return bos.toByteArray();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public static byte[] reqActiveUserList() {
		return (SendMessageHeader.REQ_ACTIVE_USER_LIST + LINE_SEPARATOR).getBytes(CHARSET);
	}

	public static byte[] ackActiveUserList(String activeUserList) {
		return (SendMessageHeader.ACK_ACTIVE_USER_LIST + LINE_SEPARATOR + 
				SendMessageBody.createBodyData(activeUserList) + LINE_SEPARATOR).getBytes(CHARSET);
	}

	public static byte[] commonPrive(byte[] cycriptedMessage) {
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			bos.write((SendMessageHeader.COMMON_PRIVE + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBody.createBodyLength(cycriptedMessage) + LINE_SEPARATOR).getBytes(CHARSET));
			bos.write((SendMessageBody.createBodyData(cycriptedMessage) + LINE_SEPARATOR).getBytes(CHARSET));
			return bos.toByteArray();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public static byte[] reqHelloMyNameIs(String name) {
		return (SendMessageHeader.REQ_HELLO_MYNAMEIS + LINE_SEPARATOR +
				SendMessageBody.createBodyData(name)).getBytes(CHARSET);
	}

	public static byte[] ackHelloMyNameIs(String name) {
		return (SendMessageHeader.ACK_HELLO_MYNAMEIS + LINE_SEPARATOR + 
				SendMessageBody.createBodyData(name)).getBytes(CHARSET);
	}

	public static byte[] reqListAllData() {
		return (SendMessageHeader.REQ_LIST_ALL + LINE_SEPARATOR).getBytes();
	}

	public static byte[] ackFileNameLine(String fileName, long size) {
		return SendMessageFileData.createFileInfoLine(fileName, size, LINE_SEPARATOR, CHARSET);
	}

	public static byte[] ackFileName(byte[] fileNameLines) {
		return (SendMessageHeader.ACK_LIST_ALL + LINE_SEPARATOR + 
				SendMessageBody.createBodyData(fileNameLines) + LINE_SEPARATOR).getBytes(CHARSET);
	}

	public static byte[] reqYourData(String fileName) {
		return (SendMessageHeader.REQ_YOUR_DATA + LINE_SEPARATOR + 
				SendMessageBody.createBodyData(fileName)).getBytes(CHARSET);
	}

	public static byte[] ackMyData(byte[] fileData) {
		return (SendMessageHeader.ACK_MY_DATA + LINE_SEPARATOR + 
				SendMessageBody.createBodyData(fileData) + LINE_SEPARATOR).getBytes(CHARSET);
	}

	public static byte[] ackFailed() {
		return (SendMessageHeader.ACK_FAILED + LINE_SEPARATOR).getBytes(CHARSET);
	}
}
