/*
 * v.1.2.0	2016-09-09	���翵		SM10 LTE ������ Key �� Scanner �� �и���
 * v.1.3.0	2020-02-11	전재영		Scanner Control 을 AIDL 로하도록 기능 추가
 */
package com.m3.sdk.scannerlib;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.m3.sdk.scanner.ScannerFunctions;
import com.m3.sdk.scanner.ScannerFunctions_2D;
import com.m3.sdk.scannerlib.DataType;

public class Barcode {
	private static String TAG = "Barcode-scan";
	private Context mContext;
	private Symbology mSymbology;
	private ScannerFunctions _function;

	@Deprecated
	public Barcode(Context context) {
		this.mContext = context;
		mSymbology = new Symbology(mContext);
		_function = new ScannerFunctions_2D(context);
	}

	public Barcode(Context context, DataType.SCANNER_MODULE module){
		this.mContext = context;
		mSymbology = new Symbology(mContext);

		switch(module){
			case SE4710:
			case SE4750:
			case SE4850:
			default:
				_function = new ScannerFunctions_2D(context);
				break;
		}
	}

	public void dispose(){
		_function.dispose();
	}

	@Deprecated
	public Symbology getSymbologyInstance(){
		return mSymbology;
	}

	public void scanStart() {
		_function.decodeStart();
	}

	public void scanDispose() {
		_function.decodeStop();
	}
	private boolean isScannerEnable() {
		return _function.isEnable();
	}

	public boolean isEnable() {
		return _function.isEnable();
	}

	public void setScanner(boolean enable) {
		_function.setScanner(enable);
	}

	public void addBarcodeListener(BarcodeListener2 bl){
		_function.addListener(bl);
	}

	public void removeBarcodeListener(BarcodeListener2 bl){
		_function.removeListener(bl);
	}

	public void setEndCharMode(DataType.END_CHAR mode){
		_function.setEndCharMode(mode.ordinal());
	}

	public void setOutputMode(DataType.OUTPUT_MODE mode){
		_function.setOutputMode(mode.ordinal());
	}

	public void setPrefix(String prefix){
		_function.setPrefix(prefix);
	}

	public void setPostfix(String postfix){
		_function.setPostfix(postfix);
	}

	public void setSoundMode(DataType.SOUND_MODE mode){
		_function.setSoundMode(mode.ordinal());
	}

	public void setVibration(boolean isOn){
		_function.setVibration(isOn);
	}

	public void setReadMode(DataType.READ_MODE mode){
		_function.setReadMode(mode.ordinal());
	}

	public void setMultipleCount(int count){
		_function.setMultipleCount(count);
	}

	// 0: Enable, 1: all Disable (Function Call and Trigger Key), 2: Key Disable
	public void setScannerTriggerMode(DataType.SCAN_TRIGGER mode){
		_function.setScannerTriggerMode(mode.ordinal());
	}

	public int setScanParameter(int num, int val){
		return _function.setScanParameter(num, val);
	}

	public int getScanParameter(int num){
		return _function.getScanParameter(num);
	}

	public void enableAllCodeType(){
		mContext.sendOrderedBroadcast(
				new Intent(Barcode_old.SCN_CUST_ACTION_SETTING_CHANGE)
						.putExtra("setting", "enable_all_types")
				, null
		);
	}

	public void disableAllCodeType(){
		mContext.sendOrderedBroadcast(
				new Intent(Barcode_old.SCN_CUST_ACTION_SETTING_CHANGE)
					.putExtra("setting", "disable_all_types")
				, null
		);
	}

	@Deprecated
	protected void setBarcodeAll(byte[] params) {
		Intent intent = new Intent(
				"com.android.server.scannerservice.setallparameter");
		intent.putExtra("scannersetallparameter", params);
		intent.putExtra("scannersetallparameterlen", 17);
		mContext.sendBroadcast(intent);
	}

