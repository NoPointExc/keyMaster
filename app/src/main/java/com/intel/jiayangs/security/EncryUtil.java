package com.intel.jiayangs.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;


public class EncryUtil{
    public KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(
                        "key1",
                        KeyProperties.PURPOSE_DECRYPT) //the purpose of private key
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                        .build());
        //get KeyPair
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return  keyPair;
    }

    public String encrypt (String in,PublicKey publicKey) throws Exception {
        String encrypted=null;
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream=new CipherOutputStream(outputStream,cipher);
        cipherOutputStream.write(in.getBytes("UTF-8"));
        cipherOutputStream.flush();
        //outputStream.flush();
        byte[] vals=outputStream.toByteArray();
        cipherOutputStream.close();
        outputStream.close();
        //notify to update
        encrypted=new String(vals, StandardCharsets.UTF_8);
        return encrypted;
    }

    private String decrypt(String in, PrivateKey privateKey) throws Exception{
        String decrypted=null;

        return decrypted;
    }
}
