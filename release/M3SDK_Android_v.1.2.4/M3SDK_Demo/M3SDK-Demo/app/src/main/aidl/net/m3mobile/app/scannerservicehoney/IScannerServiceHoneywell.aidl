// IScannerServiceHoneywell.aidl
package net.m3mobile.app.scannerservicehoney;
import net.m3mobile.app.scannerservicehoney.SymbolConfig;

// Declare any non-default types here with import statements
interface IScannerServiceHoneywell {
    void refreshDefaultOption(boolean bStartNow);
    void setDecodeTimeShow(boolean bEnable);

    void setScanner(boolean bEnable);
    boolean isEnableScanner();
    void decodeStart();
    void decodeStop();

    void setEndCharMode(int mode);
    void setOutputMode(int mode);
    void setPrefix(String prefix);
    void setPostfix(String postfix);
    void setSoundMode(int nMode);
    void setVibration(boolean isOn);
    void setReadMode(int nMode);
    void setScannerTriggerMode(int nMode); // 0: Enable, 1: all Disable (Function Call and Trigger Key), 2: Key Disable

    int getMultiReadCount();
    void setMultiReadCount(in int MultiReadCount);

    SymbolConfig getSymbologyConfig(in int symbologyID);
	boolean setSymbologyConfig(in SymbolConfig symbol);

}
