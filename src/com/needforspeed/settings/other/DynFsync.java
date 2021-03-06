/*
* Copyright (C) 2020 ronaxdevil <pratabidya.007@gmail.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.needforspeed.settings.other;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceManager;

import com.needforspeed.settings.R;

import com.needforspeed.settings.other.OtherFragment;

public class DynFsync implements OnPreferenceChangeListener {

    private Context mContext;

    public DynFsync(Context context) {
        mContext = context;
    }

    public static String getFile() {
        if (OtherUtils.fileWritable(OtherFragment.FSYNC_PATH)) {
            return OtherFragment.FSYNC_PATH;
        }
        return null;
    }

    public static boolean isSupported() {
        return OtherUtils.fileWritable(getFile());
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return OtherUtils.getFileValueAsBoolean(getFile(), false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        switch (key) {
            case OtherFragment.PREF_FSYNC:
                OtherUtils.setValue(OtherFragment.FSYNC_PATH, (boolean) value);
                break;

            default:
                break;
        }
        return true;
    }
}
