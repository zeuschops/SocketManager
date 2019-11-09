package com.gaijin.socketmanager.encryption;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	private static Base64.Encoder encoder = Base64.getEncoder();
	private static Base64.Decoder decoder = Base64.getDecoder();
	private static String key = "1a2B3c4D5e6F?@!";
	private static SecretKeySpec secretkey;
	private static Cipher encryptingCipher;
	private static Cipher decryptingCipher;

	/**
	 * Call this method to set a custom key for the encryption method.
	 * @param key A key to use instead of the default as a String.
	 */
	public void setKey(String key) {
		AES.key = key;
	}

	/**
	 * Encrypts data you want to send.
	 * @param data String message data to encrypt.
	 * @return data to send back to the SocketClient or SocketServer to send out. Will return null if the encryption fails.
	 * @throws UnsupportedEncodingException May be thrown if the key cannot be converted to UTF-8.
	 * @throws NoSuchPaddingException May be thrown if a cipher algorithm cannot be obtained.
	 * @throws NoSuchAlgorithmException May be thrown if the cipher algorithm cannot be identified.
	 * @throws InvalidKeyException May be thrown if the cipher algorithm fails to initialize.
	 * @throws BadPaddingException May be thrown if the encrypting cipher algorithm fails to encrypt data.
	 * @throws IllegalBlockSizeException Should not be thrown unless passed a cipher created as a string from byte data.
	 */
	public byte[] encrypt(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		if(AES.secretkey == null)
			updateKey();

		if(AES.encryptingCipher == null) {
			AES.encryptingCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			IvParameterSpec ivspec = new IvParameterSpec(Arrays.copyOf(key.getBytes(),16));
			try {
				AES.encryptingCipher.init(Cipher.ENCRYPT_MODE, secretkey, ivspec);
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
		}

		byte[] encrypted = AES.encryptingCipher.doFinal(data.getBytes());
		return AES.encoder.encode(encrypted);
	}

	/**
	 *
	 * @param data
	 * @return Returns decrypted string variable for use within application.
	 * @throws UnsupportedEncodingException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 */
	public String decrypt(byte[] data) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		if(AES.secretkey == null)
			updateKey();

		if(AES.decryptingCipher == null) {
			AES.decryptingCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			IvParameterSpec ivspec = new IvParameterSpec(Arrays.copyOf(key.getBytes(),16));
			try {
				AES.decryptingCipher.init(Cipher.DECRYPT_MODE, secretkey, ivspec);
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
		}

		byte[] decrypted = AES.decoder.decode(data);
		return new String(AES.decryptingCipher.doFinal(decrypted));
	}

	private void updateKey() {
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(key.toCharArray(), key.getBytes(), 65536, 256);
			SecretKey tmp = skf.generateSecret(spec);
			AES.secretkey = new SecretKeySpec(tmp.getEncoded(), "AES");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
}
