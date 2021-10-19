package net.m3mobile.ugr_demo

import android.annotation.SuppressLint
import android.content.*
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import net.m3mobile.ugremul.IUGRTestService
import net.m3mobile.ugremul.IUHFServiceCallback
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
class ResultWindow_aidl : AppCompatActivity() {
    private val TAG = "ResultWindow_aidl"
    var mListInventory: ListView? = null
    var mBtnStart: Button? = null
    var mBtnClear: Button? = null
    var mBtnExport: Button? = null
    var mTagsCount: TextView? = null
    var mInventoryTime: TextView? = null
    var mTvScannerResult: TextView? = null
    var mIsReading = false
    private var mTAGs: ArrayList<HashMap<String?, UhfTag>>? = null
    private var adapter: TagAdapter? = null
    private var resultReceiver: ResultWindowReceiver? = null
    private var mCodeReceiver: BarcodeReceiver? = null
    private var mBarcodeFilter: IntentFilter? = null
    var triggerGroup: RadioGroup? = null
    var mLastTriggerMode = 2
    private var uhfTagArrayList: ArrayList<UhfTag>? = null
    private var m_remoteSvc: IUGRTestService? = null
    private var m_remoteCallback: IUHFServiceCallback? = null
    private var m_UHFSvcConnection: ServiceConnection? = null
    private var bEnable = false
    private var isServiceConnect = false
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_window)
        mainScreen()
        resultReceiver = ResultWindowReceiver()
        val filter = IntentFilter()
        filter.addAction(UGRApplication.SCANNER_KEY_INTENT)
        mCodeReceiver = BarcodeReceiver()
        mBarcodeFilter = IntentFilter()
        mBarcodeFilter!!.addAction(UGRApplication.SCANNER_ACTION_BARCODE)
        registerReceiver(resultReceiver, filter)
        registerReceiver(mCodeReceiver, mBarcodeFilter)
        registerConnection()
        registerCallBack()
        val intent = Intent("net.m3mobile.ugremul.start")
        intent.setPackage("net.m3mobile.ugremul")
        m_UHFSvcConnection?.let { bindService(intent, it, Context.BIND_AUTO_CREATE) }
    }

    protected fun mainScreen() {
        mListInventory = findViewById<View>(R.id.listView_Inventory) as ListView?
        mBtnStart = findViewById<View>(R.id.btnStart) as Button?
        mBtnClear = findViewById<View>(R.id.btnClear) as Button?
        mBtnExport = findViewById<View>(R.id.btnExport) as Button?
        mTagsCount = findViewById<View>(R.id.textView_count) as TextView?
        mTagsCount?.setText("TAGS Count\n0")
        mInventoryTime = findViewById<View>(R.id.textView_time) as TextView?
        mTvScannerResult = findViewById<View>(R.id.scanresult_intent) as TextView?
        triggerGroup = findViewById<View>(R.id.radio_trigger_mode) as RadioGroup?
        val triggerRFID = findViewById<View>(R.id.radio_trigger_rfid) as RadioButton
        val triggerScanner = findViewById<View>(R.id.radio_trigger_scanner) as RadioButton
        val triggerBoth = findViewById<View>(R.id.radio_trigger_both) as RadioButton
        triggerRFID.setOnClickListener(OnTriggerClickListener2)
        triggerScanner.setOnClickListener(OnTriggerClickListener2)
        triggerBoth.setOnClickListener(OnTriggerClickListener2)
        triggerBoth.isChecked = true
        mTAGs = ArrayList()
        uhfTagArrayList = ArrayList()
        adapter = TagAdapter(this, mTAGs, R.layout.listview_item_row, null, null)
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
            mTagsCount?.setText("TAGS Count\n0")
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
                        Toast.makeText(this@ResultWindow_aidl, "Export data Failed!!", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }
                }
            }
            Toast.makeText(this@ResultWindow_aidl, "Export data Success!! on '$strFolderName$strFileName'", Toast.LENGTH_LONG).show()
        })
    }

    protected override fun onResume() {
        super.onResume()
        if (!isServiceConnect) return
        try {
            m_remoteSvc?.setReadMode(0)
            m_remoteSvc?.setTrigger(mLastTriggerMode)
            m_remoteSvc?.setEndChar(6)
            m_remoteSvc?.setOutputMode(2)
            if (!bNeedConnect) m_remoteSvc?.setEnable(1, false)
        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException : " + e.message)
        }
    }

    inner class ResultWindowReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val epc: String
            if (intent.getAction() == UGRApplication.UGR_ACTION_EPC) {
                epc = intent.getExtras()?.getString(UGRApplication.UGR_EXTRA_EPC_DATA).toString()
                uhfTagArrayList!!.add(UhfTag(epc, 1))
                if (epc != null) {
                    var existTag = false
                    val hashMap = HashMap<String?, UhfTag>()
                    hashMap[epc] = UhfTag(epc, 1)
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
                        mTagsCount?.setText(getString(R.string.tags_count, nSize))
                    }
                    adapter!!.notifyDataSetChanged()
                }
            } else if (intent.getAction() == UGRApplication.UGR_ACTION_IS_READING) {
                mIsReading = (intent.getExtras()?.getBoolean(UGRApplication.UGR_EXTRA_IS_READING) ?: if (mIsReading) {
                    mBtnStart!!.text = "Stop"
                } else {
                    mBtnStart!!.text = "Start"
                }) as Boolean
            } else if (intent.getAction() == UGRApplication.SCANNER_KEY_INTENT) {
                val nExtra: Int = intent.getIntExtra(UGRApplication.SCANNER_KEY_EXTRA, 0)
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

    protected override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(resultReceiver)
        unregisterReceiver(mCodeReceiver)
        resultReceiver = null
        try {
            m_remoteSvc?.setTrigger(2)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        m_UHFSvcConnection?.let { unbindService(it) }
    }

    protected override fun onPause() {
        super.onPause()
        if (mIsReading) inventory(false)
        try {
            m_remoteSvc?.setReadMode(2)
            m_remoteSvc?.setTrigger(2)
            m_remoteSvc?.setIntentEnable(true)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    protected override fun onStop() {
        super.onStop()
        val powerManager: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val isScreenOn: Boolean = powerManager.isInteractive()
            if (isScreenOn && !bNeedConnect) {
                Log.d(TAG, "isScreenOn && !bNeedConnect")
                try {
                    m_remoteSvc?.setEnable(0, false)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
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

    var OnTriggerClickListener2 = View.OnClickListener { view ->
        when (view.id) {
            R.id.radio_trigger_rfid -> mLastTriggerMode = 0
            R.id.radio_trigger_scanner -> mLastTriggerMode = 1
            R.id.radio_trigger_both -> mLastTriggerMode = 2
        }
        try {
            m_remoteSvc?.setTrigger(mLastTriggerMode)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    inner class BarcodeReceiver : BroadcastReceiver() {
        private var barcode: String? = null
        private var type: String? = null
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getAction() == UGRApplication.SCANNER_ACTION_BARCODE) {
                barcode = intent.getExtras()?.getString("m3scannerdata")
                type = intent.getExtras()?.getString("m3scanner_code_type")
                if (barcode != null) {
                    mTvScannerResult?.setText("Code : $barcode / Type : $type")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_rfid, menu)
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
        try {
            if (!bEnable) return
            m_remoteSvc?.Inventory(bStart)
            if (bStart) {
                myBaseTime = SystemClock.elapsedRealtime()
                // System.out.println(myBaseTime);
                myTimer.sendEmptyMessage(0)
            } else {
                myTimer.removeMessages(0)
                myPauseTime = SystemClock.elapsedRealtime()
                uhfTagArrayList!!.clear()
            }
        } catch (e: RemoteException) {
            Log.e(TAG, "inventory RemoteException : " + e.message)
        }
    }

    @SuppressLint("HandlerLeak")
    var myTimer: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            mInventoryTime?.setText(timeOut)
            sendEmptyMessage(0)
        }
    }
    val timeOut: String
        get() {
            val now = SystemClock.elapsedRealtime()
            val outTime = now - myBaseTime
            @SuppressLint("DefaultLocale") val easy_outTime = String.format("%02d:%02d:%02d", outTime / 1000 / 60, outTime / 1000 % 60, outTime % 1000 / 10)
            var totalDiff: Long = 0
            var rps: Long = 0
            for (i in 1 until uhfTagArrayList!!.size) {
                uhfTagArrayList!![i].diff = uhfTagArrayList!![i].time - uhfTagArrayList!![i - 1].time
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

    private fun registerConnection() {
        if (m_UHFSvcConnection == null) {
            m_UHFSvcConnection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    m_remoteSvc = IUGRTestService.Stub.asInterface(service)
                    Log.d(TAG, "Service is Connected")
                    try {
                        if (m_remoteSvc?.registerUHFServiceCallback(m_remoteCallback)!!)
                            Log.d(TAG, "Callback was registered")
                        else
                            Log.d(TAG, "Registering Callback was failed")
                        m_remoteSvc!!.setEnable(1, false)
                        m_remoteSvc!!.setReadMode(0)
                        m_remoteSvc!!.setTrigger(mLastTriggerMode)
                        m_remoteSvc!!.setEndChar(6)
                        m_remoteSvc!!.setOutputMode(2)
                        m_remoteSvc!!.setIntentEnable(false)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                    isServiceConnect = true
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    Log.d(TAG, "Service is Disconnected")
                    m_remoteSvc = null
                    isServiceConnect = false
                }
            }
        }
    }

    private fun registerCallBack() {
        m_remoteCallback = object : IUHFServiceCallback.Stub() {
            @Throws(RemoteException::class)
            override fun onInventory(epc: String?) {
                uhfTagArrayList!!.add(UhfTag(epc, 1))
                if (epc != null) {
                    var existTag = false
                    val hashMap = HashMap<String?, UhfTag>()
                    hashMap[epc] = UhfTag(epc, 1)
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

                        // mTagsCount.setText(getString(R.string.tags_count, nSize));
                        val message = Message.obtain(handler, MSG_UHF_LIST_COUNT, nSize)
                        handler.sendMessage(message)
                    }

                    // adapter.notifyDataSetChanged();
                    handler.sendMessage(handler.obtainMessage(MSG_UHF_LIST_CHANGED))
                }
            }

            @Throws(RemoteException::class)
            override fun onIsReading(isReading: Boolean) {
                mIsReading = isReading
                if (mIsReading) {
                    // mBtnStart.setText("Stop");
                    handler.sendMessage(handler.obtainMessage(MSG_UHF_BUTTON_STOP))
                } else {
                    // mBtnStart.setText("Start");
                    handler.sendMessage(handler.obtainMessage(MSG_UHF_BUTTON_START))
                }
            }

            @Throws(RemoteException::class)
            override fun onEnable(isEnable: Boolean) {
                bEnable = isEnable
                if (!isEnable) inventory(false)
            }

            @Throws(RemoteException::class)
            override fun onInventoryRssi(epc: String?, nb_rssi: Double, wb_rssi: Double) {
                Log.d(TAG, "" + nb_rssi)
            }
        }
    }

    private val MSG_UHF_LIST_CHANGED = 10
    private val MSG_UHF_LIST_COUNT = 20
    private val MSG_UHF_BUTTON_STOP = 100
    private val MSG_UHF_BUTTON_START = 200
    private val handler: Handler = object : Handler() {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_UHF_LIST_CHANGED -> adapter!!.notifyDataSetChanged()
                MSG_UHF_LIST_COUNT -> mTagsCount?.setText("TAGS Count\\n" +  msg.arg1)
                MSG_UHF_BUTTON_STOP -> mBtnStart!!.text = "Stop"
                MSG_UHF_BUTTON_START -> mBtnStart!!.text = "Start"
            }
        }
    }

    companion object {
        var bNeedConnect = false
    }
}