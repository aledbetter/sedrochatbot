package com.sedroApps.util;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;


public class EncryptUtil {
	
    private static String calculateHash(MessageDigest d, byte[] bytes) {
        if (bytes == null) return null;
        d.update(bytes);
        return encodeHex(d.digest());
    }
    private static MessageDigest getDigest(String digest) {
        try  {
            return MessageDigest.getInstance(digest);
        } catch (NoSuchAlgorithmException e) {
        	return null;
        }
    }

    private static byte[] createCipherBytes(String key, int bitsNeeded) {
    	String word = calculateHash(getDigest("MD5"), key.getBytes(StandardCharsets.UTF_8));
        return word.substring(0, bitsNeeded / 8).getBytes(StandardCharsets.UTF_8);
    }

    private static Cipher createAesEncryptionCipher(String key) throws Exception {
        return createAesCipher(key, Cipher.ENCRYPT_MODE);
    }

    private static Cipher createAesDecryptionCipher(String key) throws Exception {
        return createAesCipher(key, Cipher.DECRYPT_MODE);
    }

    private static Cipher createAesCipher(String key, int mode) throws Exception {
        Key sKey = new SecretKeySpec(createCipherBytes(key, 128), "AES");
        return createAesCipher(sKey, mode);
    }

    // 16 bytes key for seed
    private static Cipher createAesCipher(Key key, int mode) throws Exception {
        MessageDigest d = getDigest("MD5");
        d.update(key.getEncoded());
        byte[] iv = d.digest();

        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");        // CBC faster than CFB8/NoPadding (but file length changes)
        cipher.init(mode, key, paramSpec);
        return cipher;
    }

    public static String encryptBytes(String key, byte[] content) {
        try {
            return encodeHex(createAesEncryptionCipher(key).doFinal(content));
        } catch (Exception e) {
            throw new IllegalStateException("Error encryptBytes: ", e);
        }
    }

    public static byte[] decryptBytes(String key, String hexStr)  {
        try  {
            return createAesDecryptionCipher(key).doFinal(decodeHex(hexStr));
        } catch (Exception e) {
            throw new IllegalStateException("Error decryptBytes: ", e);
        }
    }
    

	public static String encodeHex(final byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length << 1);
		for (byte aByte : bytes) {
			sb.append(convertDigit(aByte >> 4));
			sb.append(convertDigit(aByte & 0x0f));
		}
		return sb.toString();
	}
	public static byte[] decodeHex(final String s) {
		int len = s.length();
		if (len % 2 != 0) return null;

		byte[] bytes = new byte[len / 2];
		int pos = 0;

		for (int i = 0; i < len; i += 2) {
			byte hi = (byte)Character.digit(s.charAt(i), 16);
			byte lo = (byte)Character.digit(s.charAt(i + 1), 16);
			bytes[pos++] = (byte)(hi * 16 + lo);
		}
		return bytes;
	}

	private static final char[] _hex = {
	        '0', '1', '2', '3', '4', '5', '6', '7',
	        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static char convertDigit(final int value) {
		return _hex[value & 0x0f];
	}
}
