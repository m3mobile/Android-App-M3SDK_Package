/*
 *  v.0.0.2 2018-06-08  한재윤     code.2  SearchActivity, ConfigPreferenceActivity - channel setting 추가
 *  v.0.0.3 2018-06-25  한재윤     code.3  CarrierWave 추가
 *  v.1.0.0 2019-01-09  한재윤     code.4  channel, carrier wave 원복. lock 기능 개선
 *  v.1.0.1 2019-07-29  한재윤     code.5  ResultWindow_aidl activity 추가
 *  v.1.0.2 2020-01-20  한재윤     code.6  aidl method 추가
 *  v.1.0.3 2020-03-16  한재윤     code.7  aidl callback 에 onInventoryRssi 추가
 *  v.1.0.4 2021-10-19  한재윤     code.8  ResultWindow_aidl 안정화
 */

package net.m3mobile.ugr_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_uhf_intent).setOnClickListener(this);
        findViewById(R.id.btn_uhf_aidl).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_uhf_intent:
                intent = new Intent(this, ResultWindow.class);
                startActivity(intent);
                break;
            case R.id.btn_uhf_aidl:
                intent = new Intent(this, ResultWindow_aidl.class);
                startActivity(intent);
                break;
        }
    }
}
