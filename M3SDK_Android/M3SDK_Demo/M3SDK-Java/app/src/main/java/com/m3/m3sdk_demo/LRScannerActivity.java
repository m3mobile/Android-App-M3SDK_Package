package com.m3.m3sdk_demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class LRScannerActivity extends Activity {


	protected static final String TAG = "LRScannerActivity";

	Context mContext;
	private TextView mTvResult = null;
	private EditText edSymNum = null;
	private EditText edValNum = null;
	private EditText edPrefix;
	private EditText edPostfix;

	private EditText edSnapShotPath;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanner_lr);
		mContext = this;

		Button btnStart = (Button)findViewById(R.id.startread_intent);
		btnStart.setOnClickListener(OnButtonClickListener);
		Button btnStop = (Button)findViewById(R.id.stopread_intent);
		btnStop.setOnClickListener(OnButtonClickListener);
		Button btnEnable = (Button)findViewById(R.id.enable_intent);
		btnEnable.setOnClickListener(OnButtonClickListener);
		Button btnDisable = (Button)findViewById(R.id.disable_intent);
		btnDisable.setOnClickListener(OnButtonClickListener);
		Button btnSnapShot = (Button)findViewById(R.id.button_snapShot);
		btnSnapShot.setOnClickListener(OnButtonClickListener);
		Button btnIsOpend = (Button)findViewById(R.id.is_opened_intent);
		btnIsOpend.setOnClickListener(OnButtonClickListener);

		mTvResult = (TextView)findViewById(R.id.scanresult_intent);
		edSymNum = (EditText)findViewById(R.id.editPnum_intent);
		edValNum = (EditText)findViewById(R.id.editPval_intent);
		edSnapShotPath = (EditText)findViewById(R.id.edit_snapshot_path);
		edSnapShotPath.setText(getString(R.string.snapshot_path_example));

		// sound
		RadioButton sound_none = (RadioButton)findViewById(R.id.sound_none);
		RadioButton sound_beep = (RadioButton)findViewById(R.id.sound_beep);
		RadioButton sound_dingdong = (RadioButton)findViewById(R.id.sound_dingdong);
		sound_none.setOnClickListener(OnSoundClickListener);
		sound_beep.setOnClickListener(OnSoundClickListener);
		sound_dingdong.setOnClickListener(OnSoundClickListener);
		sound_beep.setChecked(true);

		// vibration
		CheckBox vib = (CheckBox)findViewById(R.id.vibration_mode);
		vib.setOnCheckedChangeListener(OnVibrationCheckListener);
		vib.setChecked(true);

		// output mode
		RadioButton output_cnp = (RadioButton)findViewById(R.id.output_mode_copyandpaste);
		RadioButton output_key = (RadioButton)findViewById(R.id.output_mode_key);
		RadioButton output_none = (RadioButton)findViewById(R.id.output_mode_none);
		output_cnp.setOnClickListener(OnOutputClickListener);
		output_key.setOnClickListener(OnOutputClickListener);
		output_none.setOnClickListener(OnOutputClickListener);
		output_key.setChecked(true);

		// end char
		RadioButton end_enter = (RadioButton)findViewById(R.id.end_enter);
		RadioButton end_space = (RadioButton)findViewById(R.id.end_space);
		RadioButton end_tab = (RadioButton)findViewById(R.id.end_tab);
		RadioButton end_none = (RadioButton)findViewById(R.id.end_none);
		RadioButton end_keyEnter = (RadioButton)findViewById(R.id.end_key_enter);
		RadioButton end_KeySpace = (RadioButton)findViewById(R.id.end_key_space);
		RadioButton end_keyTab = (RadioButton)findViewById(R.id.end_key_tab);
		end_enter.setOnClickListener(OnEndClickListener);
		end_space.setOnClickListener(OnEndClickListener);
		end_tab.setOnClickListener(OnEndClickListener);
		end_none.setOnClickListener(OnEndClickListener);
		end_keyEnter.setOnClickListener(OnEndClickListener);
		end_KeySpace.setOnClickListener(OnEndClickListener);
		end_keyTab.setOnClickListener(OnEndClickListener);
		end_enter.setChecked(true);

		// fix
		edPrefix = (EditText)findViewById(R.id.edit_prefix);
		edPostfix = (EditText)findViewById(R.id.edit_postfix);
		Button btnFix = (Button)findViewById(R.id.buttonSet_fix);
		btnFix.setOnClickListener(OnFixClickListener);

		// intent filter
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConstantValues.SCANNER_ACTION_BARCODE);
		filter.addAction(ConstantValues.LRSCANNER_ACTION_STATUS);
		filter.addAction(ConstantValues.LRSCANNER_ACTION_TAKE_PICTURE_RESULT);
		registerReceiver(BarcodeIntentBroadcast,filter);

	}

	OnClickListener OnButtonClickListener = new OnClickListener() {
		public void onClick(View v) {
			int nButtonId = v.getId();
			Intent intent = null;
			if (nButtonId == R.id.startread_intent) {
				intent = new Intent(ConstantValues.LRSCANNER_ACTION_START, null);
			} else if (nButtonId == R.id.stopread_intent) {
				intent = new Intent(ConstantValues.LRSCANNER_ACTION_CANCEL, null);
			} else if (nButtonId == R.id.enable_intent) {
				intent = new Intent(ConstantValues.LRSCANNER_ACTION_ENABLE, null);
				intent.putExtra(ConstantValues.SCANNER_EXTRA_ENABLE, 1);
			} else if (nButtonId == R.id.disable_intent) {
				intent = new Intent(ConstantValues.LRSCANNER_ACTION_ENABLE, null);
				intent.putExtra(ConstantValues.SCANNER_EXTRA_ENABLE, 0);
			} else if (nButtonId == R.id.button_snapShot) {
				String strPath = edSnapShotPath.getText().toString();
				intent = new Intent(ConstantValues.LRSCANNER_ACTION_TAKE_PICTURE, null);
				intent.putExtra(ConstantValues.SCANNER_EXTRA_TAKE_PICTURE_PATH, strPath);
			} else if (nButtonId == R.id.is_opened_intent) {
				Log.i(TAG, "is Opened");
				intent = new Intent(ConstantValues.LRSCANNER_ACTION_IS_ENABLE);
			}
			mContext.sendOrderedBroadcast(intent, null);
		}
	};

	OnClickListener OnSoundClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(ConstantValues.LRSCANNER_ACTION_SETTING_CHANGE);
			intent.putExtra("setting", "sound");
			int id = v.getId();
			if (id == R.id.sound_none) {
				intent.putExtra("sound_mode", 0);
			} else if (id == R.id.sound_beep) {
				intent.putExtra("sound_mode", 1);
			} else if (id == R.id.sound_dingdong) {
				intent.putExtra("sound_mode", 2);
			}
			mContext.sendOrderedBroadcast(intent, null);
		}
	};

	CheckBox.OnCheckedChangeListener OnVibrationCheckListener = new CheckBox.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton button, boolean isChecked) {

			Intent intent = new Intent(ConstantValues.LRSCANNER_ACTION_SETTING_CHANGE);
			intent.putExtra("setting", "vibration");
			intent.putExtra("vibration_value", isChecked?1:0);
			mContext.sendOrderedBroadcast(intent, null);

		}

	};

	OnClickListener OnOutputClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(ConstantValues.LRSCANNER_ACTION_SETTING_CHANGE);
			intent.putExtra("setting", "output_mode");
			int id = v.getId();
			if (id == R.id.output_mode_copyandpaste) {
				intent.putExtra("output_mode_value", 0);
			} else if (id == R.id.output_mode_key) {
				intent.putExtra("output_mode_value", 1);
			} else if (id == R.id.output_mode_none) {
				intent.putExtra("output_mode_value", 2);
			}
			mContext.sendOrderedBroadcast(intent, null);
		}
	};



	OnClickListener OnEndClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(ConstantValues.LRSCANNER_ACTION_SETTING_CHANGE);
			intent.putExtra("setting", "end_char");
			int id = v.getId();
			if (id == R.id.end_enter) {
				intent.putExtra("end_char_value", 0);
			} else if (id == R.id.end_space) {
				intent.putExtra("end_char_value", 1);
			} else if (id == R.id.end_tab) {
				intent.putExtra("end_char_value", 2);
			} else if (id == R.id.end_key_enter) {
				intent.putExtra("end_char_value", 3);
			} else if (id == R.id.end_key_space) {
				intent.putExtra("end_char_value", 4);
			} else if (id == R.id.end_key_tab) {
				intent.putExtra("end_char_value", 5);
			} else if (id == R.id.end_none) {
				intent.putExtra("end_char_value", 6);
			}
			mContext.sendOrderedBroadcast(intent, null);
		}
		
	};	
	
	OnClickListener OnFixClickListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			String strPrefix = edPrefix.getText().toString();
			String strPostfix = edPostfix.getText().toString();			

			Intent intent = new Intent(ConstantValues.LRSCANNER_ACTION_SETTING_CHANGE);
			intent.putExtra("setting", "prefix");	
			intent.putExtra("prefix_value", strPrefix);
			mContext.sendOrderedBroadcast(intent, null);	
			
			intent = new Intent(ConstantValues.LRSCANNER_ACTION_SETTING_CHANGE);
			intent.putExtra("setting", "postfix");	
			intent.putExtra("postfix_value", strPostfix);
			mContext.sendOrderedBroadcast(intent, null);
		}
	};
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(BarcodeIntentBroadcast);
		super.onDestroy();
	}

	public BroadcastReceiver BarcodeIntentBroadcast = new BroadcastReceiver(){

		private String barcode;
		private String type;
		private String module;
		private byte[] rawdata;
		private int length;
		private int decCount;

		
		@Override
		public void onReceive(Context content, Intent intent) {
			
			if (intent.getAction().equals(ConstantValues.SCANNER_ACTION_BARCODE)) {
				
				barcode = intent.getExtras().getString(ConstantValues.SCANNER_EXTRA_BARCODE_DATA);
				type = intent.getExtras().getString(ConstantValues.SCANNER_EXTRA_BARCODE_CODE_TYPE);
				module = intent.getExtras().getString(ConstantValues.SCANNER_EXTRA_MODULE_TYPE);
				try {
					rawdata = intent.getExtras().getByteArray(ConstantValues.SCANNER_EXTRA_BARCODE_RAW_DATA);
				} catch (Exception e) {
					Log.d(TAG, "onReceive scanner - null raw data");
				}
				length = intent.getExtras().getInt(ConstantValues.SCANNER_EXTRA_BARCODE_DATA_LENGTH, 0);
				decCount = intent.getExtras().getInt(ConstantValues.SCANNER_EXTRA_BARCODE_DEC_COUNT, 0);
				
				if(barcode != null)
				{
					
					if(rawdata.length > 0){

						String strRawData = "";
						for(int i = 0; i< rawdata.length; i++){
							strRawData += String.format("0x%02X ", (int)rawdata[i]&0xFF);		
						}
						
						mTvResult.setText("data: " + barcode + " \ntype: " + type + " \nraw: " + strRawData);	
						
						
					}else{
						mTvResult.setText("data: " + barcode + " type: " + type);
					}
				}
				else
				{
					int nSymbol = intent.getExtras().getInt("symbology", -1);
					int nValue = intent.getExtras().getInt("value", -1);

					Log.i(TAG,"getSymbology ["+ nSymbol + "][" + nValue + "]");	
					
					if(nSymbol != -1)
					{
						edSymNum.setText(Integer.toString(nSymbol));						
						edValNum.setText(Integer.toString(nValue));
					}
				}	
				
			}else if(intent.getAction().equals(ConstantValues.LRSCANNER_ACTION_STATUS)){
				String strMessage = "";
				module = intent.getStringExtra(ConstantValues.SCANNER_EXTRA_MODULE_TYPE);
				int nStatus = intent.getIntExtra(ConstantValues.SCANNER_EXTRA_STATUS, 0);
				Log.i(TAG, "Status: " + nStatus + (nStatus & ConstantValues.SCANNER_STATUS_SCANNER_OPEN_SUCCESS));
				if((nStatus & ConstantValues.SCANNER_STATUS_SCANNER_OPEN_SUCCESS) == ConstantValues.SCANNER_STATUS_SCANNER_OPEN_SUCCESS){
					strMessage += "Scanner Open Success\n";
				}else if((nStatus & ConstantValues.SCANNER_STATUS_SCANNER_OPEN_FAIL) == ConstantValues.SCANNER_STATUS_SCANNER_OPEN_FAIL){
					strMessage += "Scanner Open Fail\n";
				}else if((nStatus & ConstantValues.SCANNER_STATUS_SCANNER_CLOSE_SUCCESS) == ConstantValues.SCANNER_STATUS_SCANNER_CLOSE_SUCCESS){
					strMessage += "Scanner Close Success\n";
				}else if((nStatus & ConstantValues.SCANNER_STATUS_SCANNER_CLOSE_FAIL) == ConstantValues.SCANNER_STATUS_SCANNER_CLOSE_FAIL){
					strMessage += "Scanner Open Fail\n";
				}
				strMessage += "Scanner: " + module;
				mTvResult.setText(strMessage);
			}else if(intent.getAction().equals(ConstantValues.LRSCANNER_ACTION_TAKE_PICTURE_RESULT)){
				boolean bSuccess = intent.getBooleanExtra(ConstantValues.LRSCANNER_EXTRA_TAKE_PICTURE_RESULT, false);

				Toast.makeText(getApplicationContext(), "Capture result: " + bSuccess , Toast.LENGTH_SHORT).show();
			}
		}
		
	};

}
