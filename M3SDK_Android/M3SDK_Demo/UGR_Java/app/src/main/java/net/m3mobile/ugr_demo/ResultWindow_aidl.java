package net.m3mobile.ugr_demo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
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
import java.util.Objects;

/**
 * Created by M3 on 2017-12-11.
 */

public class ResultWindow_aidl extends AppCompatActivity {
    private Button mBtnStart;

    private TextView mTagsCount, mInventoryTime;
    private TextView mTvScannerResult;

    private boolean mIsReading = false;

    private ArrayList<HashMap<String, UHFTag>> mTAGs;
    private TagAdapter adapter;

    private ResultWindowReceiver resultReceiver;
    private BarcodeReceiver mCodeReceiver;
    private int mLastTriggerMode = 2;

    public static boolean bNeedConnect = false;

    private ArrayList<UHFTag> UHFTagArrayList;

    private IUGRTestService m_remoteSvc = null;
    private IUHFServiceCallback m_remoteCallback = null;
    private ServiceConnection m_UHFSvcConnection = null;
    private boolean bEnable = false;
    private boolean isServiceConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogWriter.i("onCreate++");
        setContentView(R.layout.result_window);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainScreen();

        resultReceiver = new ResultWindowReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UGRApplication.SCANNER_KEY_INTENT);

        mCodeReceiver = new BarcodeReceiver();
        IntentFilter mBarcodeFilter = new IntentFilter();
        mBarcodeFilter.addAction(UGRApplication.SCANNER_ACTION_BARCODE);

        registerReceiver(resultReceiver, filter);
        registerReceiver(mCodeReceiver, mBarcodeFilter);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        registerConnection();
        registerCallBack();

        Intent intent = new Intent("net.m3mobile.ugremul.start");
        intent.setPackage("net.m3mobile.ugremul");
        bindService(intent,m_UHFSvcConnection, Context.BIND_AUTO_CREATE);
    }

    protected void mainScreen() {
        ListView mListInventory = findViewById(R.id.listView_Inventory);
        mBtnStart = findViewById(R.id.btnStart);
        Button mBtnClear = findViewById(R.id.btnClear);
        Button mBtnExport = findViewById(R.id.btnExport);
        mTagsCount = findViewById(R.id.textView_count);
        mTagsCount.setText("TAGS Count\n0");
        mInventoryTime = findViewById(R.id.textView_time);
        mTvScannerResult = findViewById(R.id.scanresult_intent);

        RadioGroup triggerGroup = findViewById(R.id.radio_trigger_mode);
        RadioButton triggerRFID = findViewById(R.id.radio_trigger_rfid);
        RadioButton triggerScanner = findViewById(R.id.radio_trigger_scanner);
        RadioButton triggerBoth = findViewById(R.id.radio_trigger_both);
        triggerRFID.setOnClickListener(OnTriggerClickListener2);
        triggerScanner.setOnClickListener(OnTriggerClickListener2);
        triggerBoth.setOnClickListener(OnTriggerClickListener2);
        triggerBoth.setChecked(true);

        mTAGs = new ArrayList<>();
        UHFTagArrayList = new ArrayList<>();

        adapter = new TagAdapter(this, mTAGs, R.layout.listview_item_row, null, null);

        mListInventory.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListInventory.setAdapter(adapter);

        mBtnStart.setOnClickListener(view -> {
            LogWriter.i("onClick++ " + mIsReading);

            inventory(!mIsReading);
        });

        mBtnClear.setOnClickListener(view -> {
            mTAGs.clear();
            adapter.notifyDataSetChanged();
            mTagsCount.setText("TAGS Count\n0");

            UHFTagArrayList.clear();
        });

        mBtnExport.setOnClickListener(view -> {
            long time = System.currentTimeMillis();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMdd_hhmmss");

            String strFolderName = Environment.getExternalStorageDirectory().getPath() +
                    "/android/data/net.m3mobile.ugremul";
            String strFileName = "/export_" + dayTime.format(new Date(time)) + ".txt";

            for (int i = 0; i < mTAGs.size(); i++) {
                HashMap<String, UHFTag> tm = mTAGs.get(i);

                if (tm != null) {
                    UHFTag epc = (UHFTag)tm.values().toArray()[0];

                    String strTAG = epc.TIME + "    " + epc.EPC;

                    if (!exportTxtFile(strFolderName, strFileName, strTAG)) {
                        Toast.makeText(ResultWindow_aidl.this, "Export data Failed!!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            Toast.makeText(ResultWindow_aidl.this, "Export data Success!! on '" + strFolderName + strFileName + "'", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogWriter.i("onResume isServiceConnect:" + isServiceConnect);

        if (!isServiceConnect)
            return;
        try {
            m_remoteSvc.setReadMode(0);
            m_remoteSvc.setTrigger(mLastTriggerMode);
            m_remoteSvc.setEndChar(6);
            m_remoteSvc.setOutputMode(2);
            m_remoteSvc.updateAllOption();
            if (!bNeedConnect)
                m_remoteSvc.setEnable(1, false);
        } catch (RemoteException e) {
            LogWriter.e("RemoteException : " + e.getMessage());
        }

    }

    public class ResultWindowReceiver extends BroadcastReceiver {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String epc;

            switch (intent.getAction()) {
                case UGRApplication.UGR_ACTION_EPC:
                    epc = intent.getExtras().getString(UGRApplication.UGR_EXTRA_EPC_DATA);
                    UHFTagArrayList.add(new UHFTag(epc, 1));

                    if (epc != null) {
                        boolean existTag = false;

                        HashMap<String, UHFTag> hashMap = new HashMap<>();
                        hashMap.put(epc, new UHFTag(epc, 1));

                        for (int i = 0; i < mTAGs.size(); i++) {
                            HashMap<String, UHFTag> tm = mTAGs.get(i);
                            if (tm != null) {
                                if (tm.containsKey(epc)) {
                                    Objects.requireNonNull(tm.get(epc)).Reads++;
                                    existTag = true;
                                    break;
                                }
                            }
                        }
                        if (!existTag) {
                            mTAGs.add(hashMap);

                            int nSize = mTAGs.size();

                            mTagsCount.setText(getString(R.string.tags_count, nSize));
                        }

                        adapter.notifyDataSetChanged();
                    }

                    break;
                case UGRApplication.UGR_ACTION_IS_READING:

                    mIsReading = intent.getExtras().getBoolean(UGRApplication.UGR_EXTRA_IS_READING);
                    LogWriter.i("onReceive action:" + intent.getAction() + " mIsReading:" + mIsReading);
                    if (mIsReading)
                        mBtnStart.setText("Stop");
                    else
                        mBtnStart.setText("Start");
                    break;
                case UGRApplication.SCANNER_KEY_INTENT:
                    int nExtra = intent.getIntExtra(UGRApplication.SCANNER_KEY_EXTRA, 0);
                    if (nExtra == 1) {
                        myBaseTime = SystemClock.elapsedRealtime();
                        // System.out.println(myBaseTime);
                        myTimer.sendEmptyMessage(0);
                    } else {
                        myTimer.removeMessages(0);
                        myPauseTime = SystemClock.elapsedRealtime();
                        UHFTagArrayList.clear();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogWriter.i("onDestroy++");

        unregisterReceiver(resultReceiver);
        unregisterReceiver(mCodeReceiver);
        resultReceiver = null;

        try {
            m_remoteSvc.setTrigger(2);
        } catch (RemoteException e) {
            LogWriter.e("onDestroy remoteException : " + e.getMessage());
        }

        unbindService(m_UHFSvcConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogWriter.i("onPause++ mIsReading:" + mIsReading);
        if (mIsReading)
            inventory(false);

        try {
            m_remoteSvc.setReadMode(2);
            m_remoteSvc.setTrigger(2);
            m_remoteSvc.setIntentEnable(true);
            m_remoteSvc.updateAllOption();
        } catch (RemoteException e) {
            LogWriter.e("onPause remoteException : " + e.getMessage());
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onStop() {
        super.onStop();
        LogWriter.i("onStop++ ");
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            boolean isScreenOn = powerManager.isInteractive();
            if (isScreenOn && !bNeedConnect) {
                LogWriter.d("isScreenOn && !bNeedConnect");
                try {
                    m_remoteSvc.setEnable(0, false);
                } catch (RemoteException e) {
                    LogWriter.e("onStop remoteException : " + e.getMessage());
                }
            }
        }
    }

    private boolean exportTxtFile(String strFolderName, String strFileName, String strData) {
        File folder = new File(strFolderName);
        if (!folder.exists()) {
            try {
                boolean bMk = folder.mkdir();
                LogWriter.d("exportTxtFile: mkdir: " + strFolderName + " : " + bMk);
            } catch (Exception e) {
                LogWriter.e("exportTxtFile Exception : " + e.getMessage());
                return false;
            }
        }

        LogWriter.d("exportTxtFile: " + strFolderName + strFileName);
        File exportFile = new File(strFolderName + strFileName);
        if (!exportFile.exists()) {
            try {
                exportFile.createNewFile();
            } catch (IOException e) {
                LogWriter.e("exportTxtFile Exception : " + e.getMessage());
                return false;
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(exportFile, true));
            buf.append(strData);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            LogWriter.e("exportTxtFile Exception : " + e.getMessage());
            return false;
        }

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    private final RadioButton.OnClickListener OnTriggerClickListener2 = view -> {
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
            m_remoteSvc.updateAllOption();
        } catch (RemoteException e) {
            LogWriter.e("onClick remoteException : " + e.getMessage());
        }
    };

    private class BarcodeReceiver extends BroadcastReceiver {
        private static final String SCANNER_EXTRA_BARCODE_DATA = "m3scannerdata";
        private static final String SCANNER_EXTRA_BARCODE_CODE_TYPE = "m3scanner_code_type";

        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(UGRApplication.SCANNER_ACTION_BARCODE)) {

                String barcode = intent.getExtras().getString(SCANNER_EXTRA_BARCODE_DATA);
                String type = intent.getExtras().getString(SCANNER_EXTRA_BARCODE_CODE_TYPE);

                if (barcode != null)
                    mTvScannerResult.setText("Code : " + barcode + " / Type : " + type);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_rfid, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mIsReading)
            inventory(false);

        int id = item.getItemId();
        LogWriter.i("Selected menu - " + id);

        switch (id) {
            case R.id.action_menu_config: {
                if (!bEnable)
                    break;
                bNeedConnect = true;
                startActivity(new Intent(this, ConfigPreferenceActivity.class));
            }
            break;
            case R.id.action_menu_access: {
                if (!bEnable)
                    break;
                bNeedConnect = true;
                startActivity(new Intent(this, AccessActivity.class));
            }
            break;
            case R.id.action_menu_lock: {
                if (!bEnable)
                    break;
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

    private long myBaseTime = 0;
    private long myPauseTime;
    private void inventory(boolean bStart) {
        try {
            LogWriter.i("inventory++ bStart:" + bStart + " bEnable: " + bEnable + " mIsReading: " + mIsReading);
            if (!bEnable)
                return;
            m_remoteSvc.Inventory(bStart);
            if (bStart) {
                myBaseTime = SystemClock.elapsedRealtime();
                // System.out.println(myBaseTime);
                myTimer.sendEmptyMessage(0);
            } else {
                myTimer.removeMessages(0);
                myPauseTime = SystemClock.elapsedRealtime();
                UHFTagArrayList.clear();
            }
        } catch (RemoteException e) {
            LogWriter.e("inventory RemoteException : " + e.getMessage());
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler myTimer = new Handler(){
        public void handleMessage(Message msg) {
            runOnUiThread(() -> mInventoryTime.setText(getTimeOut()));

            myTimer.sendEmptyMessage(0);
        }
    };

    private String getTimeOut() {
        long now = SystemClock.elapsedRealtime();
        long outTime = now - myBaseTime;
        @SuppressLint("DefaultLocale") String easy_outTime = String.format("%02d:%02d:%02d", outTime / 1000 / 60, (outTime / 1000) % 60,(outTime % 1000) / 10);

        long totalDiff = 0;
        long rps = 0;
        for (int i = 1; i < UHFTagArrayList.size(); i++) {
             UHFTagArrayList.get(i).diff = UHFTagArrayList.get(i).time - UHFTagArrayList.get(i - 1).time;
             totalDiff += UHFTagArrayList.get(i).diff;
        }
        try {
            if (UHFTagArrayList.size() > 2)
                rps = 1000 / (totalDiff / (UHFTagArrayList.size() - 1));
        } catch (Exception ignored) {

        }

        return easy_outTime + " " + rps + "rps";
    }

    private ProgressDialog progressDialog;
    private void registerConnection() {
        if (m_UHFSvcConnection == null) {
            m_UHFSvcConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    m_remoteSvc = IUGRTestService.Stub.asInterface(service);
                    LogWriter.d("Service is Connected");
                    try {
                        if (m_remoteSvc.registerUHFServiceCallback(m_remoteCallback))
                            LogWriter.d("Callback was registered");
                        else
                            LogWriter.d("Registering Callback was failed");

                        progressDialog.setMessage("Service is connected. start enable...");
                        onServiceConnectedHandler.removeCallbacks(onServiceConnectedRunnable);
                        onServiceConnectedHandler.postDelayed(onServiceConnectedRunnable, 1000 * 5);
                    } catch (RemoteException e) {
                        LogWriter.e("onServiceConnected remoteException : " + e.getMessage());
                    }
                    isServiceConnect = true;
                }

                private final Handler onServiceConnectedHandler = new Handler(getMainLooper());
                private final Runnable onServiceConnectedRunnable = () -> {
                    try {
                        m_remoteSvc.setEnable(1, false);
                        m_remoteSvc.setReadMode(0);
                        m_remoteSvc.setTrigger(mLastTriggerMode);
                        m_remoteSvc.setEndChar(6);
                        m_remoteSvc.setOutputMode(2);
                        m_remoteSvc.setIntentEnable(false);
                        m_remoteSvc.updateAllOption();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                };

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    LogWriter.d("Service is Disconnected");
                    m_remoteSvc = null;
                    isServiceConnect = false;
                }
            };
        }
    }

    private void registerCallBack() {
        m_remoteCallback = new IUHFServiceCallback.Stub() {
            @Override
            public void onInventory(final String epc) {
                runOnUiThread(() -> {
                    UHFTagArrayList.add(new UHFTag(epc, 1));

                    if (epc != null) {
                        boolean existTag = false;

                        HashMap<String, UHFTag> hashMap = new HashMap<>();
                        hashMap.put(epc, new UHFTag(epc, 1));

                        for (int i = 0; i < mTAGs.size(); i++) {
                            HashMap<String, UHFTag> tm = mTAGs.get(i);
                            if (tm != null) {
                                if (tm.containsKey(epc)) {
                                    Objects.requireNonNull(tm.get(epc)).Reads++;
                                    existTag = true;
                                    break;
                                }
                            }
                        }
                        if (!existTag) {
                            mTAGs.add(hashMap);
                            int nSize = mTAGs.size();
                            mTagsCount.setText(getString(R.string.tags_count, nSize));
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onIsReading(boolean isReading) {
                // Log.i(TAG, "onIsReading isReading:" + isReading);
                mIsReading = isReading;
                onIsReadingHandler.removeCallbacks(onIsReadingRunnable);
                onIsReadingHandler.post(onIsReadingRunnable);
            }

            private final Handler onIsReadingHandler = new Handler(Looper.getMainLooper());
            private final Runnable onIsReadingRunnable = new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    if (mIsReading)
                        mBtnStart.setText("Stop");
                    else {
                        mBtnStart.setText("Start");
                        // 전재영, Screen on 후 RFID 전원 켜진 동시에 Start를 누르면, Isreading 과 겹쳐서 시간만 가고 Start로 표기되는 상황이 발생한다. 그래서 추가함
                        myTimer.removeMessages(0);
                        myPauseTime = SystemClock.elapsedRealtime();
                        UHFTagArrayList.clear();
                    }
                }
            };

            @Override
            public void onEnable(boolean isEnable) {
                bEnable = isEnable;
                if (!isEnable)
                    inventory(false);
                else
                    progressDialog.dismiss();
            }

            @Override
            public void onInventoryRssi(String epc, double nb_rssi, double wb_rssi) {

            }
        };
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
            return true;
        return super.dispatchKeyEvent(event);
    }
}
