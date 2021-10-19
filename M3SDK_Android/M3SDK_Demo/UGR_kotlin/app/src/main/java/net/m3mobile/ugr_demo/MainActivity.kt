/*
 *  v.0.0.2 2018-06-08  한재윤     code.2  SearchActivity, ConfigPreferenceActivity - channel setting 추가
 *  v.0.0.3 2018-06-25  한재윤     code.3  CarrierWave 추가
 *  v.1.0.0 2019-01-09  한재윤     code.4  channel, carrier wave 원복. lock 기능 개선
 *  v.1.0.1 2019-07-29  한재윤     code.5  ResultWindow_aidl activity 추가
 *  v.1.0.2 2020-01-20  한재윤     code.6  aidl method 추가
 *  v.1.0.3 2020-03-16  한재윤     code.7  aidl callback 에 onInventoryRssi 추가
 *  v.1.0.4 2021-10-19  한재윤     code.8  ResultWindow_aidl 안정화
 */
package net.m3mobile.ugr_demo

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.view.View

class MainActivity : Activity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_uhf_intent).setOnClickListener(this)
        findViewById<View>(R.id.btn_uhf_aidl).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val intent: Intent
        when (v.id) {
            R.id.btn_uhf_intent -> {
                intent = Intent(this, ResultWindow::class.java)
                startActivity(intent)
            }
            R.id.btn_uhf_aidl -> {
                intent = Intent(this, ResultWindow_aidl::class.java)
                startActivity(intent)
            }
        }
    }
}