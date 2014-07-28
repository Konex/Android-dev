package com.yini.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AesCryptoUtil {
	private static final String seed = "This is a secret";
	
	public static String encrypt(String data) {
		try {
			SecretKeySpec sks = null;
			byte[] encodedBytes = null;
			
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed.getBytes());
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128, sr);
			sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
			
			Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(data.getBytes());
			
            return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	public static String decrypt(String data) {
		try {
			SecretKeySpec sks = null;
			byte[] decodedBytes = null;
			byte[] encodedBytes = Base64.decode(data, Base64.DEFAULT);
			
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed.getBytes());
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128, sr);
			sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
			
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(encodedBytes);
			
            return new String(decodedBytes);
		} catch (Exception e) {
			
		}
		
		return null;
	}
}
