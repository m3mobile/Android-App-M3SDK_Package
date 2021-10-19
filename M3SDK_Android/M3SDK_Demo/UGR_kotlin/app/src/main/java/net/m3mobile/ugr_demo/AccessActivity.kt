package net.m3mobile.ugr_demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import net.m3mobile.ugr_demo.R
import android.widget.AdapterView.OnItemSelectedListener
import android.content.Intent
import net.m3mobile.ugr_demo.UGRApplication
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.*
import net.m3mobile.ugr_demo.ResultWindow

/**
 * Created by M3 on 2017-12-14.
 */
class AccessActivity : AppCompatActivity() {
    var nMemBank = 0
    var mEtOffset: EditText? = null
    var mEtLength: EditText? = null
    var mEtPwd: EditText? = null
    var mTvResult: TextView? = null
    var mTvReadingResult: TextView? = null
    var mEtWriting: EditText? = null
    var mBtnReading: Button? = null
    var mBtnWriting: Button? = null
    var mBtnClear: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)
        val bank = findViewById<View>(R.id.spinner_membank) as Spinner
        bank.setSelection(1)
        bank.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                nMemBank = position
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        mEtOffset = findViewById<View>(R.id.edit_access_offset) as EditText
        mEtOffset!!.setText("02")
        mEtLength = findViewById<View>(R.id.edit_access_length) as EditText
        mEtLength!!.setText("06")
        mEtPwd = findViewById<View>(R.id.edit_access_pwd) as EditText
        mEtPwd!!.setText("00000000")
        mTvResult = findViewById<View>(R.id.textResult) as TextView
        mTvReadingResult = findViewById<View>(R.id.txt_reading) as TextView
        mEtWriting = findViewById<View>(R.id.edit_writing) as EditText
        mBtnClear = findViewById<View>(R.id.button_clear) as Button
        mBtnClear!!.setOnClickListener {
            mEtOffset!!.setText("02")
            mEtLength!!.setText("06")
            mEtPwd!!.setText("00000000")
            mEtWriting!!.setText("")
            mTvReadingResult!!.text = ""
            mTvResult!!.text = ""
        }
        mBtnReading = findViewById<View>(R.id.button_reading) as Button
        mBtnReading!!.setOnClickListener {
            val nOffset = mEtOffset!!.text.toString().toInt(16)
            val nLength = mEtLength!!.text.toString().toInt(16)
            val intent = Intent(UGRApplication.UGR_ACTION_MEMORY_READING)
            intent.putExtra("memory_bank", nMemBank)
            intent.putExtra("offset", nOffset)
            intent.putExtra("length", nLength)
            intent.putExtra("password", mEtPwd!!.text.toString())
            sendOrderedBroadcast(intent, null)
        }
        mBtnWriting = findViewById<View>(R.id.button_writing) as Button
        mBtnWriting!!.setOnClickListener {
            val nOffset = mEtOffset!!.text.toString().toInt(16)
            val nLength = mEtLength!!.text.toString().toInt(16)
            val intent = Intent(UGRApplication.UGR_ACTION_MEMORY_WRITING)
            intent.putExtra("memory_bank", nMemBank)
            intent.putExtra("offset", nOffset)
            intent.putExtra("length", nLength)
            intent.putExtra("data", mEtWriting!!.text.toString())
            intent.putExtra("password", mEtPwd!!.text.toString())
            sendOrderedBroadcast(intent, null)
        }
        val filter = IntentFilter()
        filter.addAction(UGRApplication.UGR_ACTION_MEMORY_RESPONSE)
        registerReceiver(UGRAccessReceiver, filter)
    }

    var UGRAccessReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        var strData: String? = null
        var bSuccess = false
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("onReceive", intent.action!!)
            if (intent.action == UGRApplication.UGR_ACTION_MEMORY_RESPONSE) {
                strData = intent.extras!!.getString(UGRApplication.UGR_EXTRA_MEMORY)
                bSuccess = intent.extras!!.getBoolean("success")
                Log.d("onReceive", "strData = $strData, bSuccess = $bSuccess")
                if (bSuccess) mTvReadingResult!!.text = strData else mTvResult!!.text = strData
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(UGRAccessReceiver)
        ResultWindow.bNeedConnect = false
        super.onDestroy()
    }
}