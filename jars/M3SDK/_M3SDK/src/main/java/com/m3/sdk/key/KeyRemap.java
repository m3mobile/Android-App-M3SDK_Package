/*
 * v.1.0.0	Jayden	SM10 Key Remap
 * v.1.1.0	Jayden	SM10LTE Integration
 * v.1.2.0	Jayden 20170222	Key remap SDK 를 Intent 방식 추가
 */
package com.m3.sdk.key;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
// import android.os.SystemProperties;
import android.util.Log;

public class KeyRemap {

	private final String strVersion = "1.2.0";
	
	private static final boolean localLOGV = true;
	private static final boolean localTOAST = false;
	private static final String TAG = KeyRemap.class.getSimpleName();

	private final static int KEY_DISABLE = 0;
	private final static int KEY_SCAN = 249;
	private final static int KEY_SCAN_LTE = 261;
	private final static int KEY_CAM = 212;
	private final static int KEY_MENU = 139;
	private final static int KEY_HOME = 102;
	private final static int KEY_BACK = 158;
	private final static int KEY_VOLUME_DOWN = 114;
	private final static int KEY_VOLUME_UP = 115;
	private final static int KEY_SEARCH = 528;
	private final static int KEY_FUNCTION = 464;
	private final static int KEY_F1 = 466;
	private final static int KEY_F2 = 467;
	private final static int KEY_F3 = 468;
	private final static int KEY_F4 = 469;
	private final static int KEY_F5 = 470;
	private final static int KEY_F6 = 471;
	private final static int KEY_F7 = 472;
	private final static int KEY_F8 = 473;

    public static final String KEYTOOL_ACTION_SET = "net.m3mobile.keytool.set";
    public static final String KEYTOOL_ACTION_SET_RESULT = "net.m3mobile.keytool.set.result";
    public static final String KEYTOOL_ACTION_GET = "net.m3mobile.keytool.get";
    public static final String KEYTOOL_ACTION_GET_RESULT = "net.m3mobile.keytool.get.result";

    public static final String KEYTOOL_EXTRA_KEY_INDEX = "key.index";
    public static final String KEYTOOL_EXTRA_KEY_CODE = "key.code";
    public static final String KEYTOOL_EXTRA_SET_RESULT = "key.set.result";
    
	public static int getDisableKeyCode(){return KEY_DISABLE;}
	public static int getScanKeyCode(){
		if(isSM10LTE()){
			return KEY_SCAN_LTE;
		}else{
			return KEY_SCAN;
		}
	}
	public static int getCamKeyCode(){return KEY_CAM;}
	public static int getMenuKeyCode(){return KEY_MENU;}
	public static int getHomeKeyCode(){return KEY_HOME;}
	public static int getBackKeyCode(){return KEY_BACK;}
	public static int getVolDownKeyCode(){return KEY_VOLUME_DOWN;}
	public static int getVolUpKeyCode(){return KEY_VOLUME_UP;}
	public static int getSearchKeyCode(){return KEY_SEARCH;}
	public static int getFunctionKeyCode(){return KEY_FUNCTION;}
	public static int getF1KeyCode(){return KEY_F1;}
	public static int getF2KeyCode(){return KEY_F2;}
	public static int getF3KeyCode(){return KEY_F3;}
	public static int getF4KeyCode(){return KEY_F4;}
	public static int getF5KeyCode(){return KEY_F5;}
	public static int getF6KeyCode(){return KEY_F6;}
	public static int getF7KeyCode(){return KEY_F7;}
	public static int getF8KeyCode(){return KEY_F8;}
	
	
	public KeyLScan LScan;
	public KeyRScan RScan;
	public KeyAction Action;
	public KeyCam Cam;
	public KeyVolUp VolUp;
	public KeyVolDown VolDown;
	public KeyBack Back;
	public KeyHome Home;
	public KeyMenu Menu;
	
	private Context _MainContext = null;
	
	private static int mLastGotKeyCode = -1;
	private static int mLastSetKeyResult = -1;

	public static String PackageNameFile = "/storage/sdcard0/.KeytoolPackageName";
	
	private KeyRemapListener _keyListener = null;
	
