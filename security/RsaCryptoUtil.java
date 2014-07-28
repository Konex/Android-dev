package com.yini.utils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class RsaCryptoUtil {
	// TODO: ask your web team to generate an RSA public key then you can copy and paste to here.
	private static final String modulus = "s6lpjspk+3o2GOK5TM7JySARhhxE5gB96e9XLSSRuWY2W9F951MfistKRzVtg0cjJTdSk5mnWAVHLfKOEqp8PszpJx9z4IaRCwQ937KJmn2/2VyjcUsCsor+fdbIHOiJpaxBlsuI9N++4MgF/jb0tOVudiUutDqqDut7rhrB/oc=";
	private static final String exponent = "AQAB";

	public static String encrypt(String data) {
		try {
			byte[] modulusBytes = Base64.decodeBase64(modulus);
			byte[] exponentBytes = Base64.decodeBase64(exponent);
			BigInteger modulus = new BigInteger(1, modulusBytes);
			BigInteger publicExponent = new BigInteger(1, exponentBytes);
	
			RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, publicExponent);
			KeyFactory fact = KeyFactory.getInstance("RSA");;
						
			PublicKey pubKey = fact.generatePublic(rsaPubKey);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
	
			byte[] plainBytes = data.getBytes("UTF-16LE");
			byte[] cipherData = cipher.doFinal(plainBytes);
			return Base64.encodeBase64String(cipherData);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
