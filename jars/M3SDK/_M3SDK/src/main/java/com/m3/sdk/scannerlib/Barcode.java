/*
 * v.1.2.0	2016-09-09	���翵		SM10 LTE ������ Key �� Scanner �� �и���
 */
package com.m3.sdk.scannerlib;

import java.sql.Struct;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

public class Barcode {


	public static final String SCN_CUST_ACTION_SWITCH = "com.android.server.scannerservice.m3onoff";
	public static final String SCN_CUST_EX_SWITCH = "scanneronoff";
	public static final String SCN_CUST_ACTION_ICON_STATE = "android.intent.action.ACTION_SCANNER_ENABLE";
	public static final String SCN_CUST_ACTION_START = "android.intent.action.M3SCANNER_BUTTON_DOWN";
	public static final String SCN_CUST_ACTION_CANCEL = "android.intent.action.M3SCANNER_BUTTON_UP";

	public static final String SCANNER_BARCODE_DATA = "m3scannerdata";
	public static final String SCANNER_BARCODE_CODE_TYPE = "m3scanner_code_type";
	public static final String SCANNER_MODULE_TYPE = "m3scanner_module_type";

	public static final String SCN_CUST_ACTION_SETTING_CHANGE = "com.android.server.scannerservice.settingchange";

	public static final String SCN_CUST_ACTION_PARAM = "android.intent.action.SCANNER_PARAMETER";
	public static final String SCN_CUST_ACTION_SCODE = "com.android.server.scannerservice.broadcast";

	private static String TAG = "Barcode";
	
	private Context mContext;
	private Symbology mSymbology;
	
	public Barcode(Context mContext) {
		this.mContext = mContext;
		mSymbology = new Symbology(mContext);
	}
	
	public Symbology getSymbologyInstance(){
		return mSymbology;
	}

	public void scanStart() {
		Intent intent = new Intent(SCN_CUST_ACTION_START,
				null);
		mContext.sendOrderedBroadcast(intent, null);
	}

	public void scanDispose() {
		Intent intent = new Intent(SCN_CUST_ACTION_CANCEL,
				null);   
		mContext.sendOrderedBroadcast(intent, null);
	}
	private boolean isScannerEnable() {
		int enable = Settings.System.getInt(mContext.getContentResolver(),
				"M3SCANNER_POWER_ON", 1);
		if (enable == 1)
			return true;
		else
			return false;
	}

	public void setScanner(boolean enable) {
		int onOff = 0;
		if (enable)
			onOff = 1;
		Intent intentSwitch = new Intent(SCN_CUST_ACTION_SWITCH,
				null);   
		intentSwitch.putExtra(SCN_CUST_EX_SWITCH, onOff);
		
		mContext.sendOrderedBroadcast(intentSwitch, null);
				
	}
	
	protected void setBarcodeAll(byte[] params) {
		Intent intent = new Intent(
				"com.android.server.scannerservice.setallparameter");
		intent.putExtra("scannersetallparameter", params);
		intent.putExtra("scannersetallparameterlen", 17);
		mContext.sendBroadcast(intent);
	}

	protected void setBarcodeOpenAll() {
		Intent intent = new Intent(
				"com.android.server.scannerservice.setallparameter");
		intent.putExtra("scannersetallparameter", new byte[] { 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
		intent.putExtra("scannersetallparameterlen", 17);
		mContext.sendBroadcast(intent);
	}

	protected void setBarcodeCloseAll() {
		Intent intent = new Intent(
				"com.android.server.scannerservice.setallparameter");
		intent.putExtra("scannersetallparameter", new byte[] { 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		intent.putExtra("scannersetallparameterlen", 17);
		mContext.sendBroadcast(intent);
	}

	
	public static class Symbology{
		
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
		
		protected Symbology(Context context) {
			mContext = context;			
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
			
			Intent intent = new Intent(SCN_CUST_ACTION_PARAM);
			intent.putExtra("symbology", symbology);
			intent.putExtra("value", paramVal);
			
			Log.i(TAG,"setSymbology ["+ symbology + "][" + paramVal + "]");	
			
			mContext.sendOrderedBroadcast(intent, null);
			
			return ret;
		}

		public int getSymbology(int symbology)
		{					

			int nValue = 0;
			
			nValue = getCodeType(symbology);
			
			Log.i(TAG,"getSymbology ["+ symbology + "][" + nValue + "]");	
			
			Intent intent = new Intent(SCN_CUST_ACTION_PARAM);
			intent.putExtra("symbology", symbology);
			intent.putExtra("value", -1);
			mContext.sendOrderedBroadcast(intent, null);
						
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
