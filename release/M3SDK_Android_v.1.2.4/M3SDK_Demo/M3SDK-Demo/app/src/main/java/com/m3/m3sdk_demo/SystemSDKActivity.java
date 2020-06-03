package com.m3.m3sdk_demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SystemSDKActivity extends Activity implements View.OnClickListener, ServiceConnection {

    private static String TAG = "M3SystemSDK - SystemSDKActivity";
    EditText editYear, editMonth, editDate, editHour, editMinute, editSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_sdk);

        Intent intent;
        intent = new Intent("net.m3mobile.app.m3systemsdkservice.start");
        intent.setPackage("net.m3mobile.m3systemsdk");
        boolean bBind = bindService(intent,this, Context.BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT);
        //Log.d(TAG, "bindScannerService " + bBind);

        findViewById(R.id.btn_modem_enable).setOnClickListener(this);
        findViewById(R.id.btn_modem_disable).setOnClickListener(this);
        findViewById(R.id.btn_adb_enable).setOnClickListener(this);
        findViewById(R.id.btn_adb_disable).setOnClickListener(this);
        findViewById(R.id.btn_datetime_set).setOnClickListener(this);
        findViewById(R.id.btn_shortcut_enable).setOnClickListener(this);
        findViewById(R.id.btn_shortcut_disable).setOnClickListener(this);
        findViewById(R.id.btn_lock_screen_enable).setOnClickListener(this);
        findViewById(R.id.btn_lock_screen_disable).setOnClickListener(this);
        findViewById(R.id.btn_magnification_enable).setOnClickListener(this);
        findViewById(R.id.btn_magnification_disable).setOnClickListener(this);
        findViewById(R.id.btn_color_correction_enable).setOnClickListener(this);
        findViewById(R.id.btn_color_correction_disable).setOnClickListener(this);
        findViewById(R.id.btn_color_inversion_enable).setOnClickListener(this);
        findViewById(R.id.btn_color_inversion_disable).setOnClickListener(this);
        findViewById(R.id.btn_large_pointer_enable).setOnClickListener(this);
        findViewById(R.id.btn_large_pointer_disable).setOnClickListener(this);
        findViewById(R.id.btn_power_button_enable).setOnClickListener(this);
        findViewById(R.id.btn_power_button_disable).setOnClickListener(this);
        findViewById(R.id.btn_high_contrast_enable).setOnClickListener(this);
        findViewById(R.id.btn_high_contrast_disable).setOnClickListener(this);
        editYear = (EditText) findViewById(R.id.edit_year);
        editMonth = (EditText) findViewById(R.id.edit_month);
        editDate = (EditText) findViewById(R.id.edit_date);
        editHour = (EditText) findViewById(R.id.edit_hour);
        editMinute = (EditText) findViewById(R.id.edit_minute);
        editSecond = (EditText) findViewById(R.id.edit_second);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.server.m3system.accessibility.response");
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_modem_enable:
                sendBroadcast(new Intent("com.android.server.m3system.modem.enable").putExtra("modem_enable", true));
                break;
            case R.id.btn_modem_disable:
                sendBroadcast(new Intent("com.android.server.m3system.modem.enable").putExtra("modem_enable", false));
                break;
            case R.id.btn_adb_enable:
                sendBroadcast(new Intent("com.android.server.m3system.adb.enable").putExtra("adb_enable", true));
                break;
            case R.id.btn_adb_disable:
                sendBroadcast(new Intent("com.android.server.m3system.adb.enable").putExtra("adb_enable", false));
                break;
            case R.id.btn_datetime_set:
                intent = new Intent("com.android.server.m3system.datetime.set");
                intent.putExtra("datetime_year", Integer.valueOf(editYear.getText().toString()));
                intent.putExtra("datetime_month", Integer.valueOf(editMonth.getText().toString()));
                intent.putExtra("datetime_date", Integer.valueOf(editDate.getText().toString()));
                intent.putExtra("datetime_hour", Integer.valueOf(editHour.getText().toString()));
                intent.putExtra("datetime_minute", Integer.valueOf(editMinute.getText().toString()));
                intent.putExtra("datetime_second", Integer.valueOf(editSecond.getText().toString()));
                sendBroadcast(intent);
                break;
            case R.id.btn_shortcut_enable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "shortcut");
                intent.putExtra("accessibility_enable", true);
                sendBroadcast(intent);
                break;
            case R.id.btn_shortcut_disable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "shortcut");
                intent.putExtra("accessibility_enable", false);
                sendBroadcast(intent);
                break;
            case R.id.btn_lock_screen_enable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "lock_screen");
                intent.putExtra("accessibility_enable", true);
                sendBroadcast(intent);
                break;
            case R.id.btn_lock_screen_disable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "lock_screen");
                intent.putExtra("accessibility_enable", false);
                sendBroadcast(intent);
                break;
            case R.id.btn_magnification_enable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "magnification");
                intent.putExtra("accessibility_enable", true);
                sendBroadcast(intent);
                break;
            case R.id.btn_magnification_disable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "magnification");
                intent.putExtra("accessibility_enable", false);
                sendBroadcast(intent);
                break;
            case R.id.btn_color_correction_enable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "color_correction");
                intent.putExtra("accessibility_enable", true);
                sendBroadcast(intent);
                break;
            case R.id.btn_color_correction_disable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "color_correction");
                intent.putExtra("accessibility_enable", false);
                sendBroadcast(intent);
                break;
            case R.id.btn_color_inversion_enable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "color_inversion");
                intent.putExtra("accessibility_enable", true);
                sendBroadcast(intent);
                break;
            case R.id.btn_color_inversion_disable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "color_inversion");
                intent.putExtra("accessibility_enable", false);
                sendBroadcast(intent);
                break;
            case R.id.btn_large_pointer_enable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "large_pointer");
                intent.putExtra("accessibility_enable", true);
                sendBroadcast(intent);
                break;
            case R.id.btn_large_pointer_disable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "large_pointer");
                intent.putExtra("accessibility_enable", false);
                sendBroadcast(intent);
                break;
            case R.id.btn_power_button_enable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "power_button_ends_call");
                intent.putExtra("accessibility_enable", true);
                sendBroadcast(intent);
                break;
            case R.id.btn_power_button_disable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "power_button_ends_call");
                intent.putExtra("accessibility_enable", false);
                sendBroadcast(intent);
                break;
            case R.id.btn_high_contrast_enable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "high_contrast");
                intent.putExtra("accessibility_enable", true);
                sendBroadcast(intent);
                break;
            case R.id.btn_high_contrast_disable:
                intent = new Intent("com.android.server.m3system.accessibility.set");
                intent.putExtra("accessibility_name", "high_contrast");
                intent.putExtra("accessibility_enable", false);
                sendBroadcast(intent);
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "onServiceConnected");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d(TAG, "onServiceDisconnected");
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive - "+ action);
            if("com.android.server.m3system.accessibility.response".equals(action)) {
                String strResponse = intent.getStringExtra("response_name");
                boolean bResponse = intent.getBooleanExtra("response", false);
                Log.d(TAG, "strResponse: " + strResponse + ", bResponse: " + bResponse);
            }
        }
    };
}
