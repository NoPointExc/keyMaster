package com.intel.jiayangs.main;

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

import com.intel.jiayangs.helloworld.R;
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

    ArrayList<String> keyAliases;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        keyAliases=new ArrayList<>();
        listAdapter=new ArrayAdapter(this,R.layout.list_item,R.id.keyAlias,keyAliases);
        listView.setAdapter(listAdapter);
    }



    private synchronized void refreshKeys() {
        if(keyAliases!=null){
            listAdapter.notifyDataSetChanged();
        }
    }

    public void createNewKeys(View view) {
        keyAliases.add("keyAlias Sample");
    }

    public void deleteKey(View view){

    }

    public void encrypt(View view){

    }
}
