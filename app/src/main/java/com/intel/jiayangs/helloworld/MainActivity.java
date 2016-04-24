package com.intel.jiayangs.helloworld;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.intel.jiayangs.security.EncryUtil;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;


public class MainActivity extends AppCompatActivity {
    //debug
    private static final String DEBUG_TAG="###DEBUG###";
    //view
    private ListView listView;
    private ArrayAdapter listAdapter;
    private EditText aliasText;
    private EditText algText;
    private EditText sizeText;
    private EditText initText;
    private EditText encryText;
    private EditText decryText;
    //encrypt
    private HashMap <String,KeyPair> keyMap;
    private ArrayList<String> keyAliases;
    //private ArrayList<KeyPair> keyPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        keyAliases =new ArrayList<>();
        //init view
        listView=(ListView)findViewById(R.id.listView);
        View listHeader = View.inflate(this, R.layout.activity_main_header, null);
        listView.addHeaderView(listHeader);
        aliasText=(EditText)findViewById(R.id.aliasText);
        algText=(EditText)findViewById(R.id.algText);
        sizeText=(EditText)findViewById(R.id.sizeText);
        initText=(EditText)findViewById(R.id.startText);
        encryText=(EditText)findViewById(R.id.encryptedText);
        decryText=(EditText)findViewById(R.id.decryptedText);
        //add adapter
        listAdapter=new ArrayAdapter(this,R.layout.list_item,R.id.keyAlias,keyAliases);
        listView.setAdapter(listAdapter);
        //init keys
        keyMap =new HashMap<>();
    }



    private synchronized void refreshKeys() {
        if(keyMap==null||keyMap.isEmpty()) return;
        keyAliases=new ArrayList<>(keyMap.keySet());
        if(listAdapter != null){
            listAdapter.notifyDataSetChanged();
        }
    }


    private synchronized void refreshEncryText(String str){
        encryText.setText(str);
        encryText.invalidate();
    }


    public void createNewKeys(View view) {
        String alias=aliasText.getText().toString();
        try {
            final AtomicReference<Exception> slaveExpection = new AtomicReference<Exception>();
            KeyPairGenTask task=new KeyPairGenTask(slaveExpection,alias);
            Thread slave=new Thread(task);
            slave.start();
            if(slaveExpection.get()!=null){
                throw slaveExpection.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG,"createNewKeys");
        }
        refreshKeys();
    }

    public void deleteKey(View view){
//        if(!keyAliases.isEmpty()){
//            View row=(ViewGroup)(view.getParent()).getParent();
//
//            int pos=listView.getPositionForView(row);
//            if(pos==ListView.INVALID_POSITION){
//                Log.e(DEBUG_TAG,"deleteKey(View): invalide position");
//            }
//
//
//            Log.d(DEBUG_TAG,"delete key at"+pos);
//            refreshKeys();
//        }
    }

    public void encrypt(View view){
        String initStr=initText.getText().toString();
        if(initStr!=null && initStr.length()!=0){
            KeyPair keyPair=keyMap.get(initStr);
            EncryTask task=new EncryTask(initStr,keyPair.getPublic());
            Thread salve=new Thread(task);
            salve.start();
            try {
                salve.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            refreshEncryText(task.getEncrypted());
        }
    }



    //*****************************************(de)encryption tasks*****************************

    public class KeyPairGenTask implements Runnable{
        private KeyPair keyPair;
        private String alias;
        private AtomicReference<Exception> exception;
        public KeyPairGenTask( AtomicReference<Exception> e,String alias){
            exception=e;
            this.alias=alias;
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
                keyMap.put(alias,keyPair);
                refreshKeys();
            }catch (Exception e){
                exception.set(e);
            }
        }

    }

    public class EncryTask implements Runnable{
        String text;
        PublicKey publicKey;
        String rst;

        public EncryTask(String text, PublicKey publicKey){
            this.text=text;
            this.publicKey=publicKey;
        }

        @Override
        public void run() {
            //encrypt
            try{
                //Log.d(DEBUG_TAG,"#####encrypting######");
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                Log.d(DEBUG_TAG,publicKey.toString());
                Log.d(DEBUG_TAG,cipher.toString());
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                CipherOutputStream cipherOutputStream=new CipherOutputStream(outputStream,cipher);
                Log.d(DEBUG_TAG,"len="+text.getBytes("UTF-8").length);
                cipherOutputStream.write(text.getBytes("UTF-8"));
                cipherOutputStream.flush();
                //outputStream.flush();
                byte[] vals=outputStream.toByteArray();
                cipherOutputStream.close();
                outputStream.close();
                Log.d(DEBUG_TAG,"#####encryption success######");
                //notify to update
                rst=new String(vals, StandardCharsets.UTF_8);
                Log.d(DEBUG_TAG,"rst_len="+vals.length);
                Log.d(DEBUG_TAG,"rst="+rst);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public String getEncrypted(){
            return rst;
        }
    }

}
