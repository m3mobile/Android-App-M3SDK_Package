package net.m3mobile.ugr_demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import net.m3mobile.ugr_demo.ConfigPreferenceActivity
import net.m3mobile.ugr_demo.ConfigPreferenceActivity.PrefsConfigFragment
import android.preference.PreferenceFragment
import android.preference.ListPreference
import android.preference.EditTextPreference
import net.m3mobile.ugr_demo.ProgressBarHandler
import net.m3mobile.ugr_demo.R
import android.content.Intent
import net.m3mobile.ugr_demo.UGRApplication
import android.content.IntentFilter
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import net.m3mobile.ugr_demo.ResultWindow

/**
 * Created by M3 on 2017-12-14.
 */
class ConfigPreferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, PrefsConfigFragment()).commit()
    }

    class PrefsConfigFragment : PreferenceFragment() {
        var region: ListPreference? = null
        var nPreRegionValue = 0
        var power: EditTextPreference? = null
        var nPower = 0
        var editVersion: EditTextPreference? = null
        var strDllVersion: String? = null
        var strFirmVersion: String? = null
        var mPrgHandler: ProgressBarHandler? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.config_preference)
            region = findPreference("region") as ListPreference
            region!!.summary = region!!.entry
            region!!.onPreferenceChangeListener = onConfigChangePreference
            nPreRegionValue = region!!.value.toInt()
            val regionIntent = Intent(UGRApplication.UGR_ACTION_GET_SETTING)
            regionIntent.putExtra("setting", "region_oem")
            mContext!!.sendOrderedBroadcast(regionIntent, null)
            nPower = 0
            val powerIntent = Intent(UGRApplication.UGR_ACTION_GET_SETTING)
            powerIntent.putExtra("setting", "power")
            mContext!!.sendOrderedBroadcast(powerIntent, null)
            power = findPreference("power") as EditTextPreference
            power!!.onPreferenceChangeListener = onConfigChangePreference
            mPrgHandler = ProgressBarHandler(mContext!!)

            // Version
            val versionIntent = Intent(UGRApplication.UGR_ACTION_GET_SETTING)
            versionIntent.putExtra("setting", "version")
            mContext!!.sendOrderedBroadcast(versionIntent, null)
            editVersion = findPreference("version") as EditTextPreference
            val filter = IntentFilter()
            filter.addAction(UGRApplication.UGR_ACTION_SETTING)
            mContext!!.registerReceiver(UGRSettingIntentReceiver, filter)
        }

        private fun setRegion(nRegion: Int) {
            val intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
            intent.putExtra("setting", "region_oem")
            intent.putExtra("region_oem_value", nRegion)
            mContext!!.sendOrderedBroadcast(intent, null)
            region!!.value = nRegion.toString()
            region!!.summary = region!!.entry
        }

        private val onConfigChangePreference = OnPreferenceChangeListener { preference, newValue ->
            val key = preference.key
            if (key == "region") {
                val nRegion = Integer.valueOf(newValue as String)
                mPrgHandler!!.show()
                val intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
                intent.putExtra("setting", "region_oem")
                intent.putExtra("region_oem_value", nRegion)
                mContext!!.sendOrderedBroadcast(intent, null)
                (preference as ListPreference).value = newValue
                preference.setSummary(preference.entry)
            } else if (key == "power") {
                val strValue = newValue as String
                val nPower = Integer.valueOf(strValue)
                if (nPower < 0 || nPower > 300) {
                    return@OnPreferenceChangeListener false
                }
                val strSummary = "Set a value from 0 to 300 : $strValue"
                preference.summary = strSummary
                val intent = Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE)
                intent.putExtra("setting", "power")
                intent.putExtra("power_value", nPower)
                mContext!!.sendOrderedBroadcast(intent, null)
            }
            true
        }
        var UGRSettingIntentReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            var nOemRegionValue = 0
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("onReceive", intent.action!!)
                if (intent.action == UGRApplication.UGR_ACTION_SETTING) {
                    val extra = intent.getStringExtra("setting")
                    if (extra == "region_oem") {
                        nOemRegionValue =
                            intent.extras!!.getInt(UGRApplication.UGR_EXTRA_REGION_OEM)
                        Log.d("onReceive", "nOemRegion = $nOemRegionValue")
                        if (nPreRegionValue != nOemRegionValue) {
                            region!!.value = nOemRegionValue.toString()
                            region!!.summary = region!!.entry
                        }
                    } else if (extra == "power") {
                        nPower = intent.extras!!.getInt(UGRApplication.UGR_EXTRA_POWER)
                        Log.d("onReceive", "nPower = $nPower")
                        if (nPower >= 0 && nPower <= 300) {
                            power!!.setDefaultValue(Integer.toString(nPower))
                            val strSummary =
                                "Set a value from 0 to 300 : " + Integer.toString(nPower)
                            power!!.summary = strSummary
                        }
                    } else if (extra == "version") {
                        strDllVersion =
                            intent.extras!!.getString(UGRApplication.UGR_EXTRA_DLL_VERSION)
                        strFirmVersion =
                            intent.extras!!.getString(UGRApplication.UGR_EXTRA_FIRM_VERSION)
                        Log.d("onReceive", "strDllVersion = $strDllVersion")
                        Log.d("onReceive", "strFirmVersion = $strFirmVersion")
                        val strVersion = "Lib: ver.$strDllVersion Firm: ver.$strFirmVersion"
                        editVersion!!.summary = strVersion
                    } else if (extra == "complete") {
                        mPrgHandler!!.hide()
                    }
                }
            }
        }

        override fun onDestroy() {
            mContext!!.unregisterReceiver(UGRSettingIntentReceiver)
            ResultWindow.bNeedConnect = false
            super.onDestroy()
        }
    }

    companion object {
        private var mContext: Context? = null
    }
}