	public static boolean isSM10LTE(){
		
		if(Build.MODEL.equals("M3SM10")){
			return false;
		}else{
			return true;
		}
		
	}
	
	public String getVersion(){
		return strVersion;
	}

//	public KeyRemap(){
//		LScan = new KeyLScan();
//		RScan = new KeyRScan();
//		Action = new KeyAction();
//		Cam = new KeyCam();
//		VolUp = new KeyVolUp();
//		VolDown = new KeyVolDown();
//		Back = new KeyBack();
//		Home = new KeyHome();
//		Menu = new KeyMenu();
//	}

	public KeyRemap(Context context){
		LScan = new KeyLScan();
		RScan = new KeyRScan();
		Action = new KeyAction();
		Cam = new KeyCam();
		VolUp = new KeyVolUp();
		VolDown = new KeyVolDown();
		Back = new KeyBack();
		Home = new KeyHome();
		Menu = new KeyMenu();
		
		_MainContext = context;
		
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(KEYTOOL_ACTION_GET_RESULT);
        iFilter.addAction(KEYTOOL_ACTION_SET_RESULT);
        _MainContext.registerReceiver(mKeytoolResultReciever, iFilter);
	}
	

	public void registerKeyListener(KeyRemapListener listener){
		
		_keyListener = listener;
	
	}
	
	public void unregisterKeyListener(){
		_keyListener = null;

		_MainContext.unregisterReceiver(mKeytoolResultReciever);
		_MainContext = null;
	}
	
	private class keySet{
		
		protected void setKeyIntent(int lockedKey, int code){
			showToast("current = " + lockedKey + " | " + "Keycode = " + code);

			Intent setkeyIntent = new Intent(KEYTOOL_ACTION_SET); 
			setkeyIntent.putExtra(KEYTOOL_EXTRA_KEY_INDEX, lockedKey);
			setkeyIntent.putExtra(KEYTOOL_EXTRA_KEY_CODE, code);
			
			_MainContext.sendBroadcast(setkeyIntent);	
		}
		
		protected void getKeyIntent(int lockedKey){
			_MainContext.sendBroadcast(new Intent(KEYTOOL_ACTION_GET).putExtra(KEYTOOL_EXTRA_KEY_INDEX, lockedKey));	
		}
		
		protected boolean setKey(int curKeycode, int setKeycode){
			
			if(_MainContext != null && isSM10LTE()){
				setKeyIntent(curKeycode, setKeycode);
				
				return true;
				
//				mLastSetKeyResult = -1;				
//				boolean bResult = false;
//				int nCnt = 0;
//				while(mLastSetKeyResult == -1 && nCnt < 50){
//					try {			
//						Thread.sleep(100);
//						nCnt++;			
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}					
//				}
//				showToast("setKeyIntent result: " + mLastSetKeyResult);
//				if(mLastSetKeyResult == -1){
//					return false;
//				}else{
//
//					if(mLastSetKeyResult == 1)
//						bResult = true;
//					else
//						bResult = false;
//					
//					
//					return bResult;
//				}
			}
			
			boolean bRet = false;
			
			File baseDir;
			BufferedWriter out;

			String index = Integer.toString(curKeycode);
			String keycode = Integer.toString(setKeycode);

			showToast("current = " + curKeycode + " | " + "Keycode = " + setKeycode);

			/*
			 * check a permission of below files echo index of button >
			 * /sys/devices/gpio_keys.57/locked_key echo keycode >
			 * /sys/devices/gpio_keys.57/change_key
			 */
			try {
				if(isSM10LTE()){
					baseDir = new File("/sys/devices/soc.0/gpio_keys.66");
					
				}else{
					baseDir = new File("/sys/devices/gpio_keys.57");

				}
				if(Build.MODEL.contains("M3SM15")) {
					baseDir = new File("/sys/devices/soc/soc:gpio_keys");
				}
				/* lock key */
				out = new BufferedWriter(new FileWriter(baseDir + File.separator
						+ "locked_key"));
				out.write(index);
				out.close();

				/* assign key */
				out = new BufferedWriter(new FileWriter(baseDir + File.separator
						+ "change_key"));
				out.write(keycode);
				out.close();
				
				setKeymap();
				
				bRet = true;

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				showToast("FileNotFoundException");
				bRet = false;
			} catch (IOException e) {
				e.printStackTrace();
				showToast("IOException");
				bRet = false;
			}
			
