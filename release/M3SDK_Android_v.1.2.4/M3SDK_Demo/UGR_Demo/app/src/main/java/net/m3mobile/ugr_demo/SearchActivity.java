package net.m3mobile.ugr_demo;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mEdSearch;
    EditText mEdTime;
    Button mBtnSearch;
    Button mBtnClear;

    TextView mTvCount;

    boolean mIsReading = false;

    private ArrayList<HashMap<String, UhfTag>> mTAGs;
    private ResultWindowReceiver resultReceiver;

    private Runnable mRunnable;
    private Handler mHandler;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mEdSearch = (EditText) findViewById(R.id.edit_search);
        mEdSearch.setOnClickListener(this);
        mBtnSearch = (Button) findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(this);
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(this);
        mEdTime = (EditText) findViewById(R.id.edit_time);
        mEdTime.setOnClickListener(this);
        mTvCount = (TextView) findViewById(R.id.tv_count);

        mTAGs = new ArrayList<>();
        resultReceiver = new ResultWindowReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UGRApplication.UGR_ACTION_EPC);
        filter.addAction(UGRApplication.UGR_ACTION_IS_READING);
        registerReceiver(resultReceiver, filter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching...");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                if(mIsReading == false) {
                    inventory(true);
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            inventory(false);
                        }
                    };
                    mHandler = new Handler();
                    int time = Integer.parseInt(mEdTime.getText().toString());
                    if(time > 0)
                        mHandler.postDelayed(mRunnable, time);
                } else {
                    inventory(false);
                    mHandler.removeCallbacks(mRunnable);
                }
                break;
            case R.id.edit_search:
                openDialog(mEdSearch);
                break;
            case R.id.edit_time:
                openDialog(mEdTime);
                break;
            case R.id.btn_clear:
                mTAGs.clear();
                mEdTime.setText("" + 5000);
                mEdSearch.setText("");
                mTvCount.setText("Count : 0");
                break;
        }
    }

    void openDialog(final EditText edit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);
        if(edit == mEdTime)
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editText);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(SearchActivity.this.getClass().getSimpleName(), "Ok Button Click");

                String strValue = editText.getText().toString();
                edit.setText(strValue);

                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(SearchActivity.this.getClass().getSimpleName(), "Cancel Button Click");

                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void inventory(boolean bStart) {
        Intent intent;
        if(bStart) {
            intent = new Intent(UGRApplication.UGR_ACTION_START, null);
            progressDialog.show();
        } else {
            intent = new Intent(UGRApplication.UGR_ACTION_CANCEL, null);
            progressDialog.dismiss();
            if(mTAGs.size() < 1) {
                Log.d(SearchActivity.this.getClass().getSimpleName(), "Fail");
            }
        }
        sendOrderedBroadcast(intent, null);
    }

    public class ResultWindowReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String epc;

            //Log.i(getClass().getSimpleName(), "onReceive [" + intent.getAction() + "]");

            if(intent.getAction().equals(UGRApplication.UGR_ACTION_EPC)) {
                epc = intent.getExtras().getString(UGRApplication.UGR_EXTRA_EPC_DATA);

                String strSearchValue = mEdSearch.getText().toString();

                if(epc != null && !strSearchValue.isEmpty() && epc.contains(strSearchValue)) {
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

                        mTvCount.setText("Count : "  + nSize);
                        if(nSize > 0) {
                            Log.d(SearchActivity.this.getClass().getSimpleName(), "Success");
                        }
                    }

                }

            } else if(intent.getAction().equals(UGRApplication.UGR_ACTION_IS_READING)) {
                mIsReading = intent.getExtras().getBoolean(UGRApplication.UGR_EXTRA_IS_READING);
                if(mIsReading) {
                    mBtnSearch.setText("Stop");
                } else {
                    mBtnSearch.setText("Search");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(resultReceiver);

        ResultWindow.bNeedConnect = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "read_mode");
        intent.putExtra("read_mode_value", 0);
        sendOrderedBroadcast(intent, null);

        /*intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "output_mode");
        intent.putExtra("output_mode_value", 2);
        sendOrderedBroadcast(intent, null);*/

        intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "end_char");
        intent.putExtra("end_char_value", 6);
        sendOrderedBroadcast(intent, null);

        intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "sound");
        intent.putExtra("sound_value", 0);
        sendOrderedBroadcast(intent, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Intent intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "read_mode");
        intent.putExtra("read_mode_value", 2);
        sendOrderedBroadcast(intent, null);

        /*intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "output_mode");
        intent.putExtra("output_mode_value", 0);
        sendOrderedBroadcast(intent, null);*/

        intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "end_char");
        intent.putExtra("end_char_value", 3);
        sendOrderedBroadcast(intent, null);

        intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "sound");
        intent.putExtra("sound_value", 1);
        sendOrderedBroadcast(intent, null);
    }
}
