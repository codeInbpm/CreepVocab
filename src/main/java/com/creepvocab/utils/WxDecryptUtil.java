package com.creepvocab.utils;

import cn.hutool.core.codec.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Security;

public class WxDecryptUtil {

    public static String decrypt(String encryptedData, String sessionKey, String iv) {
        try {
            // Add BouncyCastle as a security provider
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }

            // Decode from Base64
            byte[] encDataBytes = Base64.decode(encryptedData);
            byte[] keyBytes = Base64.decode(sessionKey);
            byte[] ivBytes = Base64.decode(iv);

            // Initialize Cipher
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", BouncyCastleProvider.PROVIDER_NAME);
            SecretKeySpec spec = new SecretKeySpec(keyBytes, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivBytes));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);

            // Decrypt
            byte[] resultByte = cipher.doFinal(encDataBytes);
            if (null != resultByte && resultByte.length > 0) {
                return new String(resultByte, StandardCharsets.UTF_8);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Decrypt failed: " + e.getMessage(), e);
        }
    }
}
