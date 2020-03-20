package com.m3.m3sdk_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;


public class ImageCaptureSettingsActivity extends Activity {

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    private Spinner spinner;
    private CheckBox cb_illumiation;
    private CheckBox cb_decoding_illumination;
    private CheckBox cb_decode_aiming;
    private Button btnSet;
    private Button btnCancel;
    private EditText edtExposureTime;
    private Context mContext;
    private static String TAG = "ImageCaptureSettingsActivity";

    public int mIlluminationLevel = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext=this;
        btnSet = findViewById(R.id.btn_set);
        btnCancel=findViewById(R.id.btn_cancel);
        spinner = findViewById(R.id.spinner);
        cb_illumiation = findViewById(R.id.cb_illumination);
        cb_decoding_illumination = findViewById(R.id.cb_decoding_illumination);
        cb_decode_aiming = findViewById(R.id.cb_decode_aiming);
        edtExposureTime = findViewById(R.id.edt_exposure_time);

        // image resolution setting
        RadioButton resolution_full = (RadioButton)findViewById(R.id.resolution_full);
        RadioButton resolution_half = (RadioButton)findViewById(R.id.resolution_half);
        RadioButton resolution_quarter = (RadioButton)findViewById(R.id.resolution_quarter);

        resolution_full.setOnClickListener(OnResolutionClickListener);
        resolution_half.setOnClickListener(OnResolutionClickListener);
        resolution_quarter.setOnClickListener(OnResolutionClickListener);

        resolution_full.setChecked(true);

        // image enhancement
        RadioButton enhancement_off = (RadioButton)findViewById(R.id.enhancement_off);
        RadioButton enhancement_low = (RadioButton)findViewById(R.id.enhancement_low);
        RadioButton enhancement_medium = (RadioButton)findViewById(R.id.enhancement_medium);
        RadioButton enhancement_high = (RadioButton)findViewById(R.id.enhancement_high);

        enhancement_off.setOnClickListener(OnEnhancementClickListener);
        enhancement_low.setOnClickListener(OnEnhancementClickListener);
        enhancement_medium.setOnClickListener(OnEnhancementClickListener);
        enhancement_high.setOnClickListener(OnEnhancementClickListener);

        enhancement_off.setChecked(true);

        arrayList = new ArrayList<String>();
        for(int i=10; 1<=i; i--)
            arrayList.add("Level"+i);

        edtExposureTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String edtStr=s.toString();
                if(edtStr.length()>0){
                    if(Integer.parseInt(edtStr)>960 && Integer.parseInt(edtStr)>0){
                        edtExposureTime.setText(null);
                        Toast.makeText(getApplicationContext(), "Only from 0 to 960", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList);

        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mIlluminationLevel = 10-i; //선택된 레벨 파라메터값.
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;

                intent = new Intent(ConstantValues.SCANNER_ACTION_PARAMETER);  //Illumination Parameter Enable
                intent.putExtra("symbology", 361);
                intent.putExtra("value", cb_illumiation.isChecked()?1:0);
                mContext.sendOrderedBroadcast(intent, null);

                intent = new Intent(ConstantValues.SCANNER_ACTION_PARAMETER);  //Level of Illumination Parameter
                intent.putExtra("symbology", 764);
                intent.putExtra("value", mIlluminationLevel);
                mContext.sendOrderedBroadcast(intent, null);

                intent = new Intent(ConstantValues.SCANNER_ACTION_PARAMETER);  //decoding illumination Parameter
                intent.putExtra("symbology", 298);
                intent.putExtra("value", cb_decoding_illumination.isChecked()?1:0);
                mContext.sendOrderedBroadcast(intent, null);

                intent = new Intent(ConstantValues.SCANNER_ACTION_PARAMETER);  //Decode Aiming Pattern
                intent.putExtra("symbology", 306);
                intent.putExtra("value", cb_decode_aiming.isChecked()?1:0);
                mContext.sendOrderedBroadcast(intent, null);

                if(!edtExposureTime.getText().toString().isEmpty()) {
                    int value = Integer.parseInt(edtExposureTime.getText().toString());
                    Log.d(TAG, "value: "+value);
                    intent = new Intent(ConstantValues.SCANNER_ACTION_PARAMETER);  //Exposure Time Parameter
                    intent.putExtra("symbology", 567);
                    intent.putExtra("value", value);
                    mContext.sendOrderedBroadcast(intent, null);
                    finish();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    RadioButton.OnClickListener OnResolutionClickListener = new RadioButton.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ConstantValues.SCANNER_ACTION_PARAMETER);
            intent.putExtra("symbology", 302);

            switch(v.getId()){
                case R.id.resolution_full:
                    intent.putExtra("value", 0);
                    break;
                case R.id.resolution_half:
                    intent.putExtra("value", 1);
                    break;
                case R.id.resolution_quarter:
                    intent.putExtra("value", 3);
                    break;
            }
            mContext.sendOrderedBroadcast(intent, null);
        }

    };
    RadioButton.OnClickListener OnEnhancementClickListener = new RadioButton.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ConstantValues.SCANNER_ACTION_PARAMETER);
            intent.putExtra("symbology", 564);

            switch(v.getId()){
                case R.id.enhancement_off:
                    intent.putExtra("value", 0);
                    break;
                case R.id.enhancement_low:
                    intent.putExtra("value", 1);
                    break;
                case R.id.enhancement_medium:
                    intent.putExtra("value", 2);
                    break;
                case R.id.enhancement_high:
                    intent.putExtra("value", 3);
                    break;
            }
            mContext.sendOrderedBroadcast(intent, null);
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        edtExposureTime.setText("0");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
