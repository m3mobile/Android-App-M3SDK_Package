package net.m3mobile.ugr_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_uhf_intent).setOnClickListener(this);
        findViewById(R.id.btn_uhf_aidl).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_uhf_intent:
                intent = new Intent(this, ResultWindow.class);
                startActivity(intent);
                break;
            case R.id.btn_uhf_aidl:
                intent = new Intent(this, ResultWindow_aidl.class);
                startActivity(intent);
                break;
        }
    }
}
