package com.m3.sdk.scannerlib;

import com.m3.sdk.scanner.ScanData;

public interface BarcodeListener2 {
    // void onBarcode(String code, String type, byte[] byteCode);
    void onBarcode(ScanData data);
    void onScannerInitialized();
}
