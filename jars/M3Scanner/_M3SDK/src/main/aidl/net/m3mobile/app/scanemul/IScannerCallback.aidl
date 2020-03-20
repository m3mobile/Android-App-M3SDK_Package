package net.m3mobile.app.scanemul;

interface IScannerCallback {
    oneway void onZebraPreview(in Bitmap bitmap);
    oneway void onDecoding(String code, String type, in byte[] byteCode);
}
