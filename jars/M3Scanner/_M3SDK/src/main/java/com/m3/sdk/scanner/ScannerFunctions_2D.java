package com.m3.sdk.scanner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.m3.sdk.scannerlib.BarcodeListener2;
import com.m3.sdk.util.LogWriter;

import net.m3mobile.app.scanemul.IScannerCallback;
import net.m3mobile.app.scannerservicez2d.IScannerServiceZebra2D;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ScannerFunctions_2D extends ScannerFunctions implements ServiceConnection {

    protected static final String SCANNER_2D_ACTION = "net.m3mobile.app.scannerservicezebra2d.start";
    protected static final String SCANNER_2D_PACAKAGE_NEW = "net.m3mobile.app.scanemul";
    protected static final String SCANNER_2D_PACKAGE_OLD = "net.m3mobile.app.scannerservicez2d";
    protected static final String SCANNER_2D_PACKAGE_SL10 = "com.zebra.scanner";

    protected final int MESSAGE_SCANNED = 1002;

    // private final String TAG = "ScannerFunc_2D";
    private Context _currentCtx;
    private IScannerServiceZebra2D m2DService;
    private Collection<BarcodeListener2> _listeners = null;

    private Handler _scanHandler = null;

    public ScannerFunctions_2D(Context context){
        _currentCtx = context;
        _scanHandler = new Handler(_currentCtx.getMainLooper(), new ScanCallback());

        // 연결
        Intent intent = new Intent(SCANNER_2D_ACTION).setPackage(getPackageName());
        boolean bBind = _currentCtx.bindService(intent, this, Context.BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT);
        LogWriter.i( "ScannerFunctions_2D::bindService " + bBind);
    }

    @Override
    public void dispose(){
        try {
            m2DService.unregisterScannerCallback(_callbackScanner);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // 해제
        _currentCtx.unbindService(this);
    }

    private String getPackageName() {
        String packageName;

        // SL10
        if(Build.MODEL.contains("SL10")){
            packageName = SCANNER_2D_PACKAGE_SL10;
            return packageName;
        }

        // OThers
        PackageManager manager = _currentCtx.getPackageManager();
        List<ApplicationInfo> list = manager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo info : list) {
            try {
                // LogWriter.i("info.packageName : " + info.packageName);
                if(info.packageName.equals(SCANNER_2D_PACKAGE_OLD)) {
                    packageName = SCANNER_2D_PACKAGE_OLD;
                    return packageName;
                }
            } catch (Exception e) {
                LogWriter.i( "getPackageType Exception : " + e.getMessage());
            }
        }
        return SCANNER_2D_PACAKAGE_NEW;
    }



    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        m2DService = IScannerServiceZebra2D.Stub.asInterface(iBinder);

        try {
            m2DService.registerScannerCallback(_callbackScanner);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Service bind 완료 알림
        for (final BarcodeListener2 listener : _listeners) {
            listener.onScannerInitialized();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    @Override
    public boolean setScanner(boolean bEnable) {
        try {
            m2DService.setScanner(bEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean decodeStart(){
        try {
            m2DService.decodeStart();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean decodeStop(){
        try {
            m2DService.decodeStop();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void setEndCharMode(int mode) {
        try {
            m2DService.setEndCharMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOutputMode(int mode) {
        try {
            m2DService.setOutputMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPrefix(String prefix) {
        try {
            m2DService.setPrefix(prefix);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPostfix(String postfix) {
        try {
            m2DService.setPostfix(postfix);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSoundMode(int mode) {
        try {
            m2DService.setSoundMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setVibration(boolean isOn) {

        try {
            m2DService.setVibration(isOn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setReadMode(int mode) {

        try {
            m2DService.setReadMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setScannerTriggerMode(int mode) {

        try {
            m2DService.setScannerTriggerMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int setScanParameter(int num, int val) {
        int returnValue = -1;

        try {
            returnValue = m2DService.setScanParameter(num, val);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    @Override
    public int getScanParameter(int num) {

        int returnValue = -1;

        try {
            returnValue = m2DService.getScanParameter(num);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    @Override
    public boolean isEnable() {
        boolean bEnable = false;
        try {
            bEnable = m2DService.isEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return bEnable;
    }

    @Override
    public void setMultipleCount(int count) {
        try {
            m2DService.setMultipleCount(count);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addListener(BarcodeListener2 bl) {
        if (_listeners == null) {
            _listeners = new HashSet<>();
        }
        _listeners.add(bl);
    }

    public void removeListener(BarcodeListener2 bl) {
        if (_listeners == null)
            return;
        _listeners.remove(bl);
    }

    private IScannerCallback _callbackScanner = new IScannerCallback.Stub() {
        @Override
        public void onZebraPreview(Bitmap bitmap) throws RemoteException {

        }

        @Override
        public void onDecoding(String code, String type, byte[] byteCode) throws RemoteException {

            LogWriter.i("code: " + code + " type: " + type);
            Message msg = new Message();
            ScanData data = new ScanData(code, type, byteCode);

            // 메시지 핸들 사용. 현재 Context 스레드로 값을 보내기 위함
            msg.what = MESSAGE_SCANNED;
            msg.obj = data;
            _scanHandler.sendMessage(msg);
        }
    };

    private class ScanCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message message) {
            switch(message.what){
                case MESSAGE_SCANNED:
                    for (final BarcodeListener2 listener : _listeners) {
                        listener.onBarcode((ScanData)message.obj);
                    }
                    break;
            }
            return true;
        }
    }
}
