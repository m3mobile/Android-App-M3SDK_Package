package net.m3mobile.ugr_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by M3 on 2017-12-18.
 */

public class LockActivity extends AppCompatActivity {

    EditText mEdAccPwd;
    EditText mEdKillPwd;

    TextView mTvResult;

    int nAccPermission;
    int nKillPermission;
    int nEpcPermission;
    int nTidPermission;
    int nUserPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        mEdAccPwd = (EditText) findViewById(R.id.edit_lock_access_pwd);
        mEdAccPwd.setText("00000000");
        mEdKillPwd = (EditText) findViewById(R.id.edit_kill_pwd);
        mEdKillPwd.setText("00000000");
        mTvResult = (TextView) findViewById(R.id.textLockResult);

        Spinner accSpinner = (Spinner) findViewById(R.id.spinner_accpwd);
        accSpinner.setSelection(4);
        accSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                nAccPermission = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner killSpinner = (Spinner) findViewById(R.id.spinner_killpwd);
        killSpinner.setSelection(4);
        killSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                nKillPermission = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner epcSpinner = (Spinner) findViewById(R.id.spinner_epc);
        epcSpinner.setSelection(4);
        epcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                nEpcPermission = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner tidSpinner = (Spinner) findViewById(R.id.spinner_tid);
        tidSpinner.setSelection(4);
        tidSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                nTidPermission = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner userSpinner = (Spinner) findViewById(R.id.spinner_user);
        userSpinner.setSelection(4);
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                nUserPermission = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Lock
        Button lockButton = (Button) findViewById(R.id.button_lock);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UGRApplication.UGR_ACTION_LOCK);
                intent.putExtra("acc_permission", nAccPermission);
                intent.putExtra("kill_permission", nKillPermission);
                intent.putExtra("epc_permission", nEpcPermission);
                intent.putExtra("tid_permission", nTidPermission);
                intent.putExtra("user_permission", nUserPermission);
                intent.putExtra("acc_pwd", mEdAccPwd.getText().toString());
                sendOrderedBroadcast(intent, null);
            }
        });

        // Kill
        Button killButton = (Button) findViewById(R.id.button_kill);
        killButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UGRApplication.UGR_ACTION_KILL);
                intent.putExtra("kill_pwd", mEdKillPwd.getText().toString());
                sendOrderedBroadcast(intent, null);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(UGRApplication.UGR_ACTION_LOCK_RESPONSE);
        filter.addAction(UGRApplication.UGR_ACTION_KILL_RESPONSE);
        registerReceiver(UGRAccessReceiver, filter);
    }

    public BroadcastReceiver UGRAccessReceiver = new BroadcastReceiver() {

        boolean bSuccess;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive", intent.getAction());

            if(intent.getAction().equals(UGRApplication.UGR_ACTION_LOCK_RESPONSE)) {
                bSuccess = intent.getExtras().getBoolean("success");
                String strMessage = intent.getExtras().getString("message");

                Log.d("onReceive", "bSuccess = " + bSuccess);
                if(bSuccess)
                    mTvResult.setText("Lock success");
                else
                    mTvResult.setText("Result: Fail to change permissions.\n" + strMessage);
            } else if(intent.getAction().equals(UGRApplication.UGR_ACTION_KILL_RESPONSE)) {
                bSuccess = intent.getExtras().getBoolean("success");

                Log.d("onReceive", "bSuccess = " + bSuccess);
                if(bSuccess)
                    mTvResult.setText("Kill success");
                else
                    mTvResult.setText("Result: Fail to Kill Tag.");
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
