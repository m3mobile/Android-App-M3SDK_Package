package com.m3.sdk.scanner;

public class ScanData {
    protected String _barcode;
    protected String _codeType;
    protected byte[] _rawBarcode;

    protected ScanData(String barcode, String type, byte[] barcodeData){
        _barcode = barcode;
        _codeType = type;
        _rawBarcode = barcodeData;
    }

    public String getBarcode(){
        return _barcode;
    }
    public String getCodeType(){
        return _codeType;
    }
    public byte[] getBarcodeRawData(){
        return _rawBarcode;
    }
}
