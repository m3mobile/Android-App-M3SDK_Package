package com.m3.m3sdk_demo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import net.m3mobile.app.scanemul.IScannerCallback;
import net.m3mobile.app.scannerservicez2d.IScannerServiceZebra2D;

import java.util.List;

public class PreviewAidlActivity extends Activity implements ServiceConnection {

    private static String TAG = "PreviewAidlActivity";
    ImageView ivPreview;
    IScannerServiceZebra2D  m2DService;
    private IScannerCallback.Stub callback = new IScannerCallback.Stub() {
        @Override
        public void onZebraPreview(Bitmap bitmap) throws RemoteException {
            ivPreview.setImageBitmap(bitmap);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_aidl);
        ivPreview = findViewById(R.id.iv_preview);
        bindScannerService();

    }

    public void bindScannerService(){
        Intent intent = null;
        intent = new Intent("net.m3mobile.app.scannerservicezebra2d.start");
        intent.setPackage(get2DAIDLPackageName());
        boolean bBind = bindService(intent, this, Context.BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT);

    }

    @Override
    protected void onDestroy() {
        try {
            m2DService.unregisterScannerCallback(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        m2DService = IScannerServiceZebra2D.Stub.asInterface(iBinder);
        try {
            m2DService.registerScannerCallback(callback);
            m2DService.previewStart();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    void startPreview(){
        if(m2DService != null){
            try {
                m2DService.previewStart();
                Log.d(TAG, "previewStart() Try call...");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Log.d(TAG, "Failed previewStart()...");
        }
    }
    void stopPreview(){
        if(m2DService !=null){
            try{
                m2DService.previewStop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    private String get2DAIDLPackageName() {
        String packageName = "net.m3mobile.app.scanemul";
        PackageManager manager = getPackageManager();
        List<ApplicationInfo> list = manager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : list) {
            try {
                Log.d(TAG, "info.packageName : " + info.packageName);
                if(info.packageName.equals("net.m3mobile.app.scannerservicez2d")) {
                    packageName = "net.m3mobile.app.scannerservicez2d";
                }
                if(Build.MODEL.contains("SL10")){
                    packageName = "com.zebra.scanner";
                }

            } catch (Exception e) {
                Log.e(TAG, "getPackageType Exception : " + e.getMessage());
            }
        }
        return packageName;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPreview();
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
