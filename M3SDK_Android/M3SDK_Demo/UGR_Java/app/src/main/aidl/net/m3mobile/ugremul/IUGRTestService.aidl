// IUGRTestService.aidl
package net.m3mobile.ugremul;
import net.m3mobile.ugremul.IUHFServiceCallback;

interface IUGRTestService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    boolean registerUHFServiceCallback(IUHFServiceCallback callback);
    boolean unregisterUHFServiceCallback(IUHFServiceCallback callback);
    void refreshDefaultOption();
    void Inventory(boolean bStart);
    // 2020-01-20 1.2.12
    void setRegionOem(int nRegionOem);
    void setPower(int nPower);
    void setReadMode(int nRead);
    void setTrigger(int nTrigger);
    void setOutputMode(int nOutput);
    void setEndChar(int nEndChar);
    void setSound(int nSound);
    void setEnable(int nEnable, boolean bModule);
    void setIntentEnable(boolean bEnable);
    void setHexCode(boolean bEnable);
    // 2020-08-14 1.2.19
    void updateAllOption();
}
