package net.m3mobile.ugr_demo;

/**
 * Created by M3 on 2017-12-06.
 */

public class UGRApplication extends android.app.Application{
    public static final String SCANNER_KEY_ENABLE_INTENT = "m3mobile.key.scan.intent.enable";
    public static final String SCANNER_KEY_ENABLE_EXTRA = "enable";
    public static final String SCANNER_KEY_INTENT = "m3mobile.key.scan.intent";
    public static final String SCANNER_KEY_EXTRA = "is_pressed";
    public static final String UGR_ACTION_EPC = "com.android.server.ugrservice.broadcast";
    public static final String UGR_EXTRA_EPC_DATA = "m3ugrdata";
    public static final String UGR_ACTION_START = "android.intent.action.M3UGR_BUTTON_DOWN";
    public static final String UGR_ACTION_CANCEL = "android.intent.action.M3UGR_BUTTON_UP";
    public static final String UGR_ACTION_IS_READING = "com.android.server.ugrservice.isreading";
    public static final String UGR_EXTRA_IS_READING = "m3ugr_is_reading";
    public static final String SCANNER_ACTION_SETTING_CHANGE = "com.android.server.scannerservice.settingchange";
    public static final String SCANNER_ACTION_BARCODE = "com.android.server.scannerservice.broadcast";
    public static final String UGR_ACTION_GET_SETTING = "com.android.server.ugrservice.getsetting";
    public static final String UGR_ACTION_SETTING = "com.android.server.ugrservice.setting";
    public static final String UGR_EXTRA_POWER = "m3ugr_power";
    public static final String UGR_EXTRA_REGION_OEM = "m3ugr_region_oem";
    public static final String UGR_EXTRA_DLL_VERSION = "m3ugr_dll_version";
    public static final String UGR_EXTRA_FIRM_VERSION = "m3ugr_firm_version";
    public static final String UGR_EXTRA_CHANNEL = "m3ugr_channel";
    public static final String UGR_ACTION_SETTING_CHANGE = "com.android.server.ugrservice.settingchange";
    public static final String UGR_ACTION_MEMORY_READING = "com.android.server.ugrservice.reading";
    public static final String UGR_ACTION_MEMORY_WRITING = "com.android.server.ugrservice.writing";
    public static final String UGR_ACTION_MEMORY_RESPONSE = "com.android.server.ugrservice.memory.response";
    public static final String UGR_EXTRA_MEMORY = "m3ugr_memory";
    public static final String UGR_ACTION_LOCK = "com.android.server.ugrservice.lock";
    public static final String UGR_ACTION_KILL = "com.android.server.ugrservice.kill";
    public static final String UGR_ACTION_LOCK_RESPONSE = "com.android.server.ugrservice.lock.response";
    public static final String UGR_ACTION_KILL_RESPONSE = "com.android.server.ugrservice.kill.response";
    public static final String UGR_ACTION_ENABLE = "com.android.server.ugrservice.m3onoff";
    public static final String UGR_EXTRA_ENABLE = "ugronoff";

    public static final int MSG_IS_READING = 100;
    public static final int MSG_HANDLE_DATA = 103;
}
