package com.m3.sdk.scannerlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

@Deprecated
public class BarcodeBroadcast extends BroadcastReceiver {

	private String barcode;
	private String type;
	private String module;
	private BarcodeManager bm;

	private String TAG = "BarcodeBroadcast";
	 BarcodeBroadcast(BarcodeManager bm){
		 this.bm=bm;	
	 }
	 
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG,"onReceive ["+ intent.getAction() + "]");	
		
		if (intent.getAction().equals(
				"com.android.server.scannerservice.broadcast")) {
			
			barcode = intent.getExtras().getString(Barcode_old.SCANNER_BARCODE_DATA);
			type = intent.getExtras().getString(Barcode_old.SCANNER_BARCODE_CODE_TYPE);
			module = intent.getExtras().getString(Barcode_old.SCANNER_MODULE_TYPE);
			
			if(barcode != null)
			{
				bm.sendBarcode(barcode);	
				
				bm.sendBarcode(barcode, type, module);
			}
			else
			{
				int nSymbol = intent.getExtras().getInt("symbology", -1);
				int nValue = intent.getExtras().getInt("value", -1);

				Log.i(TAG,"getSymbology ["+ nSymbol + "][" + nValue + "]");	
				
				if(nSymbol != -1)
				{
					bm.sendSymbology(nSymbol, nValue);
					Barcode.Symbology.setCodeType(nSymbol, nValue);
				}
			}	
			
		}
	}
}

