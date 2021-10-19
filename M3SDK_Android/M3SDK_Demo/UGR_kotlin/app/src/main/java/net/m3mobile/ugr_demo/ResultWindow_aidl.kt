package net.m3mobile.ugr_demo

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.*
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.view.*
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
    private var mBtnStart: Button? = null
    private var mTagsCount: TextView? = null
    private var mInventoryTime: TextView? = null
    private var mTvScannerResult: TextView? = null
    private var mIsReading = false
    private var mTAGs: ArrayList<HashMap<String, UHFTag>>? = null
    private var adapter: TagAdapter? = null
    private var resultReceiver: ResultWindowReceiver? = null
    private var mCodeReceiver: BarcodeReceiver? = null
    private var mLastTriggerMode = 2
    private var UHFTagArrayList: ArrayList<UHFTag>? = null
    private var m_remoteSvc: IUGRTestService? = null
    private var m_remoteCallback: IUHFServiceCallback? = null
    private var m_UHFSvcConnection: ServiceConnection? = null
    private var bEnable = false
    private var isServiceConnect = false
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogWriter.i("onCreate++")
        setContentView(R.layout.result_window)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mainScreen()
        resultReceiver = ResultWindowReceiver()
        val filter = IntentFilter()
        filter.addAction(UGRApplication.SCANNER_KEY_INTENT)
        mCodeReceiver = BarcodeReceiver()
        val mBarcodeFilter = IntentFilter()
        mBarcodeFilter.addAction(UGRApplication.SCANNER_ACTION_BARCODE)
        registerReceiver(resultReceiver, filter)
        registerReceiver(mCodeReceiver, mBarcodeFilter)
        progressDialog = ProgressDialog(this)
        progressDialog!!.show()
        registerConnection()
        registerCallBack()
        val intent = Intent("net.m3mobile.ugremul.start")
        intent.setPackage("net.m3mobile.ugremul")
        m_UHFSvcConnection?.let { bindService(intent, it, Context.BIND_AUTO_CREATE) }
    }

    protected fun mainScreen() {
        val mListInventory: ListView = findViewById<ListView>(R.id.listView_Inventory)
        mBtnStart = findViewById<Button>(R.id.btnStart)
        val mBtnClear: Button = findViewById<Button>(R.id.btnClear)
        val mBtnExport: Button = findViewById<Button>(R.id.btnExport)
        mTagsCount = findViewById<TextView>(R.id.textView_count)
        mTagsCount?.setText("TAGS Count\n0")
        mInventoryTime = findViewById<TextView>(R.id.textView_time)
        mTvScannerResult = findViewById<TextView>(R.id.scanresult_intent)
        val triggerGroup: RadioGroup = findViewById<RadioGroup>(R.id.radio_trigger_mode)
        val triggerRFID: RadioButton = findViewById<RadioButton>(R.id.radio_trigger_rfid)
        val triggerScanner: RadioButton = findViewById<RadioButton>(R.id.radio_trigger_scanner)
        val triggerBoth: RadioButton = findViewById<RadioButton>(R.id.radio_trigger_both)
        triggerRFID.setOnClickListener(OnTriggerClickListener2)
        triggerScanner.setOnClickListener(OnTriggerClickListener2)
        triggerBoth.setOnClickListener(OnTriggerClickListener2)
        triggerBoth.isChecked = true
        mTAGs = ArrayList()
        UHFTagArrayList = ArrayList()
        adapter = TagAdapter(this, mTAGs!!, R.layout.listview_item_row, null, null)
        mListInventory.transcriptMode = AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL
        mListInventory.adapter = adapter
        mBtnStart!!.setOnClickListener { view: View? ->
            LogWriter.i("onClick++ $mIsReading")
            inventory(!mIsReading)
        }
        mBtnClear.setOnClickListener { view: View? ->
            mTAGs!!.clear()
            adapter!!.notifyDataSetChanged()
            mTagsCount?.setText("TAGS Count\n0")
            UHFTagArrayList!!.clear()
        }
        mBtnExport.setOnClickListener { view: View? ->
            val time = System.currentTimeMillis()
            @SuppressLint("SimpleDateFormat") val dayTime = SimpleDateFormat("yyyyMMdd_hhmmss")
            val strFolderName = Environment.getExternalStorageDirectory().path +
                    "/android/data/net.m3mobile.ugremul"
            val strFileName = "/export_" + dayTime.format(Date(time)) + ".txt"
            for (i in mTAGs!!.indices) {
                val tm = mTAGs!![i]
                if (tm != null) {
                    val epc = tm.values.toTypedArray()[0]
                    val strTAG = epc.TIME + "    " + epc.EPC
                    if (!exportTxtFile(strFolderName, strFileName, strTAG)) {
                        Toast.makeText(
                            this@ResultWindow_aidl,
                            "Export data Failed!!",
                            Toast.LENGTH_LONG
                        ).show()
                        return@setOnClickListener
                    }
                }
            }
            Toast.makeText(
                this@ResultWindow_aidl,
                "Export data Success!! on '$strFolderName$strFileName'",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    protected override fun onResume() {
        super.onResume()
        LogWriter.i("onResume isServiceConnect:$isServiceConnect")
        if (!isServiceConnect) return
        try {
            m_remoteSvc?.setReadMode(0)
            m_remoteSvc?.setTrigger(mLastTriggerMode)
            m_remoteSvc?.setEndChar(6)
            m_remoteSvc?.setOutputMode(2)
            m_remoteSvc?.updateAllOption()
            if (!bNeedConnect) m_remoteSvc?.setEnable(1, false)
        } catch (e: RemoteException) {
            LogWriter.e("RemoteException : " + e.message)
        }
    }

    inner class ResultWindowReceiver : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            val epc: String
            when (intent.getAction()) {
                UGRApplication.UGR_ACTION_EPC -> {
                    epc = intent.getExtras()!!.getString(UGRApplication.UGR_EXTRA_EPC_DATA).toString()
                    UHFTagArrayList!!.add(UHFTag(epc, 1))
                    if (epc != null) {
                        var existTag = false
                        val hashMap = HashMap<String, UHFTag>()
                        hashMap[epc] = UHFTag(epc, 1)
                        var i = 0
                        while (i < mTAGs!!.size) {
                            val tm = mTAGs!![i]
                            if (tm != null) {
                                if (tm.containsKey(epc)) {
                                    Objects.requireNonNull(tm[epc])!!.Reads++
                                    existTag = true
                                    break
                                }
                            }
                            i++
                        }
                        if (!existTag) {
                            mTAGs!!.add(hashMap)
                            val nSize = mTAGs!!.size
                            mTagsCount?.setText(getString(R.string.tags_count, nSize))
                        }
                        adapter!!.notifyDataSetChanged()
                    }
                }
                UGRApplication.UGR_ACTION_IS_READING -> {
                    mIsReading = intent.getExtras()!!.getBoolean(UGRApplication.UGR_EXTRA_IS_READING)
                    LogWriter.i("onReceive action:" + intent.getAction() + " mIsReading:" + mIsReading)
                    if (mIsReading) mBtnStart!!.text = "Stop" else mBtnStart!!.text = "Start"
                }
                UGRApplication.SCANNER_KEY_INTENT -> {
                    val nExtra: Int = intent.getIntExtra(UGRApplication.SCANNER_KEY_EXTRA, 0)
                    if (nExtra == 1) {
                        myBaseTime = SystemClock.elapsedRealtime()
                        // System.out.println(myBaseTime);
                        myTimer.sendEmptyMessage(0)
                    } else {
                        myTimer.removeMessages(0)
                        myPauseTime = SystemClock.elapsedRealtime()
                        UHFTagArrayList!!.clear()
                    }
                }
            }
        }
    }

    protected override fun onDestroy() {
        super.onDestroy()
        LogWriter.i("onDestroy++")
        unregisterReceiver(resultReceiver)
        unregisterReceiver(mCodeReceiver)
        resultReceiver = null
        try {
            m_remoteSvc?.setTrigger(2)
        } catch (e: RemoteException) {
            LogWriter.e("onDestroy remoteException : " + e.message)
        }
        m_UHFSvcConnection?.let { unbindService(it) }
    }

    protected override fun onPause() {
        super.onPause()
        LogWriter.i("onPause++ mIsReading:$mIsReading")
        if (mIsReading) inventory(false)
        try {
            m_remoteSvc?.setReadMode(2)
            m_remoteSvc?.setTrigger(2)
            m_remoteSvc?.setIntentEnable(true)
            m_remoteSvc?.updateAllOption()
        } catch (e: RemoteException) {
            LogWriter.e("onPause remoteException : " + e.message)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    protected override fun onStop() {
        super.onStop()
        LogWriter.i("onStop++ ")
        val powerManager: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val isScreenOn: Boolean = powerManager.isInteractive()
            if (isScreenOn && !bNeedConnect) {
                LogWriter.d("isScreenOn && !bNeedConnect")
                try {
                    m_remoteSvc?.setEnable(0, false)
                } catch (e: RemoteException) {
                    LogWriter.e("onStop remoteException : " + e.message)
                }
            }
        }
    }

    private fun exportTxtFile(
        strFolderName: String,
        strFileName: String,
        strData: String
    ): Boolean {
        val folder = File(strFolderName)
        if (!folder.exists()) {
            try {
                val bMk = folder.mkdir()
                LogWriter.d("exportTxtFile: mkdir: $strFolderName : $bMk")
            } catch (e: Exception) {
                LogWriter.e("exportTxtFile Exception : " + e.message)
                return false
            }
        }
        LogWriter.d("exportTxtFile: $strFolderName$strFileName")
        val exportFile = File(strFolderName + strFileName)
        if (!exportFile.exists()) {
            try {
                exportFile.createNewFile()
            } catch (e: IOException) {
                LogWriter.e("exportTxtFile Exception : " + e.message)
                return false
            }
        }
        try {
            val buf = BufferedWriter(FileWriter(exportFile, true))
            buf.append(strData)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            LogWriter.e("exportTxtFile Exception : " + e.message)
            return false
        }
        return true
    }

    @SuppressLint("NonConstantResourceId")
    private val OnTriggerClickListener2 = View.OnClickListener { view: View ->
        when (view.id) {
            R.id.radio_trigger_rfid -> mLastTriggerMode = 0
            R.id.radio_trigger_scanner -> mLastTriggerMode = 1
            R.id.radio_trigger_both -> mLastTriggerMode = 2
        }
        try {
            m_remoteSvc?.setTrigger(mLastTriggerMode)
            m_remoteSvc?.updateAllOption()
        } catch (e: RemoteException) {
            LogWriter.e("onClick remoteException : " + e.message)
        }
    }

    private inner class BarcodeReceiver : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getAction() == UGRApplication.SCANNER_ACTION_BARCODE) {
                val barcode: String? =
                    intent.getExtras()?.getString("m3scannerdata")
                val type: String? =
                    intent.getExtras()?.getString("m3scannerdata")
                if (barcode != null) mTvScannerResult?.setText("Code : $barcode / Type : $type")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_rfid, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mIsReading) inventory(false)
        val id = item.itemId
        LogWriter.i("Selected menu - $id")
        when (id) {
            R.id.action_menu_config -> {
                if (!bEnable) return super.onOptionsItemSelected(item)
                bNeedConnect = true
                startActivity(Intent(this, ConfigPreferenceActivity::class.java))
            }
            R.id.action_menu_access -> {
                if (!bEnable) return super.onOptionsItemSelected(item)
                bNeedConnect = true
                startActivity(Intent(this, AccessActivity::class.java))
            }
            R.id.action_menu_lock -> {
                if (!bEnable) return super.onOptionsItemSelected(item)
                bNeedConnect = true
                startActivity(Intent(this, LockActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private var myBaseTime: Long = 0
    private var myPauseTime: Long = 0
    private fun inventory(bStart: Boolean) {
        try {
            LogWriter.i("inventory++ bStart:$bStart bEnable: $bEnable mIsReading: $mIsReading")
            if (!bEnable) return
            m_remoteSvc?.Inventory(bStart)
            if (bStart) {
                myBaseTime = SystemClock.elapsedRealtime()
                // System.out.println(myBaseTime);
                myTimer.sendEmptyMessage(0)
            } else {
                myTimer.removeMessages(0)
                myPauseTime = SystemClock.elapsedRealtime()
                UHFTagArrayList!!.clear()
            }
        } catch (e: RemoteException) {
            LogWriter.e("inventory RemoteException : " + e.message)
        }
    }

    @SuppressLint("HandlerLeak")
    private val myTimer: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            runOnUiThread(Runnable { mInventoryTime?.setText(timeOut) })
            sendEmptyMessage(0)
        }
    }
    private val timeOut: String
        private get() {
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
            for (i in 1 until UHFTagArrayList!!.size) {
                UHFTagArrayList!![i].diff =
                    UHFTagArrayList!![i].time - UHFTagArrayList!![i - 1].time
                totalDiff += UHFTagArrayList!![i].diff
            }
            try {
                if (UHFTagArrayList!!.size > 2) rps =
                    1000 / (totalDiff / (UHFTagArrayList!!.size - 1))
            } catch (ignored: Exception) {
            }
            return easy_outTime + " " + rps + "rps"
        }
    private var progressDialog: ProgressDialog? = null
    private fun registerConnection() {
        if (m_UHFSvcConnection == null) {
            m_UHFSvcConnection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    m_remoteSvc = IUGRTestService.Stub.asInterface(service)
                    LogWriter.d("Service is Connected")
                    try {
                        if (m_remoteSvc?.registerUHFServiceCallback(m_remoteCallback)!!) LogWriter.d("Callback was registered") else LogWriter.d(
                            "Registering Callback was failed"
                        )
                        progressDialog!!.setMessage("Service is connected. start enable...")
                        onServiceConnectedHandler.removeCallbacks(onServiceConnectedRunnable)
                        onServiceConnectedHandler.postDelayed(
                            onServiceConnectedRunnable,
                            (1000 * 5).toLong()
                        )
                    } catch (e: RemoteException) {
                        LogWriter.e("onServiceConnected remoteException : " + e.message)
                    }
                    isServiceConnect = true
                }

                private val onServiceConnectedHandler: Handler = Handler(getMainLooper())
                private val onServiceConnectedRunnable = Runnable {
                    try {
                        m_remoteSvc?.setEnable(1, false)
                        m_remoteSvc?.setReadMode(0)
                        m_remoteSvc?.setTrigger(mLastTriggerMode)
                        m_remoteSvc?.setEndChar(6)
                        m_remoteSvc?.setOutputMode(2)
                        m_remoteSvc?.setIntentEnable(false)
                        m_remoteSvc?.updateAllOption()
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    LogWriter.d("Service is Disconnected")
                    m_remoteSvc = null
                    isServiceConnect = false
                }
            }
        }
    }

    private fun registerCallBack() {
        m_remoteCallback = object : IUHFServiceCallback.Stub() {
            override fun onInventory(epc: String?) {
                runOnUiThread(Runnable {
                    UHFTagArrayList!!.add(UHFTag(epc, 1))
                    if (epc != null) {
                        var existTag = false
                        val hashMap = HashMap<String, UHFTag>()
                        hashMap[epc] = UHFTag(epc, 1)
                        for (i in mTAGs!!.indices) {
                            val tm = mTAGs!![i]
                            if (tm != null) {
                                if (tm.containsKey(epc)) {
                                    Objects.requireNonNull(tm[epc])!!.Reads++
                                    existTag = true
                                    break
                                }
                            }
                        }
                        if (!existTag) {
                            mTAGs!!.add(hashMap)
                            val nSize = mTAGs!!.size
                            mTagsCount!!.setText(getString(R.string.tags_count, nSize))
                        }
                        adapter!!.notifyDataSetChanged()
                    }
                })
            }

            @SuppressLint("SetTextI18n")
            override fun onIsReading(isReading: Boolean) {
                // Log.i(TAG, "onIsReading isReading:" + isReading);
                mIsReading = isReading
                onIsReadingHandler.removeCallbacks(onIsReadingRunnable)
                onIsReadingHandler.post(onIsReadingRunnable)
            }

            private val onIsReadingHandler: Handler = Handler(Looper.getMainLooper())
            private val onIsReadingRunnable = Runnable {
                if (mIsReading) mBtnStart!!.text = "Stop" else {
                    mBtnStart!!.text = "Start"
                    // 전재영, Screen on 후 RFID 전원 켜진 동시에 Start를 누르면, Isreading 과 겹쳐서 시간만 가고 Start로 표기되는 상황이 발생한다. 그래서 추가함
                    myTimer.removeMessages(0)
                    myPauseTime = SystemClock.elapsedRealtime()
                    UHFTagArrayList!!.clear()
                }
            }

            override fun onEnable(isEnable: Boolean) {
                bEnable = isEnable
                if (!isEnable) inventory(false) else progressDialog!!.dismiss()
            }

            override fun onInventoryRssi(epc: String?, nb_rssi: Double, wb_rssi: Double) {}
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode == KeyEvent.KEYCODE_ENTER) true else super.dispatchKeyEvent(
            event
        )
    }

    companion object {
        var bNeedConnect = false
    }
}