	@Deprecated
	protected void setBarcodeOpenAll() {
		Intent intent = new Intent(
				"com.android.server.scannerservice.setallparameter");
		intent.putExtra("scannersetallparameter", new byte[] { 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
		intent.putExtra("scannersetallparameterlen", 17);
		mContext.sendBroadcast(intent);
	}

	@Deprecated
	protected void setBarcodeCloseAll() {
		Intent intent = new Intent(
				"com.android.server.scannerservice.setallparameter");
		intent.putExtra("scannersetallparameter", new byte[] { 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		intent.putExtra("scannersetallparameterlen", 17);
		mContext.sendBroadcast(intent);
	}

	@Deprecated
	public static class Symbology{

		public static final String SCN_CUST_ACTION_PARAM = "android.intent.action.SCANNER_PARAMETER";
		
		public static class UPC_A{
			public static final int nCode = 1;
			public static int nValue;			
		}		
		
		public static class UPC_E{
			public static final int nCode = 2;
			public static int nValue = 0;					
		}
		
		public static class UPC_E1{
			public static final int nCode = 12;
			public static int nValue = 0;		
		}

		public static class EAN_8{
			public static final int nCode = 4;
			public static int nValue = 0;					
		}
		public static class EAN_13{
			public static final int nCode = 3;
			public static int nValue = 0;					
		}
		public static class CODABAR{
			public static final int nCode = 7;
			public static int nValue = 0;					
		}
		public static class CODE_39{
			public static final int nCode = 0;
			public static int nValue = 0;					
		}
		public static class CODE_128{
			public static final int nCode = 8;
			public static int nValue = 0;					
		}
		public static class CODE_93{
			public static final int nCode = 9;
			public static int nValue = 0;					
		}
		public static class CODE_11{
			public static final int nCode = 10;
			public static int nValue = 0;					
		}
		public static class MSI{
			public static final int nCode = 11;
			public static int nValue = 0;					
		}
		public static class Interleaved_2of5{
			public static final int nCode = 6;
			public static int nValue = 0;					
		}
		public static class Discrete_2of5{
			public static final int nCode = 5;
			public static int nValue = 0;					
		}
		public static class Chinese_2of5{
			public static final int nCode = 408;
			public static int nValue = 0;					
		}
		public static class GS1_DATABAR_14{
			public static final int nCode = 338;
			public static int nValue = 0;					
		}
		public static class GS1_DATABAR_LIMITED{
			public static final int nCode = 339;
			public static int nValue = 0;					
		}
		public static class GS1_DATABAR_EXPANED{
			public static final int nCode = 340;
			public static int nValue = 0;					
		}
		private int [] symbol = {
				UPC_A.nCode, UPC_E.nCode, UPC_E1.nCode, EAN_8.nCode,
				EAN_13.nCode, CODABAR.nCode, CODE_39.nCode, CODE_128.nCode, CODE_93.nCode,
				CODE_11.nCode, MSI.nCode, Interleaved_2of5.nCode, Discrete_2of5.nCode,
				Chinese_2of5.nCode, GS1_DATABAR_14.nCode, GS1_DATABAR_EXPANED.nCode, GS1_DATABAR_LIMITED.nCode
				};

		private Context mContext = null;
		private ScannerFunctions _functions = null;
		
		protected Symbology(Context context) {
			mContext = context;			
		}
		protected Symbology(ScannerFunctions functions){
			_functions = functions;
		}
		
		public boolean setSymbology(int symbology, int paramVal) {
			boolean ret = false;
			
			for(int i = 0; i<symbol.length; i++)
			{
				if(symbol[i] == symbology){
					ret = true;
					break;
				}
			}
			
			setCodeType(symbology, paramVal);

			if(_functions != null){
				ret = (_functions.setScanParameter(symbology, paramVal) != -1);
			}else if(mContext != null){
				Intent intent = new Intent(SCN_CUST_ACTION_PARAM);
				intent.putExtra("symbology", symbology);
				intent.putExtra("value", paramVal);
				mContext.sendOrderedBroadcast(intent, null);
			}

			Log.i(TAG,"setSymbology ["+ symbology + "][" + paramVal + "]");	

			return ret;
		}

		public int getSymbology(int symbology)
		{
			int nValue = 0;
			nValue = getCodeType(symbology);
			Log.i(TAG,"getSymbology ["+ symbology + "][" + nValue + "]");

			if(_functions != null){
				nValue = _functions.getScanParameter(symbology);
			}else if(mContext != null){
				Intent intent = new Intent(SCN_CUST_ACTION_PARAM);
				intent.putExtra("symbology", symbology);
				intent.putExtra("value", -1);
				mContext.sendOrderedBroadcast(intent, null);
			}
						
			return nValue;
		}
		
		protected static Boolean setCodeType(int Symbology, int value)
		{
			Boolean bRet = true;
			
			switch(Symbology)
			{
			case UPC_A.nCode:
				UPC_A.nValue = value;
				break;
			case UPC_E.nCode:
				UPC_E.nValue = value;
				break;
			case UPC_E1.nCode:
				UPC_E1.nValue = value;
				break;
			case EAN_8.nCode:
				EAN_8.nValue = value;
				break;
			case EAN_13.nCode:
				EAN_13.nValue = value;
				break;
			case CODABAR.nCode:
				CODABAR.nValue = value;
				break;
			case CODE_39.nCode:
				CODE_39.nValue = value;
				break;
			case CODE_128.nCode:
				CODE_128.nValue = value;
				break;
			case CODE_93.nCode:
				CODE_93.nValue = value;
				break;
			case CODE_11.nCode:
				CODE_11.nValue = value;
				break;
			case MSI.nCode:
				MSI.nValue = value;
				break;
			case Interleaved_2of5.nCode:
				Interleaved_2of5.nValue = value;
				break;
			case Discrete_2of5.nCode:
				Discrete_2of5.nValue = value;
				break;
			case Chinese_2of5.nCode:
				Chinese_2of5.nValue = value;
				break;
			case GS1_DATABAR_14.nCode:
				GS1_DATABAR_14.nValue = value;
				break;
			case GS1_DATABAR_EXPANED.nCode:
				GS1_DATABAR_EXPANED.nValue = value;
				break;
			case GS1_DATABAR_LIMITED.nCode:
				GS1_DATABAR_LIMITED.nValue = value;
				break;
			default:
				bRet = false;
				break;
			}
			
			return bRet;
		}

		protected static int getCodeType(int Symbology)
		{
			int nRetValue = 0;
			
			switch(Symbology)
			{
			case UPC_A.nCode:
				nRetValue=UPC_A.nValue;
				break;
			case UPC_E.nCode:
				nRetValue=UPC_E.nValue;
				break;
			case UPC_E1.nCode:
				nRetValue=UPC_E1.nValue;
				break;
			case EAN_8.nCode:
				nRetValue=EAN_8.nValue;
				break;
			case EAN_13.nCode:
				nRetValue=EAN_13.nValue;
				break;
			case CODABAR.nCode:
				nRetValue=CODABAR.nValue;
				break;
			case CODE_39.nCode:
				nRetValue=CODE_39.nValue;
				break;
			case CODE_128.nCode:
				nRetValue=CODE_128.nValue;
				break;
			case CODE_93.nCode:
				nRetValue=CODE_93.nValue;
				break;
			case CODE_11.nCode:
				nRetValue=CODE_11.nValue;
				break;
			case MSI.nCode:
				nRetValue=MSI.nValue;
				break;
			case Interleaved_2of5.nCode:
				nRetValue=Interleaved_2of5.nValue;
				break;
			case Discrete_2of5.nCode:
				nRetValue=Discrete_2of5.nValue;
				break;
			case Chinese_2of5.nCode:
				nRetValue=Chinese_2of5.nValue;
				break;
			case GS1_DATABAR_14.nCode:
				nRetValue=GS1_DATABAR_14.nValue;
				break;
			case GS1_DATABAR_EXPANED.nCode:
				nRetValue=GS1_DATABAR_EXPANED.nValue;
				break;
			case GS1_DATABAR_LIMITED.nCode:
				nRetValue=GS1_DATABAR_LIMITED.nValue;
				break;
			}

			Log.i(TAG,"getCodeType ["+ Symbology + "][" + nRetValue + "]");	
			
			return nRetValue;
		}
		
	}
}
