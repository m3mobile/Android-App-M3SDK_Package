using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using M3SDK_Xamarin.Droid;
using Xamarin.Forms;

[assembly:Dependency(typeof(Class_Scan))]
namespace M3SDK_Xamarin.Droid
{
    public class Class_Scan : IM3Scanner
    {
        // action scanner Settings
        private const String SCANNER_ACTION_SETTING_CHANGE = "com.android.server.scannerservice.settingchange";
        // Action Scanner Setting Parameter and Value
        private const String SCANNER_ACTION_PARAMETER = "android.intent.action.SCANNER_PARAMETER";        
        // action Scanner Enable or Disable
        private const String SCANNER_ACTION_ENABLE = "com.android.server.scannerservice.m3onoff";
        // Extra about scanner enable or Disable
        private const String SCANNER_EXTRA_ENABLE = "scanneronoff";

        // Action start decoding
        private const String SCANNER_ACTION_START = "android.intent.action.M3SCANNER_BUTTON_DOWN";
        // action Stop decoding
        private const String SCANNER_ACTION_CANCEL = "android.intent.action.M3SCANNER_BUTTON_UP";

        // Action to receive decoding result and setting parameter result
        private const String SCANNER_ACTION_BARCODE = "com.android.server.scannerservice.broadcast";
        // Extra about decoding result
        public const String SCANNER_EXTRA_BARCODE_DATA = "m3scannerdata";
        // Extra about code type
        public const String SCANNER_EXTRA_BARCODE_CODE_TYPE = "m3scanner_code_type";

        private ScanReceiver _scanReceiver = new ScanReceiver();

        public void DecodeStart()
        {
            Intent intent = new Intent(SCANNER_ACTION_START);
            Android.App.Application.Context.SendBroadcast(intent);
        }

        public void DecodeStop()
        {
            Android.App.Application.Context.SendBroadcast(new Intent(SCANNER_ACTION_CANCEL));
        }

        public void RegisterReceiver()
        {
            // for getting decoding result and setParam, getParam result.
            System.Diagnostics.Debug.WriteLine(String.Format("RegisterReceiver"));
            IntentFilter filter = new IntentFilter();
            filter.AddAction(SCANNER_ACTION_BARCODE);
            Android.App.Application.Context.RegisterReceiver(_scanReceiver, filter);
        }

        public void UnregisterReceiver()
        {
            System.Diagnostics.Debug.WriteLine(String.Format("UnregisterReceiver"));
            Android.App.Application.Context.UnregisterReceiver(_scanReceiver);
        }

        public void SetEnable(bool bEnable)
        {
            // Scanner enable or disable. If you want to prevent only decoding, we recommend 'SetKeyDisable'
            Intent intent = new Intent(SCANNER_ACTION_ENABLE);
            intent.PutExtra(SCANNER_EXTRA_ENABLE, bEnable ? 1 : 0);
            Android.App.Application.Context.SendBroadcast(intent);
        }

        public void GetScanParam(int param)
        {
            Intent intent = new Intent(SCANNER_ACTION_PARAMETER);
            intent.PutExtra("symbology", param);
            intent.PutExtra("value", -1);
            Android.App.Application.Context.SendBroadcast(intent);
        }

        public void SetScanParam(int param, int value)
        {
            Intent intent = new Intent(SCANNER_ACTION_PARAMETER);
            intent.PutExtra("symbology", param);
            intent.PutExtra("value", value);
            Android.App.Application.Context.SendBroadcast(intent);
        }

        public void SetKeyDisable(bool bDisable)
        {
            Intent intent = new Intent(SCANNER_ACTION_SETTING_CHANGE);
            intent.PutExtra("setting", "key_press");
            intent.PutExtra("key_press_value", bDisable ? 0 : 1);
            Android.App.Application.Context.SendBroadcast(intent);
        }

        public class ScanReceiver : BroadcastReceiver
        {
            private String barcode;
            private String type;
            private App scanApp = new App();

            public override void OnReceive(Context context, Intent intent)
            {
                Android.Util.Log.Info("ScanReceiver", "onReceiver: " + intent.Action);
                if (intent.Action.Equals(SCANNER_ACTION_BARCODE))
                {
                    barcode = intent.GetStringExtra(SCANNER_EXTRA_BARCODE_DATA);
                    if(barcode != null)
                    {
                        // Send Barcode Data
                        type = intent.GetStringExtra(SCANNER_EXTRA_BARCODE_CODE_TYPE);
                        MessagingCenter.Send<App, string>(scanApp, "barcode", barcode);
                        System.Diagnostics.Debug.WriteLine(String.Format("OnReceive barcode: " + barcode + " type: " + type));
                    }
                    else
                    {
                        // Send Parameter data
                        int nParam = intent.GetIntExtra("symbology", -1);
                        int nValue = intent.GetIntExtra("value", -1);
                        MessagingCenter.Send<App, int>(scanApp, "value", nValue);
                        System.Diagnostics.Debug.WriteLine(String.Format("OnReceive param: " + nParam + " value: " + nValue));
                    }
                }
            }
        }

    }
}