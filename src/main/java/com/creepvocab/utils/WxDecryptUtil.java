package com.creepvocab.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;

public class WxDecryptUtil {

    public static String decrypt(String encryptedData, String sessionKey, String iv) {
        try {
            byte[] keyBytes = Base64.decode(sessionKey);
            byte[] ivBytes = Base64.decode(iv);

            AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, keyBytes, ivBytes);
            byte[] decrypt = aes.decrypt(Base64.decode(encryptedData));
            return new String(decrypt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decrypt failed", e);
        }
    }
}
