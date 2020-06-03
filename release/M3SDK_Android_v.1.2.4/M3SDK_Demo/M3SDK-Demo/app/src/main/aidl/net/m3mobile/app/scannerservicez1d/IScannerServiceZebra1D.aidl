// IScannerServiceZebra1D.aidl
package net.m3mobile.app.scannerservicez1d;

// Declare any non-default types here with import statements

interface IScannerServiceZebra1D {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
	void handleScanCode(String code);
	void notifyInitFinish(int state);
	void handlePacket(in byte[] packet,int size);
    void refreshDefaultOption(boolean bStartNow);
    void setDecodeTimeShow(boolean bEnable);

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
    byte[] getParamRequest(in byte[] params);
    byte[] setParamRequest(in byte[] params, in byte[] val);
}
