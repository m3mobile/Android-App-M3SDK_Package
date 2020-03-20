package com.m3mobile.app.sdk_demo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*;

class MainActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_demo_scanner.setOnClickListener(this)
        button_demo_scanner_intent.setOnClickListener(this)
    }

    override fun onClick(viewId: View?) {
        when(viewId){
            button_demo_scanner -> startActivity(Intent(this, ScannerActivity::class.java))
            button_demo_scanner_intent -> startActivity(Intent(this, ScannerIntentActivity::class.java))
        }
    }
}
