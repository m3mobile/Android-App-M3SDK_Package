package net.m3mobile.ugr_demo;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by M3 on 2017-12-07.
 */

public class UHFTag {
    public String EPC;
    public int Reads;
    public String TIME;
    public long time, diff;

    public UHFTag(String EPC, int reads) {
        this.EPC = EPC;
        Reads = reads;

        long currentTime = System.currentTimeMillis();
        time = currentTime;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        TIME = currentDayTime.format(new Date(currentTime));
    }
}
