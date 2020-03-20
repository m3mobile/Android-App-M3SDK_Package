package net.m3mobile.ugr_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by M3 on 2017-12-14.
 */

public class AccessActivity extends AppCompatActivity {

    int nMemBank = 0;
    EditText mEtOffset;
    EditText mEtLength;
    EditText mEtPwd;

    TextView mTvResult;
    TextView mTvReadingResult;
    EditText mEtWriting;

    Button mBtnReading;
    Button mBtnWriting;
    Button mBtnClear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        Spinner bank = (Spinner) findViewById(R.id.spinner_membank);
        bank.setSelection(1);
        bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                nMemBank = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mEtOffset = (EditText) findViewById(R.id.edit_access_offset);
        mEtOffset.setText("02");
        mEtLength = (EditText) findViewById(R.id.edit_access_length);
        mEtLength.setText("06");
        mEtPwd = (EditText) findViewById(R.id.edit_access_pwd);
        mEtPwd.setText("00000000");

        mTvResult = (TextView) findViewById(R.id.textResult);
        mTvReadingResult = (TextView) findViewById(R.id.txt_reading);
        mEtWriting = (EditText) findViewById(R.id.edit_writing);

        mBtnClear = (Button) findViewById(R.id.button_clear);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEtOffset.setText("02");
                mEtLength.setText("06");
                mEtPwd.setText("00000000");
                mEtWriting.setText("");
                mTvReadingResult.setText("");
                mTvResult.setText("");
            }
        });

        mBtnReading = (Button) findViewById(R.id.button_reading);
        mBtnReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int nOffset = Integer.parseInt(mEtOffset.getText().toString(), 16);
                int nLength = Integer.parseInt(mEtLength.getText().toString(), 16);

                Intent intent = new Intent(UGRApplication.UGR_ACTION_MEMORY_READING);
                intent.putExtra("memory_bank", nMemBank);
                intent.putExtra("offset", nOffset);
                intent.putExtra("length", nLength);
                intent.putExtra("password", mEtPwd.getText().toString());
                sendOrderedBroadcast(intent, null);
            }
        });

        mBtnWriting = (Button) findViewById(R.id.button_writing);
        mBtnWriting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int nOffset = Integer.parseInt(mEtOffset.getText().toString(), 16);
                int nLength = Integer.parseInt(mEtLength.getText().toString(), 16);

                Intent intent = new Intent(UGRApplication.UGR_ACTION_MEMORY_WRITING);
                intent.putExtra("memory_bank", nMemBank);
                intent.putExtra("offset", nOffset);
                intent.putExtra("length", nLength);
                intent.putExtra("data", mEtWriting.getText().toString());
                intent.putExtra("password", mEtPwd.getText().toString());
                sendOrderedBroadcast(intent, null);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(UGRApplication.UGR_ACTION_MEMORY_RESPONSE);
        registerReceiver(UGRAccessReceiver, filter);
    }

    public BroadcastReceiver UGRAccessReceiver = new BroadcastReceiver() {

        String strData;
        boolean bSuccess;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive", intent.getAction());

            if(intent.getAction().equals(UGRApplication.UGR_ACTION_MEMORY_RESPONSE)) {
                strData = intent.getExtras().getString(UGRApplication.UGR_EXTRA_MEMORY);
                bSuccess = intent.getExtras().getBoolean("success");

                Log.d("onReceive", "strData = " + strData + ", bSuccess = " + bSuccess);
                if(bSuccess)
                    mTvReadingResult.setText(strData);
                else
                    mTvResult.setText(strData);
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(UGRAccessReceiver);
        ResultWindow.bNeedConnect = false;

        super.onDestroy();
    }
}
