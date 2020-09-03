package com.m3mobile.app.sdk_demo

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.OnClickListener
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.scanner_intent.*
import java.lang.NumberFormatException

class ScannerIntentActivity : Activity()  {
    private val TAG : String = "ScannerIntentActivity"
    private var barcodeBroadcast : BarcodeIntentBroadcast = BarcodeIntentBroadcast()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanner_intent)

        var filter  = IntentFilter()
        filter.addAction(ConstantValues.SCANNER_ACTION_BARCODE)
        filter.addAction(ConstantValues.SCANNER_ACTION_IS_ENABLE_ANSWER)
        filter.addAction(ConstantValues.SCANNER_ACTION_STATUS)
        registerReceiver(barcodeBroadcast, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(barcodeBroadcast)

        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()

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

        // vibration
        vibration_mode.setOnCheckedChangeListener(onVibrationCheckListener)
        vibration_mode.isChecked = true

        // scanner key
        key_all_disable.setOnClickListener(onKeyClickListener)
        key_enable.setOnClickListener(onKeyClickListener)
        key_only_sw_enable.setOnClickListener(onKeyClickListener)
        key_enable.isChecked = true;

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

    override fun onStop() {
        super.onStop()


    }

    private var onButtonClickListener = OnClickListener { view ->
        when(view){
            // basic fun
            startread_intent -> sendOrderedBroadcast(Intent(ConstantValues.SCANNER_ACTION_START, null), null)

            stopread_intent -> sendOrderedBroadcast(Intent(ConstantValues.SCANNER_ACTION_CANCEL, null), null)

            enable_intent -> sendOrderedBroadcast(
                Intent(ConstantValues.SCANNER_ACTION_ENABLE, null)
                    .putExtra(ConstantValues.SCANNER_EXTRA_ENABLE, 1)
                , null)

            disable_intent -> sendOrderedBroadcast(
                Intent(ConstantValues.SCANNER_ACTION_ENABLE, null)
                    .putExtra(ConstantValues.SCANNER_EXTRA_ENABLE, 0)
                , null)

            btn_is_enable -> sendOrderedBroadcast(Intent(ConstantValues.SCANNER_ACTION_IS_ENABLE, null), null)

            // param fun
            else -> {
                var num = -1
                var value = -1

                when(view){
                    // press get button
                    buttonGet_intent -> {

                        try {
                            num = Integer.parseInt(editPnum_intent.text.toString())
                        }catch (ex : java.lang.Exception){
                            ex.printStackTrace()
                        }

                        sendOrderedBroadcast(
                            Intent(ConstantValues.SCANNER_ACTION_PARAMETER)
                                .putExtra("symbology", num)
                            , null)

                    }
                    // press set button
                    buttonSet_intent -> {

                        try {
                            num = Integer.parseInt(editPnum_intent.text.toString())
                            value = Integer.parseInt(editPval_intent.text.toString())
                        }catch (ex : java.lang.Exception){
                            ex.printStackTrace()
                        }

                        sendOrderedBroadcast(
                            Intent(ConstantValues.SCANNER_ACTION_PARAMETER)
                                .putExtra("symbology", num)
                                .putExtra("value",value)
                            ,null)

                    }
                }
            }
        }
    }

    var onSoundClickListener = OnClickListener { view ->
        var intent = Intent(ConstantValues.SCANNER_ACTION_SETTING_CHANGE).putExtra("setting", "sound")
        when(view){
            sound_none -> intent.putExtra("sound_mode", 0)
            sound_beep -> intent.putExtra("sound_mode", 1)
            sound_dingdong -> intent.putExtra("sound_mode", 2)
        }
        sendOrderedBroadcast(intent, null)
    }

    var onVibrationCheckListener = CompoundButton.OnCheckedChangeListener { button, isChecked ->
         var intent = Intent(ConstantValues.SCANNER_ACTION_SETTING_CHANGE)
             .putExtra("setting", "vibration")
             .putExtra("vibration_value", if(isChecked == true) 1 else 0)
        sendOrderedBroadcast(intent, null)
    }

    var onKeyClickListener = OnClickListener { view ->
        var intent = Intent(ConstantValues.SCANNER_ACTION_SETTING_CHANGE)
            .putExtra("setting", "key_press")
        val ALL_DISABLE = 0
        val ALL_ENABLE = 1
        val ONLY_SW_KEY_ENABLE = 2
        when(view){
            key_all_disable -> intent.putExtra("key_press_value", ALL_DISABLE)
            key_enable -> intent.putExtra("key_press_value", ALL_ENABLE)
            key_only_sw_enable -> intent.putExtra("key_press_value", ONLY_SW_KEY_ENABLE)
        }
        sendOrderedBroadcast(intent, null)
    }

    var onReadModeClick = OnClickListener { view ->
         var intent = Intent(ConstantValues.SCANNER_ACTION_SETTING_CHANGE)
             .putExtra("setting", "read_mode")
        when(view){
            read_mode_async -> intent.putExtra("read_mode_value", 0)
            read_mode_sync -> intent.putExtra("read_mode_value", 1)
            read_mode_continue -> intent.putExtra("read_mode_value", 2)
            read_mode_multiple -> intent.putExtra("read_mode_value", 3)
        }
        sendOrderedBroadcast(intent, null)
    }

    val onMultiTextChangedListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        }
    }

    var onOutputModeListener = OnClickListener { view ->

        var intent = Intent(ConstantValues.SCANNER_ACTION_SETTING_CHANGE)
            .putExtra("setting", "output_mode")
        when(view){
            output_mode_copyandpaste -> intent.putExtra("output_mode_value", 0)
            output_mode_key -> intent.putExtra("output_mode_value", 1)
            output_mode_none -> intent.putExtra("output_mode_value", 2)
        }
        sendOrderedBroadcast(intent, null)
    }

    var onEndModeListener = OnClickListener { view ->
        var intent = Intent(ConstantValues.SCANNER_ACTION_SETTING_CHANGE)
            .putExtra("setting", "end_char")
        when(view){
            end_enter -> intent.putExtra("end_char_value", 0)
            end_space -> intent.putExtra("end_char_value", 1)
            end_tab -> intent.putExtra("end_char_value", 2)
            end_key_enter -> intent.putExtra("end_char_value", 3)
            end_key_space -> intent.putExtra("end_char_value", 4)
            end_key_tab -> intent.putExtra("end_char_value", 5)
            end_none -> intent.putExtra("end_char_value", 6)
        }
        sendOrderedBroadcast(intent, null)
    }

    var onFixClickListener = OnClickListener { view ->
        var strPrefix = edit_prefix.text.toString()
        var strPostfix = edit_postfix.text.toString()

        // prefix
        sendOrderedBroadcast(
            Intent(ConstantValues.SCANNER_ACTION_SETTING_CHANGE)
                .putExtra("setting", "prefix")
                .putExtra("prefix_value", strPrefix), null
        )
        sendOrderedBroadcast(
            Intent(ConstantValues.SCANNER_ACTION_SETTING_CHANGE)
                .putExtra("setting", "postfix")
                .putExtra("postfix_value", strPostfix), null
        )
    }

    var onAllCodeClickListener = OnClickListener { view ->

        var intent = Intent(ConstantValues.SCANNER_ACTION_SETTING_CHANGE)

        when(view){
            button_disable_all -> intent.putExtra("setting", "disable_all_types")
            button_enable_all -> intent.putExtra("setting", "enable_all_types")
        }
        sendOrderedBroadcast(intent, null)
    }


    inner class BarcodeIntentBroadcast : BroadcastReceiver() {
        private var barcode: String? = null
        private var type: String? = null
        private var module: String? = null
        private lateinit var rawdata: ByteArray
        private var length = 0
        private var decCount = 0

        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent!!.action == ConstantValues.SCANNER_ACTION_BARCODE) {
                barcode = intent.extras!!.getString(ConstantValues.SCANNER_EXTRA_BARCODE_DATA)
                type = intent.extras!!.getString(ConstantValues.SCANNER_EXTRA_BARCODE_CODE_TYPE)
                module = intent.extras!!.getString(ConstantValues.SCANNER_EXTRA_MODULE_TYPE)

                try {
                    rawdata = intent.extras!!.getByteArray(ConstantValues.SCANNER_EXTRA_BARCODE_RAW_DATA)!!
                } catch (e: Exception) {
                    Log.d(TAG,"onReceive scanner - null raw data")
                }
                length = intent.extras!!.getInt(ConstantValues.SCANNER_EXTRA_BARCODE_DATA_LENGTH, 0)
                decCount = intent.extras!!.getInt(ConstantValues.SCANNER_EXTRA_BARCODE_DEC_COUNT, 0)
                if (barcode != null) {
                    if (rawdata.isNotEmpty()) {
                        var strRawData = ""
                        for (i in rawdata.indices) {
                            strRawData += String.format("0x%02X ",rawdata[i].toInt() and 0xFF)
                        }
                        scanresult_intent.setText("data: $barcode \ntype: $type \nraw: $strRawData")
                    } else {
                        scanresult_intent.setText("data: $barcode type: $type")
                    }
                } else {
                    val nSymbol = intent.extras!!.getInt("symbology", -1)
                    val nValue = intent.extras!!.getInt("value", -1)
                    Log.i(TAG,"getSymbology [$nSymbol][$nValue]")
                    if (nSymbol != -1) {
                        editPnum_intent.setText(nSymbol.toString())
                        editPval_intent.setText(nValue.toString())
                    }
                }
            } else if (intent!!.action.equals(ConstantValues.SCANNER_ACTION_STATUS)){
                var strMessage : String = ""
                module = intent.getStringExtra(ConstantValues.SCANNER_EXTRA_MODULE_TYPE)
                var nStatus = intent.getIntExtra(ConstantValues.SCANNER_EXTRA_STATUS, 0)
                Log.i(TAG, "Status: $nStatus ${nStatus and ConstantValues.SCANNER_STATUS_SCANNER_OPEN_SUCCESS}")

                if (nStatus and ConstantValues.SCANNER_STATUS_SCANNER_OPEN_SUCCESS > 0) {
                    strMessage += "Scanner Open Success\n"
                }else if(nStatus and ConstantValues.SCANNER_STATUS_SCANNER_OPEN_FAIL > 0){
                    strMessage += "Scanner Open Fail\n"
                }else if(nStatus and ConstantValues.SCANNER_STATUS_SCANNER_CLOSE_SUCCESS > 0){
                    strMessage += "Scanner Close Success\n"
                }else if(nStatus and ConstantValues.SCANNER_STATUS_SCANNER_CLOSE_FAIL > 0){
                    strMessage += "Scanner Close Fail\n"
                }

                strMessage += "Scanner: $module"
                scanresult_intent.text = strMessage
            }
        }
    }
}