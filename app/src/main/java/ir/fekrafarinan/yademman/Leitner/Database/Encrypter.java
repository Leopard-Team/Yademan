package ir.fekrafarinan.yademman.Leitner.Database;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class Encrypter {

    private static final String key = "#$sd2455fuasghar+-ghj:Dla-+tnks9";
    private static SecretKey secretKey;

    public static SecretKey generateKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        return new SecretKeySpec(key.getBytes(), "AES");
    }

    public static SecretKey getKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (secretKey == null)
            secretKey = generateKey();
        return secretKey;
    }

    public static String encryptMsg(String message, SecretKey secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        return Base64.encodeToString(cipherText, 0);
    }

    public static String decryptMsg(String message, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
        byte[] cipherText = Base64.decode(message, 0);
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }

}
