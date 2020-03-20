package net.m3mobile.app.scannerservicehoney;

public class SymbologyValue {

    private SymbologyValue() {
    }

    /*
    * These are the defines that are used for configuring symbology options.
    * This defines are used in conjunction with the getSymbologyConfig and setSymbologyConfig functions
    * in order to enable, disable and configure symbology parameters within the application.
    * */

    public final class SymbologyFlags {
        public static final int SYMBOLOGY_ENABLE = 1;   // Enable Symbology bit
        public static final int SYMBOLOGY_CHECK_ENABLE = 2; // Enable usage of check character
        public static final int SYMBOLOGY_CHECK_TRANSMIT = 4;   // Send check character
        public static final int SYMBOLOGY_START_STOP_XMIT = 8;  // Include the start and stop characters in result string
        /** @deprecated */
        public static final int SYMBOLOGY_ENABLE_APPEND_MODE = 16;  // Code39 append mode
        public static final int SYMBOLOGY_ENABLE_FULLASCII = 32;    // Enable Code39 Full ASCII
        public static final int SYMBOLOGY_NUM_SYS_TRANSMIT = 64;    // UPC-A/UPC-E send NUM Sys
        public static final int SYMBOLOGY_2_DIGIT_ADDENDA = 128;    // Enable 2 digit Addenda (UPC & EAN)
        public static final int SYMBOLOGY_5_DIGIT_ADDENDA = 256;    // Enable 5 digit Addenda (UPC & EAN)
        public static final int SYMBOLOGY_ADDENDA_REQUIRED = 512;   // Only allow codes with addenda (UPC & EAN)
        public static final int SYMBOLOGY_ADDENDA_SEPARATOR = 1024; // Include Addenda separator space in returned string
        public static final int SYMBOLOGY_UPCA_TRANSLATE_TO_EAN13 = 2097152;    // upca to ean13
        public static final int SYMBOLOGY_EXPANDED_UPCE = 2048;     // Extended UPC-E
        public static final int SYMBOLOGY_UPCE1_ENABLE = 4096;      // UPC-E1 enable (use SYMBOLOGY_ENABLE for UPC-E0)
        public static final int SYMBOLOGY_COMPOSITE_UPC = 8192;     // Enable UPC composite codes
        public static final int SYMBOLOGY_AUSTRALIAN_BAR_WIDTH = 65536;     // Include Australian postal bar data in string
        /** @deprecated */
        public static final int SYMBOLOGY_128_APPEND = 524288;
        public static final int SYMBOLOGY_RSE_ENABLE = 8388608;     // Enable RSE Symbology bit
        public static final int SYMBOLOGY_RSL_ENABLE = 16777216;    // Enable RSL Symbology bit
        public static final int SYMBOLOGY_RSS_ENABLE = 33554432;    // Enable RSS Symbology bit
        public static final int SYMBOLOGY_RSX_ENABLE_MASK = 58720256;   // Enable all RSS versions
        public static final int SYMBOLOGY_TELEPEN_OLD_STYLE = 67108864;     // Telepen Old Style mode
        /** @deprecated */
        public static final int SYMBOLOGY_POSICODE_LIMITED_1 = 134217728;   // PosiCode Limited of 1
        /** @deprecated */
        public static final int SYMBOLOGY_POSICODE_LIMITED_2 = 268435456;   // PosiCode Limited of 2
        public static final int SYMBOLOGY_CODABAR_CONCATENATE = 536870912;  // Codabar concatenate
        public static final int SYMBOLOGY_AUS_POST_NUMERIC_N_TABLE = 1048576;   // Numeric N Table
        public static final int SYMBOLOGY_AUS_POST_ALPHANUMERIC_C_TABLE = 2097152;      // Alphanumeric C Table
        public static final int SYMBOLOGY_AUS_POST_COMBINATION_N_AND_C_TABLES = 4194304;    // Combination N and C Tables

        public static final int SYM_MASK_FLAGS = 1; // Flags are valid
        public static final int SYM_MASK_MIN_LEN = 2;   // Min Length valid
        public static final int SYM_MASK_MAX_LEN = 4;   // max Length Valid
        public static final int SYM_MASK_ALL = 7;       // All flags are valid

        private SymbologyFlags() {
        }
    }

