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
 * Created by M3 on 2017-12-18.
 */
class LockActivity : AppCompatActivity() {
    var mEdAccPwd: EditText? = null
    var mEdKillPwd: EditText? = null
    var mTvResult: TextView? = null
    var nAccPermission = 0
    var nKillPermission = 0
    var nEpcPermission = 0
    var nTidPermission = 0
    var nUserPermission = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)
        mEdAccPwd = findViewById<View>(R.id.edit_lock_access_pwd) as EditText
        mEdAccPwd!!.setText("00000000")
        mEdKillPwd = findViewById<View>(R.id.edit_kill_pwd) as EditText
        mEdKillPwd!!.setText("00000000")
        mTvResult = findViewById<View>(R.id.textLockResult) as TextView
        val accSpinner = findViewById<View>(R.id.spinner_accpwd) as Spinner
        accSpinner.setSelection(4)
        accSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                nAccPermission = position
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        val killSpinner = findViewById<View>(R.id.spinner_killpwd) as Spinner
        killSpinner.setSelection(4)
        killSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                nKillPermission = position
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        val epcSpinner = findViewById<View>(R.id.spinner_epc) as Spinner
        epcSpinner.setSelection(4)
        epcSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                nEpcPermission = position
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        val tidSpinner = findViewById<View>(R.id.spinner_tid) as Spinner
        tidSpinner.setSelection(4)
        tidSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                nTidPermission = position
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        val userSpinner = findViewById<View>(R.id.spinner_user) as Spinner
        userSpinner.setSelection(4)
        userSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                nUserPermission = position
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        // Lock
        val lockButton = findViewById<View>(R.id.button_lock) as Button
        lockButton.setOnClickListener {
            val intent = Intent(UGRApplication.UGR_ACTION_LOCK)
            intent.putExtra("acc_permission", nAccPermission)
            intent.putExtra("kill_permission", nKillPermission)
            intent.putExtra("epc_permission", nEpcPermission)
            intent.putExtra("tid_permission", nTidPermission)
            intent.putExtra("user_permission", nUserPermission)
            intent.putExtra("acc_pwd", mEdAccPwd!!.text.toString())
            sendOrderedBroadcast(intent, null)
        }

        // Kill
        val killButton = findViewById<View>(R.id.button_kill) as Button
        killButton.setOnClickListener {
            val intent = Intent(UGRApplication.UGR_ACTION_KILL)
            intent.putExtra("kill_pwd", mEdKillPwd!!.text.toString())
            sendOrderedBroadcast(intent, null)
        }
        val filter = IntentFilter()
        filter.addAction(UGRApplication.UGR_ACTION_LOCK_RESPONSE)
        filter.addAction(UGRApplication.UGR_ACTION_KILL_RESPONSE)
        registerReceiver(UGRAccessReceiver, filter)
    }

    var UGRAccessReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        var bSuccess = false
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("onReceive", intent.action!!)
            if (intent.action == UGRApplication.UGR_ACTION_LOCK_RESPONSE) {
                bSuccess = intent.extras!!.getBoolean("success")
                val strMessage = intent.extras!!.getString("message")
                Log.d("onReceive", "bSuccess = $bSuccess")
                if (bSuccess) mTvResult!!.text = "Lock success" else mTvResult!!.text =
                    "Result: Fail to change permissions.\n$strMessage"
            } else if (intent.action == UGRApplication.UGR_ACTION_KILL_RESPONSE) {
                bSuccess = intent.extras!!.getBoolean("success")
                Log.d("onReceive", "bSuccess = $bSuccess")
                if (bSuccess) mTvResult!!.text = "Kill success" else mTvResult!!.text =
                    "Result: Fail to Kill Tag."
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(UGRAccessReceiver)
        ResultWindow.bNeedConnect = false
        super.onDestroy()
    }
}