			return bRet;
		}

		private void setKeymap() {
			/* make /data/system/keymapper.conf */
			//SystemProperties.set("ctl.start", "m3_oem_keymap");
		}
		
		protected int getKey(long key_idx){
			
			//if(_MainContext != null){
			if(isSM10LTE()){
				int nReturnCode = -1;
				getKeyIntent((int)key_idx);
				return nReturnCode;
//				
//				int nCnt = 0;
//
//				mLastGotKeyCode = -1;
//				showToast("getKeyIntent wait " + nCnt + " mLastGotKeyCode: " + mLastGotKeyCode);	
//				while(mLastGotKeyCode == -1 && nCnt < 50){
//					try {		
//						showToast("getKeyIntent wait " + nCnt + " mLastGotKeyCode: " + mLastGotKeyCode);	
//						Thread.sleep(100);
//						nCnt++;			
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}					
//				}
//				
//				showToast("getKeyIntent result: " + mLastGotKeyCode);
//				if(mLastGotKeyCode == -1){
//					return -1;
//				}else{
//					nReturnCode = mLastGotKeyCode;
//					
//					return nReturnCode;
//				}
				
			}
			
			File mapfile;
			FileReader input = null;
			BufferedReader reader = null;
			String line = null;
			int ret = 0;


			 // root@msm8610:/ # cat /sys/devices/gpio_keys.57/map 2 212 0 0

