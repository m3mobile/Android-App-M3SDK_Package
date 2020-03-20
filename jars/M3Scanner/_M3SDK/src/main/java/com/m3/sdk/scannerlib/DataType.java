package com.m3.sdk.scannerlib;

/**
 * Created by Jayden on 2018-01-15.
 */

public class DataType {

    public enum DEVICE{
        UNKNOWN,
        SM10,
        SM10LTE,
        SM15,
        SM15_OREO,
        SM15_OREO_GMS,
        UL20_OREO,
        UL20_PIE,
        TN15,
        TX15,
        TL20
    }

    public enum SCANNER_TYPE{
        UNKNOWN,
        ONE_Dimension,
        TWO_Dimension
    }

    public enum SCANNER_MODULE{
        UNKNOWN,
        SE955,
        SE965,
        SE4500,
        SE4710,
        SE4750,
        N6600,
        N6603,
        NOTHING,
        SE4850,
        N6700;

        public static SCANNER_MODULE fromInteger(int x) {
            switch(x) {
                case 0:
                    return UNKNOWN;
                case 1:
                    return SE955;
                case 2:
                    return SE965;
                case 3:
                    return SE4500;
                case 4:
                    return SE4710;
                case 5:
                    return SE4750;
                case 6:
                    return N6600;
                case 7:
                    return N6603;
                case 8:
                    return NOTHING;
                case 9:
                    return SE4850;
                case 10:
                    return N6700;
                default:
                    return UNKNOWN;
            }
        }
    }

    // end char
    public enum END_CHAR{
        ENTER,
        SPACE,
        TAB,
        KEY_ENTER,
        KEY_SPACE,
        KEY_TAB,
        NONE
    }
    // outputmode
    public enum OUTPUT_MODE{
        COPY_AND_PASTE,
        KEY_STROKE,
        COPY_TO_CLIPBOARD
    }
    // sound
    public enum SOUND_MODE{
        NONE,
        BEEP,
        DING_DONG
    }
    // read mode
    public enum READ_MODE{
        ASYNC,
        SYNC,
        CONTINUE,
        MULTIPLE
    }
    // trigger mode
    public enum SCAN_TRIGGER{
        ALL_ENABLE,
        ALL_DISABLE,
        @Deprecated
        ONLY_SW_TRIGGER
    }
}
