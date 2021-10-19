package net.m3mobile.ugremul;
import net.m3mobile.ugremul.IUHFServiceCallback;

interface IUHFServiceCallback
{
   oneway void onInventory(String epc);
   oneway void onIsReading(boolean isReading);
   oneway void onEnable(boolean isEnable);
   // 2020-03-16 1.2.16
   oneway void onInventoryRssi(String epc, double nb_rssi, double wb_rssi);
}
