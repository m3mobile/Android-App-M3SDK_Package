// IScannerCallback.aidl
package net.m3mobile.app.scanemul;
import android.graphics.Bitmap;

// Declare any non-default types here with import statements

interface IScannerCallback {
   oneway void onZebraPreview(in Bitmap bitmap);
}