    public static final class SymbologyID {
        public static final int SYM_AZTEC = 0;      // Aztec Code
        public static final int SYM_CODABAR = 1;
        public static final int SYM_CODE11 = 2;
        public static final int SYM_CODE128 = 3;
        public static final int SYM_CODE39 = 4;
        /** @deprecated */
        public static final int SYM_CODE49 = 5;
        public static final int SYM_CODE93 = 6;
        public static final int SYM_COMPOSITE = 7;      // EAN UCC Composite
        public static final int SYM_DATAMATRIX = 8;
        public static final int SYM_EAN8 = 9;
        public static final int SYM_EAN13 = 10;
        public static final int SYM_INT25 = 11;     // Interleaved 2 of 5
        public static final int SYM_MAXICODE = 12;
        public static final int SYM_MICROPDF = 13;
        /** @deprecated */
        public static final int SYM_OCR = 14;
        public static final int SYM_PDF417 = 15;
        /** @deprecated */
        public static final int SYM_POSTNET = 16;
        public static final int SYM_QR = 17;
        public static final int SYM_RSS = 18;       // Reduced Space Symbology (RSS-14, RSS Limited, RSS Expanded)
        public static final int SYM_UPCA = 19;
        public static final int SYM_UPCE0 = 20;
        public static final int SYM_UPCE1 = 21;
        public static final int SYM_ISBT = 22;
        public static final int SYM_BPO = 23;       // British Post [2D Postal]
        public static final int SYM_CANPOST = 24;   // Canadian Post [2D Postal]
        public static final int SYM_AUSPOST = 25;   // Australian Post [2D Postal]
        public static final int SYM_IATA25 = 26;    // Straight 2 of 5 IATA (two-bar start/stop)
        public static final int SYM_CODABLOCK = 27;     // Codablock F
        public static final int SYM_JAPOST = 28;    // Japanese Post [2D Postal]
        public static final int SYM_PLANET = 29;    // Planet Code [2D Postal]
        public static final int SYM_DUTCHPOST = 30;     // KIX (Netherlands) Post [2D Postal]
        public static final int SYM_MSI = 31;
        public static final int SYM_TLCODE39 = 32;      // TCIF Linked Code 39 (TLC39)
        public static final int SYM_TRIOPTIC = 33;      // Trioptic Code
        public static final int SYM_CODE32 = 34;        // Code 32 Italian Pharmacy Code
        public static final int SYM_STRT25 = 35;        // Straight 2 of 5 Industrial (three-bar start/stop)
        public static final int SYM_MATRIX25 = 36;
        /** @deprecated */
        public static final int SYM_PLESSEY = 37;
        public static final int SYM_CHINAPOST = 38;     // China Post
        public static final int SYM_KOREAPOST = 39;
        public static final int SYM_TELEPEN = 40;
        /** @deprecated */
        public static final int SYM_CODE16K = 41;       // Code 16K
        /** @deprecated */
        public static final int SYM_POSICODE = 42;
        public static final int SYM_COUPONCODE = 43;
        public static final int SYM_USPS4CB = 44;   // USPS 4 State (Intelligent Mail Barcode) [2D Postal]
        public static final int SYM_IDTAG = 45;     // UPU 4 State [2D Postal]
        /** @deprecated */
        public static final int SYM_LABEL = 46;
        public static final int SYM_GS1_128 = 47;
        public static final int SYM_HANXIN = 48;
        /** @deprecated */
        public static final int SYM_GRIDMATRIX = 49;
        public static final int SYM_POSTALS = 50;       // Used to default and disable postal codes
        public static final int SYM_US_POSTALS1 = 51;   // Used to enable SYM_PLANET, SYM_POSTNET, SYM_USPS4CB & SYM_IDTAG
        public static final int SYMBOLOGIES = 52;       // Number of Symbologies
        public static final int SYM_ALL = 100;  // All Symbologies

        private SymbologyID() {
        }
    }

    public static final int  AUS_POST = 1;
    public static final int  JAPAN_POST = 3;
    public static final int  KIX = 4;
    public static final int  PLANETCODE = 5;
    public static final int  POSTNET = 6;
    public static final int  ROYAL_MAIL = 7;
    public static final int  UPU_4_STATE = 9;
    public static final int  USPS_4_STATE = 10;
    public static final int  US_POSTALS = 29;
    public static final int  CANADIAN = 30;
}
