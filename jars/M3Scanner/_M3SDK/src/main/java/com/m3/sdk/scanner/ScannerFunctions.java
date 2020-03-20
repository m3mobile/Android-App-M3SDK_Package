package com.m3.sdk.scanner;
import android.content.Context;

import com.m3.sdk.scannerlib.BarcodeListener2;

public abstract class ScannerFunctions {
    public abstract void dispose();
    public abstract boolean setScanner(boolean bEnable);
    public abstract boolean decodeStart();
    public abstract boolean decodeStop();

    public abstract void setEndCharMode(int mode);
    public abstract void setOutputMode(int mode);
    public abstract void setPrefix(String prefix);
    public abstract void setPostfix(String postfix);
    public abstract void setSoundMode(int mode);
    public abstract void setVibration(boolean isOn);
    public abstract void setReadMode(int mode);
    public abstract void setScannerTriggerMode(int mode); // 0: Enable, 1: all Disable (Function Call and Trigger Key), 2: Key Disable

    public abstract int setScanParameter(int num, int val);
    public abstract int getScanParameter(int num);

    public abstract boolean isEnable();

    public abstract void addListener(BarcodeListener2 listener);
    public abstract void removeListener(BarcodeListener2 listener);
}
