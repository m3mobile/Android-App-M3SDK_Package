package com.m3.m3sdk_demo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.m3.sdk.scannerlib.Barcode;
import com.m3.sdk.scannerlib.Barcode.Symbology;
import com.m3.sdk.scannerlib.BarcodeListener;
import com.m3.sdk.scannerlib.BarcodeManager;

import net.m3mobile.app.scannerservicez1d.IScannerServiceZebra1D;

import java.util.List;


public class Scanner1DAidlActivity extends Activity implements OnClickListener, ServiceConnection {
	// lib
	private Barcode mBarcode = null;
	private BarcodeListener mListener = null;
	private BarcodeManager mManager = null;
	private Symbology mSymbology = null;
	
	//ui
	private TextView mTvResult = null;
	private EditText edSymNum = null;
	private EditText edValNum = null;

	private EditText edSymByte = null;
	private EditText edValNum2 = null;

	private static String TAG = "ScannerAidlActivity";
	private IScannerServiceZebra1D m1DService = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanner);
		
		mBarcode = new Barcode(this);
		mManager = new BarcodeManager(this);
		mSymbology = mBarcode.getSymbologyInstance();
		//mBarcode.setScanner(true);
		
		mainScreen();

		Log.i("ScannerTest","onCreate");

		bindScannerService();
		
		mListener = new BarcodeListener() {

			@Override
			public void onGetSymbology(int nSymbol, int nVal) {
				Log.i("ScannerTest", "onGetSymbology result="+nSymbol + ", "+ nVal);
			}

			@Override
			public void onBarcode(String strBarcode) {
				Log.i("ScannerTest1","result="+strBarcode);
				// mTvResult.setText(strBarcode);
			}

			@Override
			public void onBarcode(String barcode, String codeType) {
				Log.i("ScannerTest2","result="+barcode);
				mTvResult.setText("data: " + barcode + " type: " + codeType);
			}		
			
		};

		mManager.addListener(mListener);


	}

	@Override
	protected void onDestroy() {

		mManager.removeListener(mListener);
		mManager.dismiss();
		
		super.onDestroy();
	}

	protected void mainScreen()
	{
		Button btnStart = (Button)findViewById(R.id.startread);
		btnStart.setOnClickListener(this);        
		Button btnStop = (Button)findViewById(R.id.stopread);
		btnStop.setOnClickListener(this);
		Button btnEnable = (Button)findViewById(R.id.enable);
		btnEnable.setOnClickListener(this);        
		Button btnDisable = (Button)findViewById(R.id.disable);
		btnDisable.setOnClickListener(this);
		
		Button btnGetSym = (Button)findViewById(R.id.buttonGet);
		btnGetSym.setOnClickListener(mGetParamListener);
		Button btnSetSym = (Button)findViewById(R.id.buttonSet);
		btnSetSym.setOnClickListener(mSetParamListener);
		Button btnGetByteSym = (Button)findViewById(R.id.buttonGet2);
        btnGetByteSym.setOnClickListener(mGetByteParamListener);
		Button btnSetByteSym = (Button)findViewById(R.id.buttonSet2);
		btnSetByteSym.setOnClickListener(mSetByteParamListener);
		
		mTvResult = (TextView)findViewById(R.id.scanresult);
		edSymNum = (EditText)findViewById(R.id.editPnum);
		edValNum = (EditText)findViewById(R.id.editPval);
		edSymByte = (EditText)findViewById(R.id.editByte);
		edValNum2 = (EditText)findViewById(R.id.editPval2);

		findViewById(R.id.ll_params_byte).setVisibility(View.VISIBLE);
	}
	

	@Override
	public void onClick(View vw) {
		int id = vw.getId();

		try {
			if(id == R.id.startread){
				m1DService.decodeStart();
			}else if(id == R.id.stopread){
				m1DService.decodeStop();
			}else if(id == R.id.enable){
				m1DService.setScanner(true);
			}else if(id == R.id.disable){
				m1DService.setScanner(false);
			}
		} catch (RemoteException e) {
			Log.d(TAG, "onClick error");
			e.printStackTrace();
		}
		
	}


	// ------------------------------------------------------
	// callback Get Param for button press
	OnClickListener mGetParamListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			getParam();
		}
	};

    OnClickListener mGetByteParamListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            getByteParam();
        }
    };

	// ------------------------------------------------------
	// callback for Set Param button press
	OnClickListener mSetParamListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			setParam();
		}
	};

	OnClickListener mSetByteParamListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			setByteParam();
		}
	};
	// ----------------------------------------
	private void getParam()
	{
		String s = edSymNum.getText().toString();

		try
		{
			int nValue = m1DService.getScanParameter(Integer.parseInt(s));
			edValNum.setText(Integer.toString(nValue));
		}
		catch (NumberFormatException nx)
		{
			nx.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

    private void getByteParam()
    {
        String s = edSymByte.getText().toString();

        try
        {
            byte[] values = m1DService.getParamRequest(hexStringToByteArray(s));
			edValNum2.setText(byteArrayToHexString(values));
        }
        catch (NumberFormatException nx)
        {
            nx.printStackTrace();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	public static String byteArrayToHexString(byte[] bytes){
		StringBuilder sb = new StringBuilder();
		for(byte b : bytes){
			sb.append(String.format("%02X", b&0xff));
		}
		return sb.toString();
	}

	// ----------------------------------------
	private void setParam()
	{
		// get param #
		String sn = edSymNum.getText().toString();
		String sv = edValNum.getText().toString();
		try
		{
			int num = Integer.parseInt(sn);
			int val = Integer.parseInt(sv);
				
			int nResult = m1DService.setScanParameter(num,  val);
			Log.d(TAG, "setScanParameter result '" + nResult);

			/*
			 * Function completed successfully
			 public static final int BCR_SUCCESS = 0;
			 *
			 * Function failed
			 public static final int BCR_ERROR = -1;
			 */
		}
		catch (NumberFormatException nx)
		{
			nx.printStackTrace();		
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void setByteParam()
	{
		// get param #
		String sn = edSymByte.getText().toString();
		String sv = edValNum2.getText().toString();
		try
		{
			int val = Integer.parseInt(sv);

			byte[] result = m1DService.setParamRequest(hexStringToByteArray(sn),  hexStringToByteArray(sv));
			Log.d(TAG, "setScanParameter result '" + result);

		}
		catch (NumberFormatException nx)
		{
			nx.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void bindScannerService(){
		Intent intent = null;
		intent = new Intent("net.m3mobile.app.scannerservicezebra1d.start");
		intent.setPackage(get1DAIDLPackageName());
		boolean bBind = bindService(intent,this, Context.BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT);
		Log.d(TAG, "bindScannerService " + bBind);
	}

	@Override
	public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
		Log.d(TAG, "onServiceConnected " + iBinder.getClass().getSimpleName());
		m1DService = IScannerServiceZebra1D.Stub.asInterface(iBinder);

		// test aidl
		try {
			/**
			 * end char mode
			 public static final int END_CHAR_ENTER = 0;
			 public static final int END_CHAR_SPACE = 1;
			 public static final int END_CHAR_TAB = 2;
			 public static final int END_CHAR_KEY_ENTER = 3;
			 public static final int END_CHAR_KEY_SPACE = 4;
			 public static final int END_CHAR_KEY_TAB = 5;
			 public static final int END_CHAR_NONE = 6;
			 */
			m1DService.setEndCharMode(1);
			m1DService.setPrefix("pre_");
			m1DService.setPostfix("_suffix");
			/**
			 * SOUND MODE
			 public static final int SOUND_NONE = 0;
			 public static final int SOUND_BEEP = 1;
			 public static final int SOUND_DING_DONG=2;
			 */
			m1DService.setSoundMode(1);
			m1DService.setVibration(true);

			/**
			 * OUTPUT MODE
			 public static final int OUTPUT_DIRECT = 0;
			 public static final int OUTPUT_EMU_KEY = 1;
			 public static final int OUTPUT_CLIPBOARD = 2;
			 */
			m1DService.setOutputMode(1);
			/**
			 * READ MODE
			 public static final int READ_ASYNC = 0;
			 public static final int READ_SYNC = 1;
			 public static final int READ_CONTINUE = 2;
			 */
			m1DService.setReadMode(1);


			/**
			 * setScannerTriggerMode
			 0 : enable
			 1 : can't use scanner key
			 2 : using only calling api
			 */
			m1DService.setScannerTriggerMode(0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName componentName) {
		Log.d(TAG, "onServiceDisconnected " + componentName.getClassName());
	}

	private String get1DAIDLPackageName() {
		String packageName = "net.m3mobile.app.scanemul";
		PackageManager manager = getPackageManager();
		List<ApplicationInfo> list = manager.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo info : list) {
			try {
				if(info.packageName.equals("net.m3mobile.app.scannerservicez1d"))
					packageName = "net.m3mobile.app.scannerservicez1d";
			} catch (Exception e) {
				Log.e(TAG, "getPackageType Exception : " + e.getMessage());
			}
		}
		return packageName;
	}
}
