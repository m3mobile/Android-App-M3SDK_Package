package net.m3mobile.ugr_demo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.m3mobile.ugremul.IUGRTestService;
import net.m3mobile.ugremul.IUHFServiceCallback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by M3 on 2017-12-11.
 */

public class ResultWindow_aidl extends AppCompatActivity{
    private final String TAG = "ResultWindow_aidl";

    ListView mListInventory;
    Button mBtnStart;
    Button mBtnClear;
    Button mBtnExport;

    TextView mTagsCount, mInventoryTime;
    TextView mTvScannerResult;

    boolean mIsReading = false;

    private ArrayList<HashMap<String, UhfTag>> mTAGs;
    private TagAdapter adapter;

    private ResultWindowReceiver resultReceiver;
    private BarcodeReceiver mCodeReceiver;
    private IntentFilter mBarcodeFilter;
    RadioGroup triggerGroup;
    int mLastTriggerMode = 2;

    static public boolean bNeedConnect = false;

    private ArrayList<UhfTag> uhfTagArrayList;

    private IUGRTestService m_remoteSvc = null;
    private IUHFServiceCallback m_remoteCallback = null;
    private ServiceConnection m_UHFSvcConnection = null;
    private boolean bEnable = false;
    private boolean isServiceConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.result_window);

        mainScreen();

        resultReceiver = new ResultWindowReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UGRApplication.SCANNER_KEY_INTENT);

        mCodeReceiver = new BarcodeReceiver();
        mBarcodeFilter = new IntentFilter();
        mBarcodeFilter.addAction(UGRApplication.SCANNER_ACTION_BARCODE);

        registerReceiver(resultReceiver, filter);
        registerReceiver(mCodeReceiver, mBarcodeFilter);

        registerConnection();
        registerCallBack();

        Intent intent = new Intent("net.m3mobile.ugremul.start");
        intent.setPackage("net.m3mobile.ugremul");
        bindService(intent,m_UHFSvcConnection, Context.BIND_AUTO_CREATE);
    }

    protected void mainScreen() {
        mListInventory = (ListView) findViewById(R.id.listView_Inventory);
        mBtnStart = (Button) findViewById(R.id.btnStart);
        mBtnClear = (Button) findViewById(R.id.btnClear);
        mBtnExport = (Button) findViewById(R.id.btnExport);
        mTagsCount = (TextView) findViewById(R.id.textView_count);
        mTagsCount.setText("TAGS Count\n0");
        mInventoryTime = (TextView) findViewById(R.id.textView_time);
        mTvScannerResult = (TextView) findViewById(R.id.scanresult_intent);

        triggerGroup = (RadioGroup) findViewById(R.id.radio_trigger_mode);
        RadioButton triggerRFID = (RadioButton) findViewById(R.id.radio_trigger_rfid);
        RadioButton triggerScanner = (RadioButton) findViewById(R.id.radio_trigger_scanner);
        RadioButton triggerBoth = (RadioButton) findViewById(R.id.radio_trigger_both);
        triggerRFID.setOnClickListener(OnTriggerClickListener2);
        triggerScanner.setOnClickListener(OnTriggerClickListener2);
        triggerBoth.setOnClickListener(OnTriggerClickListener2);
        triggerBoth.setChecked(true);

        mTAGs = new ArrayList<>();
        uhfTagArrayList = new ArrayList<>();

        adapter = new TagAdapter(this, mTAGs, R.layout.listview_item_row, null, null);

        mListInventory.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListInventory.setAdapter(adapter);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mIsReading == false) {
                    inventory(true);
                } else {
                    inventory(false);
                }
            }
        });

        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTAGs.clear();
                adapter.notifyDataSetChanged();
                mTagsCount.setText("TAGS Count\n0");

                uhfTagArrayList.clear();
            }
        });

        mBtnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long time = System.currentTimeMillis();
                SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMdd_hhmmss");

                String strFolderName = Environment.getExternalStorageDirectory().getPath() +
                        "/android/data/net.m3mobile.ugremul";
                String strFileName = "/export_" + dayTime.format(new Date(time)) + ".txt";

                for(int i = 0; i < mTAGs.size(); i++) {
                    HashMap<String, UhfTag> tm = mTAGs.get(i);

                    if(tm != null) {
                        UhfTag epc = (UhfTag)tm.values().toArray()[0];

                        String strTAG = epc.TIME + "    " + epc.EPC;

                        if(!exportTxtFile(strFolderName, strFileName, strTAG)) {
                            Toast.makeText(ResultWindow_aidl.this, "Export data Failed!!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
                Toast.makeText(ResultWindow_aidl.this, "Export data Success!! on '" + strFolderName + strFileName + "'", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isServiceConnect)
            return;
        try {
            m_remoteSvc.setReadMode(0);
            m_remoteSvc.setTrigger(mLastTriggerMode);
            m_remoteSvc.setEndChar(6);
            m_remoteSvc.setOutputMode(2);
            if(!bNeedConnect)
                m_remoteSvc.setEnable(1, false);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException : " + e.getMessage());
        }

    }

    public class ResultWindowReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String epc;

            if(intent.getAction().equals(UGRApplication.UGR_ACTION_EPC)) {
                epc = intent.getExtras().getString(UGRApplication.UGR_EXTRA_EPC_DATA);
                uhfTagArrayList.add(new UhfTag(epc, 1));

                if(epc != null) {
                    boolean existTag = false;

                    HashMap<String, UhfTag> hashMap = new HashMap<>();
                    hashMap.put(epc, new UhfTag(epc, 1));

                    for(int i = 0; i < mTAGs.size(); i++) {
                        HashMap<String, UhfTag> tm = mTAGs.get(i);
                        if(tm != null) {
                            if(tm.containsKey(epc)) {
                                tm.get(epc).Reads++;
                                existTag = true;
                                break;
                            }
                        }
                    }
                    if(!existTag) {
                        mTAGs.add(hashMap);

                        int nSize = mTAGs.size();

                        mTagsCount.setText(getString(R.string.tags_count, nSize));
                    }

                    adapter.notifyDataSetChanged();
                }

            } else if(intent.getAction().equals(UGRApplication.UGR_ACTION_IS_READING)) {
                mIsReading = intent.getExtras().getBoolean(UGRApplication.UGR_EXTRA_IS_READING);
                if(mIsReading) {
                    mBtnStart.setText("Stop");
                } else {
                    mBtnStart.setText("Start");
                }
            } else if(intent.getAction().equals(UGRApplication.SCANNER_KEY_INTENT)) {
                int nExtra = intent.getIntExtra(UGRApplication.SCANNER_KEY_EXTRA, 0);
                if(nExtra == 1) {
                    myBaseTime = SystemClock.elapsedRealtime();
                    // System.out.println(myBaseTime);
                    myTimer.sendEmptyMessage(0);
                } else {
                    myTimer.removeMessages(0);
                    myPauseTime = SystemClock.elapsedRealtime();
                    uhfTagArrayList.clear();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(resultReceiver);
        unregisterReceiver(mCodeReceiver);
        resultReceiver = null;

        try {
            m_remoteSvc.setTrigger(2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        unbindService(m_UHFSvcConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsReading)
            inventory(false);

        try {
            m_remoteSvc.setReadMode(2);
            m_remoteSvc.setTrigger(2);
            m_remoteSvc.setIntentEnable(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onStop() {
        super.onStop();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            boolean isScreenOn = powerManager.isInteractive();
            if(isScreenOn && !bNeedConnect) {
                Log.d(TAG, "isScreenOn && !bNeedConnect");
                try {
                    m_remoteSvc.setEnable(0, false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean exportTxtFile(String strFolderName, String strFileName, String strData) {
        File folder = new File(strFolderName);
        if(!folder.exists()) {
            try {
                boolean bMk = folder.mkdir();
                Log.d(TAG, "exportTxtFile: mkdir: " + strFolderName + " : " + bMk);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        Log.d(TAG, "exportTxtFile: " + strFolderName + strFileName);
        File exportFile = new File(strFolderName + strFileName);
        if(!exportFile.exists()) {
            try {
                exportFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(exportFile, true));
            buf.append(strData);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    RadioButton.OnClickListener OnTriggerClickListener2 = new RadioButton.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.radio_trigger_rfid:
                    mLastTriggerMode = 0;
                    break;
                case R.id.radio_trigger_scanner:
                    mLastTriggerMode = 1;
                    break;
                case R.id.radio_trigger_both:
                    mLastTriggerMode = 2;
                    break;
            }

            try {
                m_remoteSvc.setTrigger(mLastTriggerMode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    public class BarcodeReceiver extends BroadcastReceiver {

        private String barcode;
        private String type;

        private static final String SCANNER_EXTRA_BARCODE_DATA = "m3scannerdata";
        private static final String SCANNER_EXTRA_BARCODE_CODE_TYPE = "m3scanner_code_type";

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(UGRApplication.SCANNER_ACTION_BARCODE)) {

                barcode = intent.getExtras().getString(SCANNER_EXTRA_BARCODE_DATA);
                type = intent.getExtras().getString(SCANNER_EXTRA_BARCODE_CODE_TYPE);

                if(barcode != null) {
                    mTvScannerResult.setText("Code : " + barcode + " / Type : " + type);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_rfid, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mIsReading) {
            inventory(false);
        }

        int id = item.getItemId();
        Log.i(TAG, "Selected menu - " + id);

        switch (id) {
            case R.id.action_menu_config: {
                bNeedConnect = true;
                startActivity(new Intent(this, ConfigPreferenceActivity.class));
            }
            break;
            case R.id.action_menu_access: {
                bNeedConnect = true;
                startActivity(new Intent(this, AccessActivity.class));
            }
            break;
            case R.id.action_menu_lock: {
                bNeedConnect = true;
                startActivity(new Intent(this, LockActivity.class));
            }
            /*case R.id.action_menu_oem_data: {
                bNeedConnect = true;
                startActivity(new Intent(this, OemDataActivity.class));
            }*/
            /*case R.id.action_menu_algorithm: {
                bNeedConnect = true;
                startActivity(new Intent(this, Algorithm.class));
            }*/
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    long myBaseTime = 0;
    long myPauseTime;
    private void inventory(boolean bStart) {
        try {
            if(!bEnable)
                return;
            m_remoteSvc.Inventory(bStart);
            if(bStart) {
                myBaseTime = SystemClock.elapsedRealtime();
                // System.out.println(myBaseTime);
                myTimer.sendEmptyMessage(0);
            } else {
                myTimer.removeMessages(0);
                myPauseTime = SystemClock.elapsedRealtime();
                uhfTagArrayList.clear();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "inventory RemoteException : " + e.getMessage());
        }
    }

    @SuppressLint("HandlerLeak")
    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            mInventoryTime.setText(getTimeOut());

            myTimer.sendEmptyMessage(0);
        }
    };

    String getTimeOut(){
        long now = SystemClock.elapsedRealtime();
        long outTime = now - myBaseTime;
        @SuppressLint("DefaultLocale") String easy_outTime = String.format("%02d:%02d:%02d", outTime/1000 / 60, (outTime/1000)%60,(outTime%1000)/10);

        long totalDiff = 0;
        long rps = 0;
        for (int i = 1; i < uhfTagArrayList.size(); i++) {
             uhfTagArrayList.get(i).diff = uhfTagArrayList.get(i).time - uhfTagArrayList.get(i - 1).time;
             totalDiff += uhfTagArrayList.get(i).diff;
        }
        try {
            if (uhfTagArrayList.size() > 2) {
                rps = 1000 / (totalDiff / (uhfTagArrayList.size() - 1));
            }
        } catch (Exception e) {

        }

        return easy_outTime + " " + rps + "rps";
    }

    private void registerConnection() {
        if(m_UHFSvcConnection == null) {
            m_UHFSvcConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    m_remoteSvc = IUGRTestService.Stub.asInterface(service);
                    Log.d(TAG, "Service is Connected");
                    try {
                        if(m_remoteSvc.registerUHFServiceCallback(m_remoteCallback))
                            Log.d(TAG, "Callback was registered");
                        else
                            Log.d(TAG, "Registering Callback was failed");
                        m_remoteSvc.setEnable(1, false);
                        m_remoteSvc.setReadMode(0);
                        m_remoteSvc.setTrigger(mLastTriggerMode);
                        m_remoteSvc.setEndChar(6);
                        m_remoteSvc.setOutputMode(2);
                        m_remoteSvc.setIntentEnable(false);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    isServiceConnect = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.d(TAG, "Service is Disconnected");
                    m_remoteSvc = null;
                    isServiceConnect = false;
                }
            };
        }
    }

    private void registerCallBack() {
        m_remoteCallback = new IUHFServiceCallback.Stub() {
            @Override
            public void onInventory(String epc) throws RemoteException {
                uhfTagArrayList.add(new UhfTag(epc, 1));

                if(epc != null) {
                    boolean existTag = false;

                    HashMap<String, UhfTag> hashMap = new HashMap<>();
                    hashMap.put(epc, new UhfTag(epc, 1));

                    for(int i = 0; i < mTAGs.size(); i++) {
                        HashMap<String, UhfTag> tm = mTAGs.get(i);
                        if(tm != null) {
                            if(tm.containsKey(epc)) {
                                tm.get(epc).Reads++;
                                existTag = true;
                                break;
                            }
                        }
                    }
                    if(!existTag) {
                        mTAGs.add(hashMap);

                        int nSize = mTAGs.size();

                        // mTagsCount.setText(getString(R.string.tags_count, nSize));
                        Message message = Message.obtain(handler, MSG_UHF_LIST_COUNT, nSize);
                        handler.sendMessage(message);
                    }

                    // adapter.notifyDataSetChanged();
                    handler.sendMessage(handler.obtainMessage(MSG_UHF_LIST_CHANGED));
                }
            }

            @Override
            public void onIsReading(boolean isReading) throws RemoteException {
                mIsReading = isReading;
                if(mIsReading) {
                    // mBtnStart.setText("Stop");
                    handler.sendMessage(handler.obtainMessage(MSG_UHF_BUTTON_STOP));
                } else {
                    // mBtnStart.setText("Start");
                    handler.sendMessage(handler.obtainMessage(MSG_UHF_BUTTON_START));
                }
            }

            @Override
            public void onEnable(boolean isEnable) throws RemoteException {
                bEnable = isEnable;
                if(!isEnable)
                    inventory(false);
            }

            @Override
            public void onInventoryRssi(String epc, double nb_rssi, double wb_rssi) throws RemoteException {
                Log.d(TAG, "" + nb_rssi);
            }
        };
    }

    private final int MSG_UHF_LIST_CHANGED = 10;
    private final int MSG_UHF_LIST_COUNT = 20;
    private final int MSG_UHF_BUTTON_STOP = 100;
    private final int MSG_UHF_BUTTON_START = 200;

    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UHF_LIST_CHANGED:
                    adapter.notifyDataSetChanged();
                    break;
                case MSG_UHF_LIST_COUNT:
                    mTagsCount.setText(getString(R.string.tags_count, msg.arg1));
                    break;
                case MSG_UHF_BUTTON_STOP:
                    mBtnStart.setText("Stop");
                    break;
                case MSG_UHF_BUTTON_START:
                    mBtnStart.setText("Start");
                    break;
            }
        }
    };
}
