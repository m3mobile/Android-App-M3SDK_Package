// IScannerServiceZebra2D.aidl
package net.m3mobile.app.scannerservicez2d;
import net.m3mobile.app.scanemul.IScannerCallback;
// Declare any non-default types here with import statements

interface IScannerServiceZebra2D {
    void setScanner(boolean bEnable);
    void decodeStart();
    void decodeStop();
    int  setScanParameter(int num, int val);
    int  getScanParameter(int num);
    void setEndCharMode(int mode);
    void setOutputMode(int mode);
    void setPrefix(String prefix);
    void setPostfix(String postfix);
    void setSoundMode(int nMode);
    void setVibration(boolean isOn);
    void setReadMode(int nMode);
    void setScannerTriggerMode(int nMode); // 0: Enable, 1: all Disable (Function Call and Trigger Key), 2: Key Disable
    void setDecodeTimeShow(boolean bEnable);
    void refreshDefaultOption(boolean bStartNow);
    void previewStart();
    void previewStop();

    void registerScannerCallback(IScannerCallback callback);
    void unregisterScannerCallback(IScannerCallback callback);
}