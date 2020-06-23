package com.m3mobile.app.sdk_demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.SettingsSlicesContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton

import android.view.View.OnClickListener
import android.widget.EditText
import kotlinx.android.synthetic.main.scanner_intent.*

import com.m3.sdk.scannerlib.Barcode
import com.m3.sdk.scannerlib.BarcodeListener2
import com.m3.sdk.scanner.ScanData
import com.m3.sdk.scannerlib.DataType


class ScannerActivity : Activity(), BarcodeListener2, View.OnClickListener {

    private val TAG : String = "ScannerActivity"

    private lateinit var _barcode : Barcode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanner_intent)

        mainScreen()

        _barcode = Barcode(this.applicationContext)
        _barcode.addBarcodeListener(this)
    }


    override fun onScannerInitialized() {
        registListener()
    }

    private fun mainScreen()
    {
        startread_intent.setOnClickListener(this)
        stopread_intent.setOnClickListener(this)
        enable_intent.setOnClickListener(this)
        disable_intent.setOnClickListener(this)

        buttonGet_intent.setOnClickListener{ getParam() }
        buttonSet_intent.setOnClickListener{ setParam() }
    }

    override fun onDestroy() {
        _barcode.removeBarcodeListener(this)
        _barcode.dispose()
        super.onDestroy()
    }


    private fun getParam() {
        val s: String = editPnum_intent.text.toString()
        val nValue: Int = _barcode.getScanParameter(s.toInt())
        editPval_intent.setText("$nValue")
    }

    // ----------------------------------------
    private fun setParam() { // get param #
        val sn: String = editPnum_intent.text.toString()
        val sv: String = editPval_intent.text.toString()
        val num = sn.toInt()
        val value = sv.toInt()

        _barcode.setScanParameter(num, value)
    }

    override fun onBarcode(scanned : ScanData) {
        Log.i(TAG, "onBarcode: barcode: ${scanned.barcode}, codeType: ${scanned.codeType}, rawSize:${scanned.barcodeRawData.size}")
        scanresult_intent.setText("barcode: ${scanned.barcode}, codeType: ${scanned.codeType}")
    }

    override fun onClick(view: View?) {
        when(view)
        {
            startread_intent -> {
                _barcode.scanStart()
            }
            stopread_intent -> {
                _barcode.scanDispose()
            }
            enable_intent -> {
                _barcode.setScanner(true)
            }
            disable_intent -> {
                _barcode.setScanner(false)
            }
        }
    }


    private fun registListener(){

        // basic func
        startread_intent.setOnClickListener(onButtonClickListener)
        stopread_intent.setOnClickListener(onButtonClickListener)
        enable_intent.setOnClickListener(onButtonClickListener)
        disable_intent.setOnClickListener(onButtonClickListener)
        btn_is_enable.setOnClickListener(onButtonClickListener)

        // set param
        buttonGet_intent.setOnClickListener(onButtonClickListener)
        buttonSet_intent.setOnClickListener(onButtonClickListener)

        // sound
        sound_none.setOnClickListener(onSoundClickListener)
        sound_beep.setOnClickListener(onSoundClickListener)
        sound_dingdong.setOnClickListener(onSoundClickListener)
        sound_beep.isChecked = true


        // vibration
        vibration_mode.setOnCheckedChangeListener(onVibrationCheckListener)
        vibration_mode.isChecked = true

        // scanner key
        key_all_disable.setOnClickListener(onKeyClickListener)
        key_enable.setOnClickListener(onKeyClickListener)
        key_only_sw_enable.setOnClickListener(onKeyClickListener)
        key_enable.isChecked = true

        // read mode
        read_mode_async.setOnClickListener(onReadModeClick)
        read_mode_sync.setOnClickListener(onReadModeClick)
        read_mode_continue.setOnClickListener(onReadModeClick)
        read_mode_multiple.setOnClickListener(onReadModeClick)
        read_mode_async.isChecked = true
        editMultipleCount_intent.addTextChangedListener(onMultiTextChangedListener)

        // output mode
        output_mode_copyandpaste.setOnClickListener(onOutputModeListener)
        output_mode_key.setOnClickListener(onOutputModeListener)
        output_mode_none.setOnClickListener(onOutputModeListener)
        output_mode_key.isChecked = true

        // end
        end_enter.setOnClickListener(onEndModeListener)
        end_space.setOnClickListener(onEndModeListener)
        end_tab.setOnClickListener(onEndModeListener)
        end_none.setOnClickListener(onEndModeListener)
        end_key_enter.setOnClickListener(onEndModeListener)
        end_key_space.setOnClickListener(onEndModeListener)
        end_key_tab.setOnClickListener(onEndModeListener)
        end_enter.isChecked = true

        // fix
        buttonSet_fix.setOnClickListener(onFixClickListener)

        // codeType all
        button_disable_all.setOnClickListener(onAllCodeClickListener)
        button_enable_all.setOnClickListener(onAllCodeClickListener)

    }


    private var onButtonClickListener = OnClickListener { view ->
        when (view) {
            // basic fun
            startread_intent -> {
                _barcode.scanStart()
            }
            stopread_intent -> {
                _barcode.scanDispose()
            }
            enable_intent -> {
                _barcode.setScanner(true)
            }
            disable_intent -> {
                _barcode.setScanner(false)
            }

            btn_is_enable -> {
                var bEnable = _barcode.isEnable

                scanresult_intent.text = "Enable: $bEnable"
            }

            // param fun
            else -> {
                var num = -1
                var value = -1

                when (view) {
                    // press get button
                    buttonGet_intent -> {

                        try {
                            num = Integer.parseInt(editPnum_intent.text.toString())
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        value = _barcode.getScanParameter(num)

                        editPval_intent.setText(value.toString())
                    }
                    // press set button
                    buttonSet_intent -> {

                        try {
                            num = Integer.parseInt(editPnum_intent.text.toString())
                            value = Integer.parseInt(editPval_intent.text.toString())
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                        var nResult = _barcode.setScanParameter(num, value)

                        if(nResult == -1){
                            scanresult_intent.text = "Failed setParam"
                        }
                    }
                }
            }
        }
    }

    var onSoundClickListener = OnClickListener { view ->
        var sound  = DataType.SOUND_MODE.BEEP
        when (view) {
            sound_none -> sound = DataType.SOUND_MODE.NONE
            sound_beep -> sound = DataType.SOUND_MODE.BEEP
            sound_dingdong -> sound = DataType.SOUND_MODE.DING_DONG
        }
        _barcode.setSoundMode(sound)
    }

    var onVibrationCheckListener = CompoundButton.OnCheckedChangeListener { button, isChecked ->
        _barcode.setVibration(isChecked)
    }

    var onKeyClickListener = OnClickListener { view ->

        var mode = DataType.SCAN_TRIGGER.ALL_ENABLE

        when (view) {
            key_enable -> mode = DataType.SCAN_TRIGGER.ALL_ENABLE
            key_all_disable -> mode = DataType.SCAN_TRIGGER.ALL_DISABLE
            key_only_sw_enable -> mode = DataType.SCAN_TRIGGER.ONLY_SW_TRIGGER
        }
        _barcode.setScannerTriggerMode(mode)
    }

    var onReadModeClick = OnClickListener { view ->
        var readMode = DataType.READ_MODE.ASYNC
        when (view) {
            read_mode_async -> readMode = DataType.READ_MODE.ASYNC
            read_mode_sync -> readMode = DataType.READ_MODE.SYNC
            read_mode_continue -> readMode = DataType.READ_MODE.CONTINUE
            read_mode_multiple -> readMode = DataType.READ_MODE.MULTIPLE
        }
        _barcode.setReadMode(readMode)
        var value = 2
        try {
            value = Integer.parseInt(editMultipleCount_intent.text.toString())
            _barcode.setMultipleCount(value)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    val onMultiTextChangedListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
            if (radio_read_mode.indexOfChild(findViewById(radio_read_mode.checkedRadioButtonId)) == 3) {
                _barcode.setReadMode(DataType.READ_MODE.MULTIPLE)
                var value = 2
                try {
                    value = Integer.parseInt(editMultipleCount_intent.text.toString())
                    _barcode.setMultipleCount(value)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    var onOutputModeListener = OnClickListener { view ->
        var outputMode = DataType.OUTPUT_MODE.COPY_AND_PASTE
        when(view){
            output_mode_copyandpaste -> outputMode = DataType.OUTPUT_MODE.COPY_AND_PASTE
            output_mode_key -> outputMode = DataType.OUTPUT_MODE.KEY_STROKE
            output_mode_none -> outputMode = DataType.OUTPUT_MODE.COPY_TO_CLIPBOARD
        }
        _barcode.setOutputMode(outputMode)
    }

    var onEndModeListener = OnClickListener { view ->
        var endMode = DataType.END_CHAR.ENTER
        when (view) {
            end_enter -> endMode = DataType.END_CHAR.ENTER
            end_space -> endMode = DataType.END_CHAR.SPACE
            end_tab -> endMode = DataType.END_CHAR.TAB
            end_key_enter -> endMode = DataType.END_CHAR.KEY_ENTER
            end_key_space -> endMode = DataType.END_CHAR.KEY_SPACE
            end_key_tab -> endMode = DataType.END_CHAR.KEY_TAB
            end_none -> endMode = DataType.END_CHAR.NONE
        }
        _barcode.setEndCharMode(endMode)
    }

    var onFixClickListener = OnClickListener { view ->
        var strPrefix = edit_prefix.text.toString()
        var strPostfix = edit_postfix.text.toString()

        _barcode.setPrefix(strPrefix)
        _barcode.setPostfix(strPostfix)
    }

    var onAllCodeClickListener = OnClickListener { view ->

        var intent = Intent(ConstantValues.SCANNER_ACTION_SETTING_CHANGE)

        when (view) {
            button_disable_all -> intent.putExtra("setting", "disable_all_types")
            button_enable_all -> intent.putExtra("setting", "enable_all_types")
        }
        sendOrderedBroadcast(intent, null)
    }
}