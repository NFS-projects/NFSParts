/*
 * Copyright (C) 2020 Paranoid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.needforspeed.settings;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.Context;
import android.os.Bundle;
import android.content.SharedPreferences;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;

import com.needforspeed.settings.preferences.CustomSeekBarPreference;
import com.needforspeed.settings.preferences.SecureSettingListPreference;
import com.needforspeed.settings.preferences.SecureSettingSwitchPreference;
import com.needforspeed.settings.preferences.VibrationSeekBarPreference;
import com.needforspeed.settings.kcal.KCalSettingsActivity;
import com.needforspeed.settings.fps.FPSInfoService;

import com.needforspeed.settings.R;

import java.lang.Math.*;

public class NfsDeviceFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String PREF_USB_FASTCHARGE = "fastcharge";
    public static final String USB_FASTCHARGE_PATH = "/sys/kernel/fast_charge/force_fast_charge";

    public static final String PREF_FP_BOOST = "fpboost";
    public static final String FP_BOOST_PATH = "/sys/kernel/fp_boost/enabled";

    public static final String PREF_FSYNC = "fsync";
    public static final String FSYNC_PATH = "/sys/kernel/dyn_fsync/Dyn_fsync_active";

    public static final String PREF_KEY_FPS_INFO = "fps_info";

    private static final String PREF_DEVICE_KCAL = "device_kcal";

    private static Context mContext;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.nfs_main_activity, rootKey);
        mContext = this.getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        SecureSettingSwitchPreference charge = (SecureSettingSwitchPreference) findPreference(PREF_USB_FASTCHARGE);
        charge.setEnabled(Fastcharge.isSupported());
        charge.setChecked(Fastcharge.isCurrentlyEnabled(this.getContext()));
        charge.setOnPreferenceChangeListener(new Fastcharge(getContext()));

        SecureSettingSwitchPreference fp = (SecureSettingSwitchPreference) findPreference(PREF_FP_BOOST);
        fp.setEnabled(FpBoost.isSupported());
        fp.setChecked(FpBoost.isCurrentlyEnabled(this.getContext()));
        fp.setOnPreferenceChangeListener(new FpBoost(getContext()));

        SecureSettingSwitchPreference dyn = (SecureSettingSwitchPreference) findPreference(PREF_FSYNC);
        dyn.setEnabled(DynFsync.isSupported());
        dyn.setChecked(DynFsync.isCurrentlyEnabled(this.getContext()));
        dyn.setOnPreferenceChangeListener(new DynFsync(getContext()));

        SecureSettingSwitchPreference fpsInfo = (SecureSettingSwitchPreference) findPreference(PREF_KEY_FPS_INFO);
        fpsInfo.setChecked(prefs.getBoolean(PREF_KEY_FPS_INFO, false));
        fpsInfo.setOnPreferenceChangeListener(this);

        PreferenceScreen kcal = findPreference(PREF_DEVICE_KCAL);

        kcal.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), KCalSettingsActivity.class);
            startActivity(intent);
            return true;
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        switch (key) {

            case PREF_KEY_FPS_INFO:
                boolean enabled = (Boolean) value;
                Intent fpsinfo = new Intent(this.getContext(), FPSInfoService.class);
                if (enabled) {
                    this.getContext().startService(fpsinfo);
                } else {
                    this.getContext().stopService(fpsinfo);
                }
                break;
        }
        return true;
    }

    private boolean isAppNotInstalled(String uri) {
        PackageManager packageManager = getContext().getPackageManager();
        try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }
}
