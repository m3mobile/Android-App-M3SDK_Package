package com.m3.sdk.scannerlib;

public interface BarcodeListener {

	void onBarcode(String barcode);
	void onBarcode(String barcode, String codeType);
	void onGetSymbology(int nSymbol, int nVal);

}
