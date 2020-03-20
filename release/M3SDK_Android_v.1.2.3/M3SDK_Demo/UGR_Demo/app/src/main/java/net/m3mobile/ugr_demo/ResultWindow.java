/*
 *  v.0.0.2 2018-06-08  한재윤     code.2  SearchActivity, ConfigPreferenceActivity - channel setting 추가
 *  v.0.0.3 2018-06-25  한재윤     code.3  CarrierWave 추가
 *  v.1.0.0 2019-01-09  한재윤     code.4  channel, carrier wave 원복. lock 기능 개선
 *  v.1.0.1 2019-07-29  한재윤     code.5  ResultWindow_aidl activity 추가
 */

package net.m3mobile.ugr_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
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

public class ResultWindow extends AppCompatActivity{

    ListView mListInventory;
    Button mBtnStart;
    Button mBtnClear;
    Button mBtnExport;

    TextView mTagsCount;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.result_window);

        mainScreen();

        resultReceiver = new ResultWindowReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UGRApplication.UGR_ACTION_EPC);
        filter.addAction(UGRApplication.UGR_ACTION_IS_READING);

        mCodeReceiver = new BarcodeReceiver();
        mBarcodeFilter = new IntentFilter();
        mBarcodeFilter.addAction(UGRApplication.SCANNER_ACTION_BARCODE);

        registerReceiver(resultReceiver, filter);
        registerReceiver(mCodeReceiver, mBarcodeFilter);

        RFIDEnable(true);
    }

    protected void mainScreen() {
        mListInventory = (ListView) findViewById(R.id.listView_Inventory);
        mBtnStart = (Button) findViewById(R.id.btnStart);
        mBtnClear = (Button) findViewById(R.id.btnClear);
        mBtnExport = (Button) findViewById(R.id.btnExport);
        mTagsCount = (TextView) findViewById(R.id.textView_count);
        mTagsCount.setText("TAGS Count\n0");
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
                            Toast.makeText(ResultWindow.this, "Export data Failed!!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
                Toast.makeText(ResultWindow.this, "Export data Success!! on '" + strFolderName + strFileName + "'", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "read_mode");
        intent.putExtra("read_mode_value", 0);
        sendOrderedBroadcast(intent, null);

        intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "trigger_mode");
        intent.putExtra("trigger_mode_value", mLastTriggerMode);
        sendOrderedBroadcast(intent, null);
    }

    public class ResultWindowReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String epc;

            //Log.i(getClass().getSimpleName(), "onReceive [" + intent.getAction() + "]");

            if(intent.getAction().equals(UGRApplication.UGR_ACTION_EPC)) {
                epc = intent.getExtras().getString(UGRApplication.UGR_EXTRA_EPC_DATA);

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
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(resultReceiver);
        unregisterReceiver(mCodeReceiver);
        resultReceiver = null;
        RFIDEnable(false);

        Intent intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "trigger_mode");
        intent.putExtra("trigger_mode_value", 2);
        sendOrderedBroadcast(intent, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Intent intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "read_mode");
        intent.putExtra("read_mode_value", 2);
        sendOrderedBroadcast(intent, null);

        intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "trigger_mode");
        intent.putExtra("trigger_mode_value", 2);
        sendOrderedBroadcast(intent, null);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            boolean isScreenOn = powerManager.isInteractive();
            if(isScreenOn && !bNeedConnect)
                RFIDEnable(false);
        }
    }

    public boolean exportTxtFile(String strFolderName, String strFileName, String strData) {
        File folder = new File(strFolderName);
        if(!folder.exists()) {
            try {
                boolean bMk = folder.mkdir();
                Log.d(ResultWindow.class.getSimpleName(), "exportTxtFile: mkdir: " + strFolderName + " : " + bMk);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        Log.d(ResultWindow.class.getSimpleName(), "exportTxtFile: " + strFolderName + strFileName);
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
            Intent triggerIntent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
            triggerIntent.putExtra("setting", "trigger_mode");

            switch (view.getId()) {
                case R.id.radio_trigger_rfid:
                    triggerIntent.putExtra("trigger_mode_value", 0);
                    mLastTriggerMode = 0;
                    break;
                case R.id.radio_trigger_scanner:
                    triggerIntent.putExtra("trigger_mode_value", 1);
                    mLastTriggerMode = 1;
                    break;
                case R.id.radio_trigger_both:
                    triggerIntent.putExtra("trigger_mode_value", 2);
                    mLastTriggerMode = 2;
                    break;
            }

            sendBroadcast(triggerIntent, null);
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
        Log.i(getClass().getSimpleName(), "Selected menu - " + id);

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
            break;
            case R.id.action_menu_search: {
                bNeedConnect = true;
                startActivity(new Intent(this, SearchActivity.class));
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inventory(boolean bStart) {
        Intent intent;
        if(bStart) {
            intent = new Intent(UGRApplication.UGR_ACTION_START, null);
        } else {
            intent = new Intent(UGRApplication.UGR_ACTION_CANCEL, null);
        }
        sendOrderedBroadcast(intent, null);
    }

    private void RFIDEnable(boolean bOn) {
        int nExtra;
        if(bOn)
            nExtra = 1;
        else
            nExtra = 0;
        Intent intent = new Intent(UGRApplication.UGR_ACTION_ENABLE, null);
        intent.putExtra(UGRApplication.UGR_EXTRA_ENABLE, nExtra);
        intent.putExtra("module_reset", bOn);
        sendOrderedBroadcast(intent, null);
    }
}
