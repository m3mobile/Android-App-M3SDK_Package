using System;
using System.Collections.Generic;
using System.Text;

namespace M3SDK_Xamarin
{
    public interface IM3Scanner
    {
        void SetEnable(bool bEnable);
        void DecodeStart();
        void DecodeStop();
        void RegisterReceiver();
        void UnregisterReceiver();
        void GetScanParam(int param);
        void SetScanParam(int param, int value);
        void SetKeyDisable(bool bDisable);
    }


}
