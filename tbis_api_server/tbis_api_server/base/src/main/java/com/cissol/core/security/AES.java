package com.cissol.core.security;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.log4j.Logger;

public class AES {

	private static SecretKeySpec secretKey;
//	private static Logger log = Logger.getLogger(AES.class.getName());

	public static String encodedString(String str, String encryptKey) {
		String resultEncodedString = "";
		try {
			String plainText = str;
			Base64.Encoder encoder = Base64.getEncoder();
			setKey(encryptKey);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encodedData = cipher.doFinal(plainText.getBytes());
			resultEncodedString = encoder.encodeToString(encodedData) + "~"
					+ encoder.encodeToString(cipher.getParameters().getEncoded());

		} catch (Exception e) {
			// log.info("Inside Encoded : " +e);
		}

		return resultEncodedString;
	}

	public static String decodedString(String encodedStr, String ivStr, String encryptKey) {
		String resultDecodedString = "";
		try {
			// String stringToDecode = "khlExYKYkpFg/zkAoiz/CA==";
			String stringToDecode = encodedStr;
			// byte[] iv = Base64.getDecoder().decode("BBA6suIl7DmlmhnJydb+U8nD");
			byte[] iv = Base64.getDecoder().decode(ivStr);
			byte[] iv2 = new byte[16];
			for (int i = 0; i < 16; i++) {
				iv2[i] = iv[i + 2];
			}
			setKey(encryptKey);
			IvParameterSpec ivspec = new IvParameterSpec(iv2);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
			byte[] encodedData = Base64.getDecoder().decode(stringToDecode);
			byte[] plainBytesDecrypted = cipher.doFinal(encodedData);

			resultDecodedString = new String(plainBytesDecrypted);
		} catch (Exception e) {
			// log.info("Inside Decoded : " +e);

		}

		return resultDecodedString;
	}

	public static void setKey(String myKey) {

		try {
			MessageDigest sha = null;
			byte[] key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (Exception e) {
			// log.info("Inside set Key : " +e);
		}
	}

}
