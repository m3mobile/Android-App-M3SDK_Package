package net.m3mobile.ugr_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by M3 on 2017-12-14.
 */

public class ConfigPreferenceActivity extends AppCompatActivity {

    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsConfigFragment()).commit();
    }

    public static class PrefsConfigFragment extends PreferenceFragment {

        ListPreference region;
        int nPreRegionValue;
        EditTextPreference power;
        int nPower;
        EditTextPreference editVersion;
        String strDllVersion;
        String strFirmVersion;

        ProgressBarHandler mPrgHandler;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.config_preference);

            region = (ListPreference) findPreference("region");
            region.setSummary(region.getEntry());
            region.setOnPreferenceChangeListener(onConfigChangePreference);

            nPreRegionValue = Integer.parseInt(region.getValue());
            Intent regionIntent = new Intent(UGRApplication.UGR_ACTION_GET_SETTING);
            regionIntent.putExtra("setting", "region_oem");
            mContext.sendOrderedBroadcast(regionIntent, null);

            nPower = 0;
            Intent powerIntent = new Intent(UGRApplication.UGR_ACTION_GET_SETTING);
            powerIntent.putExtra("setting", "power");
            mContext.sendOrderedBroadcast(powerIntent, null);

            power = (EditTextPreference) findPreference("power");
            power.setOnPreferenceChangeListener(onConfigChangePreference);

            mPrgHandler = new ProgressBarHandler(mContext);

            // Version
            Intent versionIntent = new Intent(UGRApplication.UGR_ACTION_GET_SETTING);
            versionIntent.putExtra("setting", "version");
            mContext.sendOrderedBroadcast(versionIntent, null);

            editVersion = (EditTextPreference) findPreference("version");

            IntentFilter filter = new IntentFilter();
            filter.addAction(UGRApplication.UGR_ACTION_SETTING);
            mContext.registerReceiver(UGRSettingIntentReceiver, filter);
        }

        private void setRegion(int nRegion) {
            Intent intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
            intent.putExtra("setting", "region_oem");
            intent.putExtra("region_oem_value", nRegion);
            mContext.sendOrderedBroadcast(intent, null);

            region.setValue(String.valueOf(nRegion));
            region.setSummary(region.getEntry());
        }

        private Preference.OnPreferenceChangeListener onConfigChangePreference = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                String key = preference.getKey();

                if(key.equals("region")) {

                    int nRegion = Integer.valueOf((String)newValue);
                    mPrgHandler.show();
                    Intent intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
                    intent.putExtra("setting", "region_oem");
                    intent.putExtra("region_oem_value", nRegion);
                    mContext.sendOrderedBroadcast(intent, null);

                    ((ListPreference)preference).setValue((String)newValue);
                    preference.setSummary(((ListPreference)preference).getEntry());
                } else if(key.equals("power")) {

                    String strValue = (String)newValue;

                    int nPower = Integer.valueOf(strValue);
                    if(nPower < 0 || nPower > 300) {
                        return false;
                    }
                    String strSummary = "Set a value from 0 to 300 : " + strValue;
                    preference.setSummary(strSummary);

                    Intent intent = new Intent(UGRApplication.UGR_ACTION_SETTING_CHANGE);
                    intent.putExtra("setting", "power");
                    intent.putExtra("power_value", nPower);
                    mContext.sendOrderedBroadcast(intent, null);
                }

                return true;
            }
        };

        public BroadcastReceiver UGRSettingIntentReceiver = new BroadcastReceiver() {

            int nOemRegionValue;

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", intent.getAction());

                if(intent.getAction().equals(UGRApplication.UGR_ACTION_SETTING)) {
                    String extra = intent.getStringExtra("setting");

                    if(extra.equals("region_oem")) {
                        nOemRegionValue = intent.getExtras().getInt(UGRApplication.UGR_EXTRA_REGION_OEM);
                        Log.d("onReceive", "nOemRegion = " + nOemRegionValue);

                        if(nPreRegionValue != nOemRegionValue) {
                            region.setValue(String.valueOf(nOemRegionValue));
                            region.setSummary(region.getEntry());
                        }
                    } else if(extra.equals("power")) {
                        nPower = intent.getExtras().getInt(UGRApplication.UGR_EXTRA_POWER);
                        Log.d("onReceive", "nPower = " + nPower);

                        if(nPower >= 0 && nPower <= 300) {
                            power.setDefaultValue(Integer.toString(nPower));
                            String strSummary = "Set a value from 0 to 300 : " + Integer.toString(nPower);
                            power.setSummary(strSummary);
                        }
                    } else if(extra.equals("version")) {
                        strDllVersion = intent.getExtras().getString(UGRApplication.UGR_EXTRA_DLL_VERSION);
                        strFirmVersion = intent.getExtras().getString(UGRApplication.UGR_EXTRA_FIRM_VERSION);
                        Log.d("onReceive", "strDllVersion = " + strDllVersion);
                        Log.d("onReceive", "strFirmVersion = " + strFirmVersion);

                        String strVersion = "Lib: ver." + strDllVersion + " Firm: ver." + strFirmVersion;
                        editVersion.setSummary(strVersion);
                    } else if(extra.equals("complete")) {
                        mPrgHandler.hide();
                    }
                }
            }
        };

        @Override
        public void onDestroy() {
            mContext.unregisterReceiver(UGRSettingIntentReceiver);
            ResultWindow.bNeedConnect = false;

            super.onDestroy();
        }
    }

}
