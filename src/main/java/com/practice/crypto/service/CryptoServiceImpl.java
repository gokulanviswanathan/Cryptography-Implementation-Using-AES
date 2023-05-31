/**
 * 
 */
package com.practice.crypto.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CryptoServiceImpl implements CryptoService {

	private static final Logger logger = LogManager.getLogger(CryptoServiceImpl.class);
	
	private static String cryptoSecretKey = "abcdef";

	private static String cryptoSecretSalt = "pqrst";

	private static String cryptoKeyInstance = "PBKDF2WithHmacSHA256";

	private static String cryptoCipherInstance = "AES/GCM/PKCS5PADDING";

	private static String cryptoAlogorithmV2 = "AES";

	private static final String AES = "AES";

	private static final String SHA_2 = "SHA-2";

	private static final String ALGORITHM_MODE = "AES/ECB/PKCS5PADDING";
	
	private static final String CRYPTO_TYPE_ENC = "enc";
	
	private static final String CRYPTO_TYPE_DEC = "dec";
	
	private static final String INVALID_INPUT = "Invalid Input";

	private static SecretKeySpec cryptoSecretKeySpec;

	public static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
			if (Objects.isNull(cryptoSecretKeySpec)) {
				byte[] key;
				key = myKey.getBytes(StandardCharsets.UTF_8);
				sha = MessageDigest.getInstance(SHA_2);
				key = sha.digest(key);
				key = Arrays.copyOf(key, 16);
				cryptoSecretKeySpec = new SecretKeySpec(key, AES);
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public String getCryptoData(String cryptoType, String cryptoInput, String trackingId) {
		if (CRYPTO_TYPE_ENC.equalsIgnoreCase(cryptoType)) {
			return encryptData(cryptoInput);
		} else if (CRYPTO_TYPE_DEC.equalsIgnoreCase(cryptoType)) {
			return decryptData(cryptoInput);
		} else {
			return INVALID_INPUT;
		}
	}

	public static String encryptData(String strToEncrypt) {
		try {
			byte[] initializationVector = { 9, 1, 7, 0, 2, 5, 8, 0, 1, 3, 8, 4, 1, 7, 3, 9 };
			GCMParameterSpec initializationVectorSpec = new GCMParameterSpec(128, initializationVector);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(cryptoKeyInstance);
			KeySpec spec = new PBEKeySpec(cryptoSecretKey.toCharArray(), cryptoSecretSalt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKeySpecV2 = new SecretKeySpec(tmp.getEncoded(), cryptoAlogorithmV2);
			Cipher cipher = Cipher.getInstance(cryptoCipherInstance);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpecV2, initializationVectorSpec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return strToEncrypt;
		}
	}

	public static String decryptData(String strToDecrypt) {
		try {
			byte[] initializationVector = { 9, 1, 7, 0, 2, 5, 8, 0, 1, 3, 8, 4, 1, 7, 3, 9 };
			GCMParameterSpec initializationVectorSpec = new GCMParameterSpec(128, initializationVector);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(cryptoKeyInstance);
			KeySpec spec = new PBEKeySpec(cryptoSecretKey.toCharArray(), cryptoSecretSalt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKeySpecV2 = new SecretKeySpec(tmp.getEncoded(), cryptoAlogorithmV2);
			Cipher cipher = Cipher.getInstance(cryptoCipherInstance);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpecV2, initializationVectorSpec);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			// Fallback
			try {
				setKey(cryptoSecretKey);
				Cipher newCipher = Cipher.getInstance(ALGORITHM_MODE);
				newCipher.init(Cipher.DECRYPT_MODE, cryptoSecretKeySpec);
				return new String(newCipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
			} catch (Exception e2) {
				logger.error(e2.getMessage(), e2);
				return strToDecrypt;
			}
		}
	}
}
