package com.m3.m3sdk_demo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ImageCaptureIntentActivity extends Activity implements View.OnClickListener {

    private static String TAG = "ImageCaptureIntent";
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1002;

    Button btn_takePicture;
    ImageView ivResult;
    TextView tvPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture_intent);
        btn_takePicture=findViewById(R.id.btn_take_picture);
        tvPath = findViewById(R.id.tv_path);
        ivResult = findViewById(R.id.iv_result);
        btn_takePicture.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantValues.SCANNER_ACTION_TAKE_PICTURE_PATH);
        registerReceiver(mReceiver, filter);

       if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive - "+action);
            if(ConstantValues.SCANNER_ACTION_TAKE_PICTURE_PATH.equals(action)){
                String strPath = intent.getStringExtra(ConstantValues.SCANNER_EXTRA_TAKE_PICTURE_PATH);
                File fileCasheItem = new File(strPath);
                ivResult.setImageURI(Uri.fromFile(fileCasheItem));
                tvPath.setText(strPath);
            }
        }
    };


    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_take_picture:
                this.sendOrderedBroadcast(new Intent(ConstantValues.SCANNER_ACTION_TAKE_PICTURE), null);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            Intent it = new Intent(this, ImageCaptureSettingsActivity.class);
            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }
}
