package com.m3.m3sdk_demo;

import java.util.ArrayList;

import com.m3.sdk.key.KeyRemap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class KeyActivity extends Activity implements OnClickListener{

	private Button mApply;
	private Button mDefault;
	
	private Spinner mIndexSpin;
	private Spinner mKeycodeSpin;

	private ArrayList<Integer> mKeycodeArrayList;
	private int mIndex =0;
	private int mKeycode = 0;

	private KeyRemap mKey;
	
	private String TAG = "KeyActivity";

	private void showToast(CharSequence msg) {
		if (true)
			Log.v(TAG, msg.toString());
		if (false)
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_key);

		mKey = new KeyRemap(this);
		
		mApply = (Button) findViewById(R.id.btn_key_apply);
		mDefault = (Button) findViewById(R.id.btn_key_default);
		
		mApply.setOnClickListener(this);
		mDefault.setOnClickListener(this);
		
		mKeycodeArrayList = new ArrayList<Integer>();
		mKeycodeArrayList.add(KeyRemap.getDisableKeyCode()); // disable
		mKeycodeArrayList.add(KeyRemap.getScanKeyCode()); // scan	
		mKeycodeArrayList.add(KeyRemap.getCamKeyCode()); // cam
		mKeycodeArrayList.add(KeyRemap.getMenuKeyCode()); // menu
		mKeycodeArrayList.add(KeyRemap.getHomeKeyCode()); // Home
		mKeycodeArrayList.add(KeyRemap.getBackKeyCode()); // back
		mKeycodeArrayList.add(KeyRemap.getVolDownKeyCode()); // volume down
		mKeycodeArrayList.add(KeyRemap.getVolUpKeyCode()); // volume up
		mKeycodeArrayList.add(KeyRemap.getSearchKeyCode()); // SEARCH
		mKeycodeArrayList.add(KeyRemap.getFunctionKeyCode()); // FUNCTION
		mKeycodeArrayList.add(KeyRemap.getF1KeyCode()); // F1
		mKeycodeArrayList.add(KeyRemap.getF2KeyCode()); // F2
		mKeycodeArrayList.add(KeyRemap.getF3KeyCode()); // F3
		mKeycodeArrayList.add(KeyRemap.getF4KeyCode()); // F4
		mKeycodeArrayList.add(KeyRemap.getF5KeyCode()); // F5
		mKeycodeArrayList.add(KeyRemap.getF6KeyCode()); // F6
		mKeycodeArrayList.add(KeyRemap.getF7KeyCode()); // F7
		mKeycodeArrayList.add(KeyRemap.getF8KeyCode()); // F8

		mIndexSpin = (Spinner) this.findViewById(R.id.spinner_key_idx);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.indexes,
				android.R.layout.simple_spinner_dropdown_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mIndexSpin.setAdapter(adapter);
		mIndexSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mIndex = position;
				showToast("mIndexSpin: position=" + position + " id=" + id
						+ " mIndex=" + mIndex);
				int nCode = 0;
				switch(mIndex){
				case 0: // Vol+
					nCode = mKey.VolUp.getKey();
					break;
				case 1: // RScan
					nCode = mKey.RScan.getKey();
					break;
				case 2: // Cam
					nCode = mKey.Cam.getKey();
					break;
				case 3: 
					nCode = mKey.LScan.getKey();
					break;
				case 4: 
					nCode = mKey.VolDown.getKey();
					break;
				case 5:
					nCode = mKey.Home.getKey();
					break;
				case 6:
					nCode = mKey.Back.getKey();
					break;
				case 7:
					nCode = mKey.Menu.getKey();
					break;
				case 8: 
					nCode = mKey.Action.getKey();
					break;
				}
				
				mKeycodeSpin.setSelection(mKeycodeArrayList.indexOf(nCode));

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				showToast("mIndexSpin: Nothing");
			}
		});

		mKeycodeSpin = (Spinner) this.findViewById(R.id.spinner_key_value);
		adapter = ArrayAdapter.createFromResource(this, R.array.keycodes,
				android.R.layout.simple_spinner_dropdown_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mKeycodeSpin.setAdapter(adapter);
		mKeycodeSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mKeycode = mKeycodeArrayList.get(position);
				showToast("mKeycodeSpin: position=" + position + " code=" + mKeycode);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				showToast("mKeycodeSpin: Nothing");
			}
		});

		mKey.registerKeyListener(listener);
	}

	
	@Override
	public void onClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_key_apply) {
			showToast("onClick apply mIndex: " + mIndex + " / mKeycode: " + mKeycode);
			if (mIndex == 0) { // Vol+
				mKey.VolUp.setKey(mKeycode);
			} else if (mIndex == 1) {
				mKey.RScan.setKey(mKeycode);
			} else if (mIndex == 2) {
				mKey.Cam.setKey(mKeycode);
			} else if (mIndex == 3) {
				mKey.Cam.setKey(mKeycode);
			} else if (mIndex == 4) {
				mKey.Cam.setKey(mKeycode);
			} else if (mIndex == 5) {
				mKey.Cam.setKey(mKeycode);
			} else if (mIndex == 6) {
				mKey.Cam.setKey(mKeycode);
			} else if (mIndex == 7) {
				mKey.Cam.setKey(mKeycode);
			} else if (mIndex == 8) {
				mKey.Cam.setKey(mKeycode);
			}
		} else if (id == R.id.btn_key_default) {
			mKey.VolUp.setDefaultKey();
			mKey.RScan.setDefaultKey();
			mKey.Cam.setDefaultKey();
			mKey.LScan.setDefaultKey();
			mKey.VolDown.setDefaultKey();
			mKey.Home.setDefaultKey();
			mKey.Back.setDefaultKey();
			mKey.Menu.setDefaultKey();
			mKey.Action.setDefaultKey();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mKey.unregisterKeyListener();
	}

	KeyRemap.KeyRemapListener listener = new KeyRemap.KeyRemapListener() {
		@Override
		public void onSetKeyResult(boolean b) {

		}

		@Override
		public void onGetKeyResult(int i) {
			mKeycodeSpin.setSelection(mKeycodeArrayList.indexOf(i));
		}
	};

}
