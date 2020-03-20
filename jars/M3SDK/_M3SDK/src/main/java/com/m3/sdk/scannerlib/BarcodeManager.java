package com.m3.sdk.scannerlib;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BarcodeManager {
	
	private Context mContext;
	private Collection<BarcodeListener> listeners;
	private BarcodeBroadcast bb;
	
	private String barcode;
	
	public BarcodeManager(Context mContext){
		this.mContext=mContext;
		bb = new BarcodeBroadcast(this);
		
		IntentFilter filter = new IntentFilter();
		
		filter.addAction(Barcode.SCN_CUST_ACTION_SCODE);
		
		mContext.registerReceiver(bb,filter);				
	}


	public void dismiss(){
		mContext.unregisterReceiver(bb);
	}


	public void addListener(BarcodeListener bl) {
		if (listeners == null) 
            listeners = new HashSet<BarcodeListener>();
        listeners.add(bl);
	}


	public void removeListener(BarcodeListener bl) {
		if (listeners == null)
            return;
        listeners.remove(bl);
	}


	public String getBarcode(){
		return this.barcode;
	}


	protected void sendBarcode(String barcode){
		this.barcode=barcode;
		if (listeners == null)
            return;
        notifyListeners(barcode);	
	}

	protected void sendBarcode(String barcode, String codeType, String module){
		this.barcode=barcode;
		
		/*if(module.equals("Zebra_2D")){
			
	        notifyListeners(barcode, codeType);	
		}else if(module.equals("Zebra_1D")){
	        notifyListeners(barcode, codeType);	
		}else{
	        notifyListeners(barcode);	
		}*/

		if(codeType.isEmpty())
			notifyListeners(barcode);
		else
			notifyListeners(barcode, codeType);
		
		if (listeners == null)
            return;
		
	}


	protected void sendSymbology(int nSymbology, int nValue){
		if (listeners == null)
            return;
		notifySymbologyToListeners(nSymbology, nValue);	
	}

	private void notifyListeners(String strBarcode) {
        Iterator<BarcodeListener> iter = listeners.iterator();
        while (iter.hasNext()) {
            BarcodeListener listener = (BarcodeListener) iter.next();
            
            listener.onBarcode(barcode);
        }
    }

	private void notifyListeners(String strBarcode, String codeType) {
        Iterator<BarcodeListener> iter = listeners.iterator();
        while (iter.hasNext()) {
            BarcodeListener listener = (BarcodeListener) iter.next();
            
            listener.onBarcode(barcode, codeType);
        }
    }

	private void notifySymbologyToListeners(int nSymbol, int nValue) {
        Iterator<BarcodeListener> iter = listeners.iterator();
        while (iter.hasNext()) {
            BarcodeListener listener = (BarcodeListener) iter.next();
            
            listener.onGetSymbology(nSymbol, nValue);
        }
    }

	public void scanStart() {
		Intent intent = new Intent(Barcode.SCN_CUST_ACTION_START, null);
		mContext.sendOrderedBroadcast(intent, null);
	}


	public void scanDispose() {
		Intent intent = new Intent(Barcode.SCN_CUST_ACTION_CANCEL, null);
		mContext.sendOrderedBroadcast(intent, null);
	}
}

