package net.m3mobile.ugr_demo

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import net.m3mobile.ugr_demo.UHFTag
import android.os.Bundle
import net.m3mobile.ugr_demo.R
import android.content.IntentFilter
import net.m3mobile.ugr_demo.UGRApplication
import android.text.InputType
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Button
import net.m3mobile.ugr_demo.ResultWindow
import java.util.ArrayList
import java.util.HashMap

class SearchActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "SearchActivity"
    var mEdSearch: EditText? = null
    var mEdTime: EditText? = null
    var mBtnSearch: Button? = null
    var mBtnClear: Button? = null
    var mTvCount: TextView? = null
    var mIsReading = false
    private var mTAGs: ArrayList<HashMap<String, UHFTag>>? = null
    private var resultReceiver: ResultWindowReceiver? = null
    private var mRunnable: Runnable? = null
    private var mHandler: Handler? = null
    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        mEdSearch = findViewById<View>(R.id.edit_search) as EditText
        mEdSearch!!.setOnClickListener(this)
        mBtnSearch = findViewById<View>(R.id.btn_search) as Button
        mBtnSearch!!.setOnClickListener(this)
        mBtnClear = findViewById<View>(R.id.btn_clear) as Button
        mBtnClear!!.setOnClickListener(this)
        mEdTime = findViewById<View>(R.id.edit_time) as EditText
        mEdTime!!.setOnClickListener(this)
        mTvCount = findViewById<View>(R.id.tv_count) as TextView
        mTAGs = ArrayList()
        resultReceiver = ResultWindowReceiver()
        val filter = IntentFilter()
        filter.addAction(UGRApplication.UGR_ACTION_EPC)
        filter.addAction(UGRApplication.UGR_ACTION_IS_READING)
        registerReceiver(resultReceiver, filter)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Searching...")
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_search -> if (mIsReading == false) {
                inventory(true)
                mRunnable = Runnable { inventory(false) }
                mHandler = Handler()
                val time = mEdTime!!.text.toString().toInt()
                if (time > 0) mHandler!!.postDelayed(mRunnable!!, time.toLong())
            } else {
                inventory(false)
                mHandler!!.removeCallbacks(mRunnable!!)
            }
            R.id.edit_search -> openDialog(mEdSearch)
            R.id.edit_time -> openDialog(mEdTime)
            R.id.btn_clear -> {
                mTAGs!!.clear()
                mEdTime!!.setText("" + 5000)
                mEdSearch!!.setText("")
                mTvCount!!.text = "Count : 0"
            }
        }
    }

    fun openDialog(edit: EditText?) {
        val builder = AlertDialog.Builder(this)
        val editText = EditText(this)
        if (edit === mEdTime) editText.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(editText)
        builder.setPositiveButton("Ok") { dialogInterface, i ->
            Log.d(TAG, "Ok Button Click")
            val strValue = editText.text.toString()
            edit!!.setText(strValue)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            Log.d(TAG, "Cancel Button Click")
            dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun inventory(bStart: Boolean) {
        val intent: Intent
        if (bStart) {
            intent = Intent(UGRApplication.UGR_ACTION_START, null)
            progressDialog!!.show()
        } else {
            intent = Intent(UGRApplication.UGR_ACTION_CANCEL, null)
            progressDialog!!.dismiss()
            if (mTAGs!!.size < 1) {
                Log.d(TAG, "Fail")
            }
        }
        sendOrderedBroadcast(intent, null)
    }

    inner class ResultWindowReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val epc: String?
            if (intent.action == UGRApplication.UGR_ACTION_EPC) {
                epc = intent.extras!!.getString(UGRApplication.UGR_EXTRA_EPC_DATA)
                val strSearchValue = mEdSearch!!.text.toString()
                if (epc != null && !strSearchValue.isEmpty() && epc.contains(strSearchValue)) {
                    var existTag = false
                    val hashMap = HashMap<String, UHFTag>()
                    hashMap[epc] = UHFTag(epc, 1)
                    for (i in mTAGs!!.indices) {
                        val tm = mTAGs!![i]
                        if (tm != null) {
                            if (tm.containsKey(epc)) {
                                tm[epc]!!.Reads++
                                existTag = true
                                break
                            }
                        }
                    }
                    if (!existTag) {
                        mTAGs!!.add(hashMap)
                        val nSize = mTAGs!!.size
                        mTvCount!!.text = "Count : $nSize"
                        if (nSize > 0) {
                            Log.d(TAG, "Success")
                        }
                    }
                }
            } else if (intent.action == UGRApplication.UGR_ACTION_IS_READING) {
                mIsReading = intent.extras!!.getBoolean(UGRApplication.UGR_EXTRA_IS_READING)
                if (mIsReading) {
                    mBtnSearch!!.text = "Stop"
                } else {
                    mBtnSearch!!.text = "Search"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(resultReceiver)
        ResultWindow.bNeedConnect = true
    }

    override fun onResume() {
        super.onResume()
        var intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "read_mode")
        intent.putExtra("read_mode_value", 0)
        sendOrderedBroadcast(intent, null)

        /*intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "output_mode");
        intent.putExtra("output_mode_value", 2);
        sendOrderedBroadcast(intent, null);*/intent =
            Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "end_char")
        intent.putExtra("end_char_value", 6)
        sendOrderedBroadcast(intent, null)
        intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "sound")
        intent.putExtra("sound_value", 0)
        sendOrderedBroadcast(intent, null)
    }

    override fun onPause() {
        super.onPause()
        var intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "read_mode")
        intent.putExtra("read_mode_value", 2)
        sendOrderedBroadcast(intent, null)

        /*intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "output_mode");
        intent.putExtra("output_mode_value", 0);
        sendOrderedBroadcast(intent, null);*/intent =
            Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "end_char")
        intent.putExtra("end_char_value", 3)
        sendOrderedBroadcast(intent, null)
        intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "sound")
        intent.putExtra("sound_value", 1)
        sendOrderedBroadcast(intent, null)
    }
}