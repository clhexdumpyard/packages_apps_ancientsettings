/*
 * Copyright (C) 2018 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancient.settings.fragments;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.preference.*;

import android.widget.Toast;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

import com.android.internal.util.hwkeys.ActionConstants;
import com.android.internal.util.hwkeys.ActionUtils;

import com.ancient.settings.preferences.CustomSeekBarPreference;
import com.ancient.settings.preferences.ActionFragment;

public class Buttons extends SettingsPreferenceFragment implements
    Preference.OnPreferenceChangeListener {

    // category keys
    private static final String CATEGORY_BACK = "back_key";
    private static final String CATEGORY_HOME = "home_key";
    private static final String CATEGORY_MENU = "menu_key";
    private static final String CATEGORY_ASSIST = "assist_key";
    private static final String CATEGORY_APPSWITCH = "app_switch_key";
    private static final String CATEGORY_VOLUME = "volume_keys";
    private static final String CATEGORY_POWER = "power_key";
     // Masks for checking presence of hardware keys.
    // Must match values in frameworks/base/core/res/res/values/config.xml
    public static final int KEY_MASK_HOME = 0x01;
    public static final int KEY_MASK_BACK = 0x02;
    public static final int KEY_MASK_MENU = 0x04;
    public static final int KEY_MASK_ASSIST = 0x08;
    public static final int KEY_MASK_APP_SWITCH = 0x10;
    public static final int KEY_MASK_CAMERA = 0x20;
    public static final int KEY_MASK_VOLUME = 0x40;

    private static final String TORCH_POWER_BUTTON_GESTURE = "torch_power_button_gesture";

    private ListPreference mTorchPowerButton;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.ancient_settings_buttons);
        PreferenceScreen prefSet = getPreferenceScreen();
        final Resources res = getResources();
        final PreferenceScreen prefScreen = getPreferenceScreen();

         // bits for hardware keys present on device
        final int deviceKeys = getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);
         // read bits for present hardware keys
        final boolean hasHomeKey = (deviceKeys & KEY_MASK_HOME) != 0;
        final boolean hasBackKey = (deviceKeys & KEY_MASK_BACK) != 0;
        final boolean hasMenuKey = (deviceKeys & KEY_MASK_MENU) != 0;
        final boolean hasAssistKey = (deviceKeys & KEY_MASK_ASSIST) != 0;
        final boolean hasAppSwitchKey = (deviceKeys & KEY_MASK_APP_SWITCH) != 0;
         // load categories and init/remove preferences based on device
        // configuration
        final PreferenceCategory backCategory = (PreferenceCategory) prefScreen
                .findPreference(CATEGORY_BACK);
        final PreferenceCategory homeCategory = (PreferenceCategory) prefScreen
                .findPreference(CATEGORY_HOME);
        final PreferenceCategory menuCategory = (PreferenceCategory) prefScreen
                .findPreference(CATEGORY_MENU);
        final PreferenceCategory assistCategory = (PreferenceCategory) prefScreen
                .findPreference(CATEGORY_ASSIST);
        final PreferenceCategory appSwitchCategory = (PreferenceCategory) prefScreen
                .findPreference(CATEGORY_APPSWITCH);
         // back key
        if (!hasBackKey) {
            prefScreen.removePreference(backCategory);
        }
         // home key
        if (!hasHomeKey) {
            prefScreen.removePreference(homeCategory);
        }
         // App switch key (recents)
        if (!hasAppSwitchKey) {
            prefScreen.removePreference(appSwitchCategory);
        }
         // menu key
        if (!hasMenuKey) {
            prefScreen.removePreference(menuCategory);
        }
         // search/assist key
        if (!hasAssistKey) {
            prefScreen.removePreference(assistCategory);
        }
         // let super know we can load ActionPreferences
        onPreferenceScreenLoaded(ActionConstants.getDefaults(ActionConstants.HWKEYS));

        // screen off torch
        mTorchPowerButton = (ListPreference) findPreference(TORCH_POWER_BUTTON_GESTURE);
        int mTorchPowerButtonValue = Settings.System.getInt(resolver,
                Settings.System.TORCH_POWER_BUTTON_GESTURE, 0);
        mTorchPowerButton.setValue(Integer.toString(mTorchPowerButtonValue));
        mTorchPowerButton.setSummary(mTorchPowerButton.getEntry());
        mTorchPowerButton.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mTorchPowerButton) {
            int mTorchPowerButtonValue = Integer.valueOf((String) newValue);
            int index = mTorchPowerButton.findIndexOfValue((String) newValue);
            mTorchPowerButton.setSummary(
                    mTorchPowerButton.getEntries()[index]);
            Settings.System.putInt(resolver, Settings.System.TORCH_POWER_BUTTON_GESTURE,
                    mTorchPowerButtonValue);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }
}
