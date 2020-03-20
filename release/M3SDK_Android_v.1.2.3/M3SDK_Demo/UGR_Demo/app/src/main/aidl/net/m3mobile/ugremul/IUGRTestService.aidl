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
}
