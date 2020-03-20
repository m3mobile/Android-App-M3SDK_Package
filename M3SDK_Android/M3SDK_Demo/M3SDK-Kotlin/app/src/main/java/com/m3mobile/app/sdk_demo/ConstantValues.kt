package com.m3mobile.app.sdk_demo



object ConstantValues {

    val SCANNER_ACTION_SETTING_CHANGE = "com.android.server.scannerservice.settingchange"
    val LRSCANNER_ACTION_SETTING_CHANGE = "com.android.server.lrscannerservice.settingchange"
    val SCANNER_ACTION_PARAMETER = "android.intent.action.SCANNER_PARAMETER"
    val SCANNER_ACTION_ENABLE = "com.android.server.scannerservice.m3onoff"
    val LRSCANNER_ACTION_ENABLE = "com.android.server.lrscannerservice.m3onoff"
    val SCANNER_EXTRA_ENABLE = "scanneronoff"

    val SCANNER_ACTION_START = "android.intent.action.M3SCANNER_BUTTON_DOWN"
    val SCANNER_ACTION_CANCEL = "android.intent.action.M3SCANNER_BUTTON_UP"
    val LRSCANNER_ACTION_START = "android.intent.action.LRSCANNER_BUTTON_DOWN"
    val LRSCANNER_ACTION_CANCEL = "android.intent.action.LRSCANNER_BUTTON_UP"

    val SCANNER_ACTION_BARCODE = "com.android.server.scannerservice.broadcast"

    val SCANNER_EXTRA_BARCODE_DATA = "m3scannerdata"
    val SCANNER_EXTRA_BARCODE_CODE_TYPE = "m3scanner_code_type"
    val SCANNER_EXTRA_MODULE_TYPE = "m3scanner_module_type"
    val SCANNER_EXTRA_BARCODE_RAW_DATA = "m3scannerdata_raw"    // add 2017-01-17	after ScanEmul 1.3.0
    val SCANNER_EXTRA_BARCODE_DATA_LENGTH = "m3scannerdata_length"    // add 2017-01-31	after ScanEmul 1.3.0
    val SCANNER_EXTRA_BARCODE_DEC_COUNT = "m3scanner_dec_count" // add 2018-10-08   after ScanEmul 2.2.3

    val SCANNER_ACTION_IS_ENABLE = "com.android.server.scannerservice.m3onoff.ison"
    val SCANNER_ACTION_IS_ENABLE_ANSWER = "com.android.server.scannerservice.m3onoff.ison.answer"
    val SCANNER_EXTRA_IS_ENABLE_ANSWER = "ison"

    // add 20190730 after ScanEmul 2.4.6
    val SCANNER_ACTION_STATUS = "scanemul.action.status"
    val SCANNER_EXTRA_STATUS = "scanemul.extra.status"
    val SCANNER_STATUS_NO_ERROR = 0
    val SCANNER_STATUS_SCANNER_OPEN_FAIL = 1
    val SCANNER_STATUS_SCANNER_CLOSE_FAIL = 2
    val SCANNER_STATUS_SCANNER_OPEN_SUCCESS = 4
    val SCANNER_STATUS_SCANNER_CLOSE_SUCCESS = 8
}