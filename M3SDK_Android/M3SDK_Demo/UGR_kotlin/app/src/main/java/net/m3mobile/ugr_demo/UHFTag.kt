package net.m3mobile.ugr_demo

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by M3 on 2017-12-07.
 */
class UHFTag(var EPC: String, var Reads: Int) {
    var TIME: String
    var time: Long
    var diff: Long = 0

    init {
        val currentTime = System.currentTimeMillis()
        time = currentTime
        @SuppressLint("SimpleDateFormat") val currentDayTime =
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        TIME = currentDayTime.format(Date(currentTime))
    }
}