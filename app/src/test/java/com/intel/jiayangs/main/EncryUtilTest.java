package com.intel.jiayangs.main;

import com.intel.jiayangs.security.EncryUtil;

import org.junit.Test;

import java.security.KeyPair;
import java.security.PublicKey;

import dalvik.annotation.TestTargetClass;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    EncryUtil encryUtil;
    KeyPair keyPair;
    String encrypted;
    @before
    public void init(){
        encryUtil=new EncryUtil();
    }

    @Test
    public void getKeyPairTest(){
        keyPair=encryUtil.getKeyPair();
        assertNotNull(keyPair);
    }

    @Test
    public void encryptTest(){
        getKeyPairTest();
        PublicKey publicKey=keyPair.getPublic();
        String in="hello";
        String encrypted=encryUtil.encrypt(in,publicKey);
        assertNotNull(encrypted);
        assertTrue(encrypted.length() != 0);
        assertTrue(!encrypted.equals(in));
    }

}