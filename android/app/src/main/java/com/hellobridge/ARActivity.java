package com.hellobridge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class ARActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ARActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_layout);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.REQ_MSG);
    }
}
