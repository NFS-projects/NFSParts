/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2018 The LineageOS Project
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

import android.content.SharedPreferences;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.provider.Settings;
import androidx.preference.PreferenceManager;

import java.lang.Math.*;

import com.needforspeed.settings.soundcontrol.SoundControlSettings;
import com.needforspeed.settings.soundcontrol.SoundControlFileUtils;
import com.needforspeed.settings.dirac.DiracUtils;
import com.needforspeed.settings.doze.DozeUtils;
import com.needforspeed.settings.kcal.Utils;
import com.needforspeed.settings.torch.TorchSettings;
import com.needforspeed.settings.torch.TorchUtils;
import com.needforspeed.settings.vibration.VibrationFragment;
import com.needforspeed.settings.vibration.VibrationUtils;
import com.needforspeed.settings.fps.FPSInfoService;

public class BootCompletedReceiver extends BroadcastReceiver implements Utils {

    private static final boolean DEBUG = false;
     private static final String TAG = "NFSParts";

    @Override
    public void onReceive(final Context context, Intent intent) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (Settings.Secure.getInt(context.getContentResolver(), PREF_ENABLED, 0) == 1) {
            FileUtils.setValue(KCAL_ENABLE, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_ENABLED, 0));

            String rgbValue = Settings.Secure.getInt(context.getContentResolver(),
                    PREF_RED, RED_DEFAULT) + " " +
                    Settings.Secure.getInt(context.getContentResolver(), PREF_GREEN,
                            GREEN_DEFAULT) + " " +
                    Settings.Secure.getInt(context.getContentResolver(), PREF_BLUE,
                            BLUE_DEFAULT);

            FileUtils.setValue(KCAL_RGB, rgbValue);
            FileUtils.setValue(KCAL_MIN, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_MINIMUM, MINIMUM_DEFAULT));
            FileUtils.setValue(KCAL_SAT, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_GRAYSCALE, 0) == 1 ? 128 :
                    Settings.Secure.getInt(context.getContentResolver(),
                            PREF_SATURATION, SATURATION_DEFAULT) + SATURATION_OFFSET);
            FileUtils.setValue(KCAL_VAL, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_VALUE, VALUE_DEFAULT) + VALUE_OFFSET);
            FileUtils.setValue(KCAL_CONT, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_CONTRAST, CONTRAST_DEFAULT) + CONTRAST_OFFSET);
            FileUtils.setValue(KCAL_HUE, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_HUE, HUE_DEFAULT));
        }
        if (DEBUG) Log.d(TAG, "Received boot completed intent");
            DozeUtils.checkDozeService(context);
            new DiracUtils(context).onBootCompleted();

        int gain = Settings.Secure.getInt(context.getContentResolver(),
                SoundControlSettings.PREF_HEADPHONE_GAIN, 0);
        SoundControlFileUtils.setValue(SoundControlSettings.HEADPHONE_GAIN_PATH, gain + " " + gain);
        SoundControlFileUtils.setValue(SoundControlSettings.MICROPHONE_GAIN_PATH, Settings.Secure.getInt(context.getContentResolver(),
                SoundControlSettings.PREF_MICROPHONE_GAIN, 0));

        VibrationUtils.setValue(VibrationFragment.PREF_VIBRATION_PATH, Settings.Secure.getInt(context.getContentResolver(),
                VibrationFragment.PREF_VIBRATION_OVERRIDE, 0));
        VibrationUtils.setValue(VibrationFragment.SYSTEM_VIBRATION_STRENGTH_PATH, Settings.Secure.getInt(
                context.getContentResolver(), VibrationFragment.PREF_SYSTEM_VIBRATION_STRENGTH, 80) / 100.0 * (VibrationFragment.MAX_VIBRATION - VibrationFragment.MIN_VIBRATION) + VibrationFragment.MIN_VIBRATION);
        VibrationUtils.setValue(VibrationFragment.VIBRATION_NOTIFICATION_PATH, Settings.Secure.getInt(
                context.getContentResolver(), VibrationFragment.PREF_VIBRATION_NOTIFICATION_STRENGTH, 80) / 100.0 * (VibrationFragment.MAX_VIBRATION - VibrationFragment.MIN_VIBRATION) + VibrationFragment.MIN_VIBRATION);
        VibrationUtils.setValue(VibrationFragment.VIBRATION_CALL_PATH, Settings.Secure.getInt(
                context.getContentResolver(), VibrationFragment.PREF_VIBRATION_CALL_STRENGTH, 80) / 100.0 * (VibrationFragment.MAX_VIBRATION - VibrationFragment.MIN_VIBRATION) + VibrationFragment.MIN_VIBRATION);

        FileUtils.setValue(NfsDeviceFragment.USB_FASTCHARGE_PATH, Settings.Secure.getInt(context.getContentResolver(),
                NfsDeviceFragment.PREF_USB_FASTCHARGE, 0));

        FileUtils.setValue(NfsDeviceFragment.FP_BOOST_PATH, Settings.Secure.getInt(context.getContentResolver(),
                NfsDeviceFragment.PREF_FP_BOOST, 0));

        FileUtils.setValue(NfsDeviceFragment.FSYNC_PATH, Settings.Secure.getInt(context.getContentResolver(),
                NfsDeviceFragment.PREF_FSYNC, 0));

        TorchUtils.setValue(TorchSettings.TORCH_1_BRIGHTNESS_PATH,
                Settings.Secure.getInt(context.getContentResolver(),
                        TorchSettings.KEY_WHITE_TORCH_BRIGHTNESS, 100));
        TorchUtils.setValue(TorchSettings.TORCH_2_BRIGHTNESS_PATH,
                Settings.Secure.getInt(context.getContentResolver(),
                        TorchSettings.KEY_YELLOW_TORCH_BRIGHTNESS, 100));

        boolean enabled = sharedPrefs.getBoolean(NfsDeviceFragment.PREF_KEY_FPS_INFO, false);
        if (enabled) {
            context.startService(new Intent(context, FPSInfoService.class));
        }
    }
}
