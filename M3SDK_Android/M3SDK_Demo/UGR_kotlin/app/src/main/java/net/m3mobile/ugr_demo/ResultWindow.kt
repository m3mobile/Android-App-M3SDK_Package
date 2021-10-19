package net.m3mobile.ugr_demo

import android.support.v7.app.AppCompatActivity
import net.m3mobile.ugr_demo.UHFTag
import net.m3mobile.ugr_demo.TagAdapter
import android.content.IntentFilter
import net.m3mobile.ugr_demo.R
import net.m3mobile.ugr_demo.UGRApplication
import android.content.Intent
import net.m3mobile.ugr_demo.ResultWindow
import android.content.BroadcastReceiver
import net.m3mobile.ugr_demo.ConfigPreferenceActivity
import net.m3mobile.ugr_demo.AccessActivity
import net.m3mobile.ugr_demo.LockActivity
import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by M3 on 2017-12-11.
 */
class ResultWindow : AppCompatActivity() {
    private val TAG = "ResultWindow"
    var mListInventory: ListView? = null
    var mBtnStart: Button? = null
    var mBtnClear: Button? = null
    var mBtnExport: Button? = null
    var mTagsCount: TextView? = null
    var mInventoryTime: TextView? = null
    var mTvScannerResult: TextView? = null
    var mIsReading = false
    private var mTAGs: ArrayList<HashMap<String, UHFTag>>? = null
    private var adapter: TagAdapter? = null
    private var resultReceiver: ResultWindowReceiver? = null
    private var mCodeReceiver: BarcodeReceiver? = null
    private var mBarcodeFilter: IntentFilter? = null
    var triggerGroup: RadioGroup? = null
    var mLastTriggerMode = 2
    private var uhfTagArrayList: ArrayList<UHFTag>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_window)
        mainScreen()
        resultReceiver = ResultWindowReceiver()
        val filter = IntentFilter()
        filter.addAction(UGRApplication.UGR_ACTION_EPC)
        filter.addAction(UGRApplication.UGR_ACTION_IS_READING)
        filter.addAction(UGRApplication.SCANNER_KEY_INTENT)
        mCodeReceiver = BarcodeReceiver()
        mBarcodeFilter = IntentFilter()
        mBarcodeFilter!!.addAction(UGRApplication.SCANNER_ACTION_BARCODE)
        registerReceiver(resultReceiver, filter)
        registerReceiver(mCodeReceiver, mBarcodeFilter)

        //RFIDEnable(true);
    }

    protected fun mainScreen() {
        mListInventory = findViewById<View>(R.id.listView_Inventory) as ListView
        mBtnStart = findViewById<View>(R.id.btnStart) as Button
        mBtnClear = findViewById<View>(R.id.btnClear) as Button
        mBtnExport = findViewById<View>(R.id.btnExport) as Button
        mTagsCount = findViewById<View>(R.id.textView_count) as TextView
        mTagsCount!!.text = "TAGS Count\n0"
        mInventoryTime = findViewById<View>(R.id.textView_time) as TextView
        mTvScannerResult = findViewById<View>(R.id.scanresult_intent) as TextView
        triggerGroup = findViewById<View>(R.id.radio_trigger_mode) as RadioGroup
        val triggerRFID = findViewById<View>(R.id.radio_trigger_rfid) as RadioButton
        val triggerScanner = findViewById<View>(R.id.radio_trigger_scanner) as RadioButton
        val triggerBoth = findViewById<View>(R.id.radio_trigger_both) as RadioButton
        triggerRFID.setOnClickListener(OnTriggerClickListener2)
        triggerScanner.setOnClickListener(OnTriggerClickListener2)
        triggerBoth.setOnClickListener(OnTriggerClickListener2)
        triggerBoth.isChecked = true
        mTAGs = ArrayList()
        uhfTagArrayList = ArrayList()
        adapter = TagAdapter(this, mTAGs!!, R.layout.listview_item_row, null, null)
        mListInventory!!.transcriptMode = AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL
        mListInventory!!.adapter = adapter
        mBtnStart!!.setOnClickListener {
            if (mIsReading == false) {
                inventory(true)
            } else {
                inventory(false)
            }
        }
        mBtnClear!!.setOnClickListener {
            mTAGs!!.clear()
            adapter!!.notifyDataSetChanged()
            mTagsCount!!.text = "TAGS Count\n0"
            uhfTagArrayList!!.clear()
        }
        mBtnExport!!.setOnClickListener(View.OnClickListener {
            val time = System.currentTimeMillis()
            val dayTime = SimpleDateFormat("yyyyMMdd_hhmmss")
            val strFolderName = Environment.getExternalStorageDirectory().path +
                    "/android/data/net.m3mobile.ugremul"
            val strFileName = "/export_" + dayTime.format(Date(time)) + ".txt"
            for (i in mTAGs!!.indices) {
                val tm = mTAGs!![i]
                if (tm != null) {
                    val epc = tm.values.toTypedArray()[0]
                    val strTAG = epc.TIME + "    " + epc.EPC
                    if (!exportTxtFile(strFolderName, strFileName, strTAG)) {
                        Toast.makeText(this@ResultWindow, "Export data Failed!!", Toast.LENGTH_LONG)
                            .show()
                        return@OnClickListener
                    }
                }
            }
            Toast.makeText(
                this@ResultWindow,
                "Export data Success!! on '$strFolderName$strFileName'",
                Toast.LENGTH_LONG
            ).show()
        })
    }

    override fun onResume() {
        super.onResume()
        var intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "read_mode")
        intent.putExtra("read_mode_value", 0)
        sendOrderedBroadcast(intent, null)
        intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "trigger_mode")
        intent.putExtra("trigger_mode_value", mLastTriggerMode)
        sendOrderedBroadcast(intent, null)

        /*intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
        intent.putExtra("setting", "sound");
        intent.putExtra("sound_value", 0);
        sendOrderedBroadcast(intent, null);*/intent =
            Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "end_char")
        intent.putExtra("end_char_value", 6)
        sendOrderedBroadcast(intent, null)
        intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "output_mode")
        intent.putExtra("output_mode_value", 2)
        sendOrderedBroadcast(intent, null)
        if (!bNeedConnect) RFIDEnable(true)
    }

    inner class ResultWindowReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val epc: String?
            if (intent.action == UGRApplication.UGR_ACTION_EPC) {
                epc = intent.extras!!.getString(UGRApplication.UGR_EXTRA_EPC_DATA)
                uhfTagArrayList!!.add(UHFTag(epc, 1))
                if (epc != null) {
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
                        mTagsCount!!.text = getString(R.string.tags_count, nSize)
                    }
                    adapter!!.notifyDataSetChanged()
                }
            } else if (intent.action == UGRApplication.UGR_ACTION_IS_READING) {
                mIsReading = intent.extras!!.getBoolean(UGRApplication.UGR_EXTRA_IS_READING)
                if (mIsReading) {
                    mBtnStart!!.text = "Stop"
                } else {
                    mBtnStart!!.text = "Start"
                }
            } else if (intent.action == UGRApplication.SCANNER_KEY_INTENT) {
                val nExtra = intent.getIntExtra(UGRApplication.SCANNER_KEY_EXTRA, 0)
                if (nExtra == 1) {
                    myBaseTime = SystemClock.elapsedRealtime()
                    // System.out.println(myBaseTime);
                    myTimer.sendEmptyMessage(0)
                } else {
                    myTimer.removeMessages(0)
                    myPauseTime = SystemClock.elapsedRealtime()
                    uhfTagArrayList!!.clear()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(resultReceiver)
        unregisterReceiver(mCodeReceiver)
        resultReceiver = null
        //RFIDEnable(false);
        val intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "trigger_mode")
        intent.putExtra("trigger_mode_value", 2)
        sendOrderedBroadcast(intent, null)
    }

    override fun onPause() {
        super.onPause()
        var intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "read_mode")
        intent.putExtra("read_mode_value", 2)
        sendOrderedBroadcast(intent, null)
        intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        intent.putExtra("setting", "trigger_mode")
        intent.putExtra("trigger_mode_value", 2)
        sendOrderedBroadcast(intent, null)
    }

    override fun onStop() {
        super.onStop()
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val isScreenOn = powerManager.isInteractive
            if (isScreenOn && !bNeedConnect) {
                Log.d(TAG, "isScreenOn && !bNeedConnect")
                RFIDEnable(false)
            }
        }
    }

    fun exportTxtFile(strFolderName: String, strFileName: String, strData: String?): Boolean {
        val folder = File(strFolderName)
        if (!folder.exists()) {
            try {
                val bMk = folder.mkdir()
                Log.d(TAG, "exportTxtFile: mkdir: $strFolderName : $bMk")
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
        Log.d(TAG, "exportTxtFile: $strFolderName$strFileName")
        val exportFile = File(strFolderName + strFileName)
        if (!exportFile.exists()) {
            try {
                exportFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }
        try {
            val buf = BufferedWriter(FileWriter(exportFile, true))
            buf.append(strData)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    var OnTriggerClickListener = View.OnClickListener { view ->
        val scannerIntent = Intent(UGRApplication.SCANNER_ACTION_SETTING_CHANGE)
        scannerIntent.putExtra("setting", "key_press")
        val rfidIntent = Intent(UGRApplication.SCANNER_KEY_ENABLE_INTENT)
        when (view.id) {
            R.id.radio_trigger_rfid -> {
                scannerIntent.putExtra("key_press_value", 0)
                rfidIntent.putExtra(UGRApplication.SCANNER_KEY_ENABLE_EXTRA, 1)
            }
            R.id.radio_trigger_scanner -> {
                scannerIntent.putExtra("key_press_value", 1)
                rfidIntent.putExtra(UGRApplication.SCANNER_KEY_ENABLE_EXTRA, 0)
            }
            R.id.radio_trigger_both -> {
                scannerIntent.putExtra("key_press_value", 1)
                rfidIntent.putExtra(UGRApplication.SCANNER_KEY_ENABLE_EXTRA, 1)
            }
        }
        sendBroadcast(scannerIntent, null)
        sendBroadcast(rfidIntent, null)
    }
    var OnTriggerClickListener2 = View.OnClickListener { view ->
        val triggerIntent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
        triggerIntent.putExtra("setting", "trigger_mode")
        when (view.id) {
            R.id.radio_trigger_rfid -> {
                triggerIntent.putExtra("trigger_mode_value", 0)
                mLastTriggerMode = 0
            }
            R.id.radio_trigger_scanner -> {
                triggerIntent.putExtra("trigger_mode_value", 1)
                mLastTriggerMode = 1
            }
            R.id.radio_trigger_both -> {
                triggerIntent.putExtra("trigger_mode_value", 2)
                mLastTriggerMode = 2
            }
        }
        sendBroadcast(triggerIntent, null)
    }

    inner class BarcodeReceiver : BroadcastReceiver() {
        private var barcode: String? = null
        private var type: String? = null
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == UGRApplication.SCANNER_ACTION_BARCODE) {
                barcode = intent.extras!!.getString("m3scannerdata")
                type = intent.extras!!.getString("m3scanner_code_type")
                if (barcode != null) {
                    mTvScannerResult!!.text = "Code : $barcode / Type : $type"
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_rfid, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mIsReading) {
            inventory(false)
        }
        val id = item.itemId
        Log.i(TAG, "Selected menu - $id")
        when (id) {
            R.id.action_menu_config -> {
                bNeedConnect = true
                startActivity(Intent(this, ConfigPreferenceActivity::class.java))
            }
            R.id.action_menu_access -> {
                bNeedConnect = true
                startActivity(Intent(this, AccessActivity::class.java))
            }
            R.id.action_menu_lock -> {
                bNeedConnect = true
                startActivity(Intent(this, LockActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    var myBaseTime: Long = 0
    var myPauseTime: Long = 0
    private fun inventory(bStart: Boolean) {
        val intent: Intent
        if (bStart) {
            intent = Intent(UGRApplication.UGR_ACTION_START, null)
            myBaseTime = SystemClock.elapsedRealtime()
            // System.out.println(myBaseTime);
            myTimer.sendEmptyMessage(0)
        } else {
            intent = Intent(UGRApplication.UGR_ACTION_CANCEL, null)
            myTimer.removeMessages(0)
            myPauseTime = SystemClock.elapsedRealtime()
            uhfTagArrayList!!.clear()
        }
        sendOrderedBroadcast(intent, null)
    }

    private fun RFIDEnable(bOn: Boolean) {
        Log.d(TAG, "RFIDEnable")
        val nExtra: Int
        nExtra = if (bOn) 1 else 0
        val intent = Intent(UGRApplication.UGR_ACTION_ENABLE, null)
        intent.putExtra(UGRApplication.UGR_EXTRA_ENABLE, nExtra)
        intent.putExtra("module_reset", false)
        sendOrderedBroadcast(intent, null)
    }

    @SuppressLint("HandlerLeak")
    var myTimer: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            mInventoryTime!!.text = timeOut
            sendEmptyMessage(0)
        }
    }
    val timeOut: String
        get() {
            val now = SystemClock.elapsedRealtime()
            val outTime = now - myBaseTime
            @SuppressLint("DefaultLocale") val easy_outTime = String.format(
                "%02d:%02d:%02d",
                outTime / 1000 / 60,
                outTime / 1000 % 60,
                outTime % 1000 / 10
            )
            var totalDiff: Long = 0
            var rps: Long = 0
            for (i in 1 until uhfTagArrayList!!.size) {
                uhfTagArrayList!![i].diff =
                    uhfTagArrayList!![i].time - uhfTagArrayList!![i - 1].time
                totalDiff += uhfTagArrayList!![i].diff
            }
            try {
                if (uhfTagArrayList!!.size > 2) {
                    rps = 1000 / (totalDiff / (uhfTagArrayList!!.size - 1))
                }
            } catch (e: Exception) {
            }
            return easy_outTime + " " + rps + "rps"
        }

    companion object {
        @JvmField
        var bNeedConnect = false
    }
}