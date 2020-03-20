package net.m3mobile.ugremul;
import net.m3mobile.ugremul.IUHFServiceCallback;

interface IUHFServiceCallback
{
   oneway void onInventory(String epc);
   oneway void onIsReading(boolean isReading);
}
