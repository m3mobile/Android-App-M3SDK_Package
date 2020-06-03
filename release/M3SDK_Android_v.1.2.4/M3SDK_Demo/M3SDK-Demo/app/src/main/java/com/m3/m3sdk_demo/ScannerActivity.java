package com.m3.m3sdk_demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.m3.sdk.scannerlib.Barcode;
import com.m3.sdk.scannerlib.BarcodeListener;
import com.m3.sdk.scannerlib.BarcodeManager;
import com.m3.sdk.scannerlib.Barcode.Symbology;


public class ScannerActivity extends Activity implements OnClickListener {
	// lib
	private Barcode mBarcode = null;
	private BarcodeListener mListener = null;
	private BarcodeManager mManager = null;
	private Symbology mSymbology = null;
	
	//ui
	private TextView mTvResult = null;
	private EditText edSymNum = null;
	private EditText edValNum = null;

	private static String TAG = "ScannerActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanner);
		
		mBarcode = new Barcode(this);
		mManager = new BarcodeManager(this);
		mSymbology = mBarcode.getSymbologyInstance();

		mainScreen();

		Log.i("ScannerTest","onCreate");

		mListener = new BarcodeListener() {

			@Override
			public void onGetSymbology(int nSymbol, int nVal) {
				Log.i("ScannerTest", "onGetSymbology result="+nSymbol + ", "+ nVal);
				edSymNum.setText(Integer.toString(nSymbol));	
				edValNum.setText(Integer.toString(nVal));
				
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
		//mBarcode.setScanner(false);
		
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
		
		mTvResult = (TextView)findViewById(R.id.scanresult);
		edSymNum = (EditText)findViewById(R.id.editPnum);
		edValNum = (EditText)findViewById(R.id.editPval);
	}
	

	@Override
	public void onClick(View vw) {
		int id = vw.getId();

		if(id == R.id.startread){
			mBarcode.scanStart();
		}else if(id == R.id.stopread){
			mBarcode.scanDispose();
		}else if(id == R.id.enable){
			mBarcode.setScanner(true);
		}else if(id == R.id.disable){
			mBarcode.setScanner(false);
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

	// ------------------------------------------------------
	// callback for Set Param button press
	OnClickListener mSetParamListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			setParam();
		}
	};
	// ----------------------------------------
	private void getParam()
	{	

		String s = edSymNum.getText().toString();
				
		try
		{
			int nValue = mSymbology.getSymbology(Integer.parseInt(s));
			
			edValNum.setText(Integer.toString(nValue));
			
		}
		catch (NumberFormatException nx)
		{
			nx.printStackTrace();
		}
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
				
			mSymbology.setSymbology(num,  val);
			
			
		}
		catch (NumberFormatException nx)
		{
			nx.printStackTrace();		
		}
	}

}
