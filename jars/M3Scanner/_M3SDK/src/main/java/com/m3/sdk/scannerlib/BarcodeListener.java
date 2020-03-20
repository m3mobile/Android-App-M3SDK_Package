package com.m3.sdk.scannerlib;

@Deprecated
public interface BarcodeListener {
	void onBarcode(String barcode);
	void onBarcode(String barcode, String codeType);
	void onGetSymbology(int nSymbol, int nVal);
}
