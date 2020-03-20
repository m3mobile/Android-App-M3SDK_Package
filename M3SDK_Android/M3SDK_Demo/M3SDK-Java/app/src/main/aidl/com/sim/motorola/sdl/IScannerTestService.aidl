package com.sim.motorola.sdl;

interface IScannerTestService
{
    void turnOnorOffScan(boolean isOn);
    void setOutPutMode(int mode);
    int getOutputMode();
    void setEndCharMode(int mode);
    void setPrefix(String prefix);
    void setPostfix(String postfix);
    void setSoundMode(int nMode);
    void turnOnorOffVibration(boolean isOn);
    void setScannerId(int id);
    int  setScanParameter(int num, int val);
    int  getScanParameter(int num);
    void SetReadMode(int nMode);
    int GetReadMode();
    void registScannerButton(boolean bRegist);
    void setHexcodeMode(boolean bSet);
    boolean setAimerIllumination(boolean aimer, boolean illumination);
    boolean setDecodeSessionTimeout(int sec);
    int getDecodeSessionTimeout();
    boolean setMobileMode(boolean bSet);
    boolean getMobileMode();
    void setPrefixPostFixHexcode(boolean bSet);
    boolean getPrefixPostFixHexcode();
    void refreshDefaultOption(boolean bStartNow);
    void decodeStart();
    void decodeStop();
}
