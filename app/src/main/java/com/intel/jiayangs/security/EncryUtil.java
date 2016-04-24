package com.intel.jiayangs.security;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;


/**
 * Created by yang on 16-4-21.
 */
public class EncryUtil{
    private static final String DEBUG_TAG="###DEBUG###EncryUtil";

    public KeyPair getKeyPair() throws Exception{
        final AtomicReference<Exception> slaveExpection = new AtomicReference<Exception>();
        KeyPairGenTask task=new KeyPairGenTask(slaveExpection);
        Thread slave=new Thread(task);
        slave.start();
        slave.join();
        if(slaveExpection.get()!=null){
            throw slaveExpection.get();
        }
        KeyPair keyPair=task.getKeyPair();
        return  keyPair;
    }


    public class KeyPairGenTask implements Runnable{
        private KeyPair keyPair;
        private  AtomicReference<Exception> exception;

        public KeyPairGenTask( AtomicReference<Exception> e){
            exception=e;
        }

        @Override
        public void run() {
            try{
                KeyStore keyStore=KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                keyPairGenerator.initialize(
                        new KeyGenParameterSpec.Builder(
                                "key1",
                                KeyProperties.PURPOSE_DECRYPT)
                                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                                .build());
                keyPair = keyPairGenerator.generateKeyPair();
            }catch (Exception e){
               exception.set(e);
            }
        }

        public KeyPair getKeyPair(){
            return keyPair;
        }
    }

    public class EncryTask implements Runnable{
        String text;
        PrivateKey privateKey;

        public EncryTask(String text, PrivateKey privateKey){
            this.text=text;
            this.privateKey=privateKey;
        }

        @Override
        public void run() {
            //encrypt
            try{
                Log.d(DEBUG_TAG,"#####encrypting######");
                Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                CipherOutputStream cipherOutputStream=new CipherOutputStream(outputStream,cipher);
                cipherOutputStream.write(text.getBytes("UTF-8"));
                byte[] vals=outputStream.toByteArray();
                cipherOutputStream.close();
                outputStream.close();
                Log.d(DEBUG_TAG,"#####encrypting success######");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