			try {
				if(isSM10LTE()){
					mapfile = new File("/sys/devices/soc.0/gpio_keys.66/map");

				}else{

					mapfile = new File("/sys/devices/gpio_keys.57/map");
				}
				if(Build.MODEL.contains("M3SM15")) {
					mapfile = new File("/sys/devices/soc/soc:gpio_keys/map");
				}

				input = new FileReader(mapfile);
				reader = new BufferedReader(input);

				for (int i = 0; i < 10; i++) {
					showToast(i + " ");
					line = reader.readLine();

					if (String.valueOf(key_idx).compareTo(line.substring(0, 1)) == 0) {
						ret = Integer.valueOf(line.substring(2, line.length()));
						break;
					}
				}

				input.close();
				reader.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return ret;
		}
	}

	public class KeyLScan extends keySet{
		private final static int CODE = 3;
		private final int DEFAULT;

		public KeyLScan(){
			DEFAULT = getScanKeyCode();
		}
		
		public int getDefaultKey(){
			return DEFAULT;
		}
		
		public boolean setDefaultKey(){			
			
			return super.setKey(CODE, DEFAULT);
		}

		public boolean setKey(int setKeyCode){
			return super.setKey(CODE, setKeyCode);
		}
		
		public int getKey(){
			return super.getKey(CODE);
		}
	}
	public class KeyRScan extends keySet{
		private final static int CODE = 1;
		//private final static int DEFAULT = KEY_SCAN;
		private final int DEFAULT = KeyRemap.getScanKeyCode();

		public int getDefaultKey(){
			return DEFAULT;
		}
		
		public boolean setDefaultKey(){
			return super.setKey(CODE, DEFAULT);
		}

		public boolean setKey(int setKeyCode){
			return super.setKey(CODE, setKeyCode);
		}
		
		public int getKey(){
			return super.getKey(CODE);
		}
	}
	public class KeyCam extends keySet{
		private final static int CODE = 2;
		private final static int DEFAULT = KEY_CAM;

		public int getDefaultKey(){
			return DEFAULT;
		}
		
		public boolean setDefaultKey(){
			return super.setKey(CODE, DEFAULT);
		}

		public boolean setKey(int setKeyCode){
			return super.setKey(CODE, setKeyCode);
		}
		
		public int getKey(){
			return super.getKey(CODE);
		}
	}
	public class KeyVolUp extends keySet{
		private final static int CODE = 0;
		private final static int DEFAULT = KEY_VOLUME_UP;

		public int getDefaultKey(){
			return DEFAULT;
		}
		
		public boolean setDefaultKey(){
			return super.setKey(CODE, DEFAULT);
		}

		public boolean setKey(int setKeyCode){
			return super.setKey(CODE, setKeyCode);
		}
		
		public int getKey(){
			return super.getKey(CODE);
		}
	}
	public class KeyVolDown extends keySet{
		private final static int CODE = 4;
		private final static int DEFAULT = KEY_VOLUME_DOWN;

		public int getDefaultKey(){
			return DEFAULT;
		}
		
		public boolean setDefaultKey(){
			return super.setKey(CODE, DEFAULT);
		}

		public boolean setKey(int setKeyCode){
			return super.setKey(CODE, setKeyCode);
		}
		
		public int getKey(){
			return super.getKey(CODE);
		}
	}
	public class KeyBack extends keySet{
		private final static int CODE = 6;
		private final static int DEFAULT = KEY_BACK;

		public int getDefaultKey(){
			return DEFAULT;
		}
		
		public boolean setDefaultKey(){
			return super.setKey(CODE, DEFAULT);
		}

		public boolean setKey(int setKeyCode){
			return super.setKey(CODE, setKeyCode);
		}
		
		public int getKey(){
			return super.getKey(CODE);
		}
	}
	public class KeyHome extends keySet{
		private final static int CODE = 5;
		private final static int DEFAULT = KEY_HOME;

		public int getDefaultKey(){
			return DEFAULT;
		}
		
		public boolean setDefaultKey(){
			return super.setKey(CODE, DEFAULT);
		}

		public boolean setKey(int setKeyCode){
			return super.setKey(CODE, setKeyCode);
		}
		
		public int getKey(){
			return super.getKey(CODE);
		}
	}
	public class KeyMenu extends keySet{
		private final static int CODE = 7;
		private final static int DEFAULT = KEY_MENU;

		public int getDefaultKey(){
			return DEFAULT;
		}
		
		public boolean setDefaultKey(){
			return super.setKey(CODE, DEFAULT);
		}

		public boolean setKey(int setKeyCode){
			return super.setKey(CODE, setKeyCode);
		}
		
		public int getKey(){
			return super.getKey(CODE);
		}
	}
	public class KeyAction extends keySet{
		private final static int CODE = 8;
		//private final static int DEFAULT = KEY_SCAN;
		private final int DEFAULT = KeyRemap.getScanKeyCode();

		public int getDefaultKey(){
			return DEFAULT;
		}
		
		public boolean setDefaultKey(){
			return super.setKey(CODE, DEFAULT);
		}
		
		public boolean setKey(int setKeyCode){
			return super.setKey(CODE, setKeyCode);
		}
		
		public int getKey(){
			return super.getKey(CODE);
		}
		
	}
	
	private BroadcastReceiver mKeytoolResultReciever = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			
			if(action.equals(KEYTOOL_ACTION_GET_RESULT)){			
				
				int nResultCode = intent.getIntExtra(KEYTOOL_EXTRA_KEY_CODE, -1);
				
				//mLastGotKeyCode = nResultCode;	
				
				if(_keyListener != null){
					_keyListener.onGetKeyResult(nResultCode);
				}
				
				// showToast("KeytoolResultReciever: got key code " + mLastGotKeyCode);
				
			}else if(action.equals(KEYTOOL_ACTION_SET_RESULT)){
				boolean bResult = intent.getBooleanExtra(KEYTOOL_EXTRA_SET_RESULT, false);
				
				if(_keyListener != null){
					_keyListener.onSetKeyResult(bResult);
				}
				
//				if(bResult){
//					mLastSetKeyResult = 1;
//				}else{
//					mLastSetKeyResult = 0;
//				}

				// showToast("KeytoolResultReciever: setResult " + mLastSetKeyResult);
				
			}
			
		}
		
	};

	public interface KeyRemapListener {
		public void onSetKeyResult(boolean bResult);
		public void onGetKeyResult(int nCode);
	}

	
	private void showToast(CharSequence msg) {
		if (localLOGV)
			Log.v(TAG, msg.toString());
//		if (localTOAST)
//			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
