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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.SystemProperties;
import android.provider.Settings;

import androidx.preference.*;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto; 

import com.android.internal.util.ancient.AncientUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.ancient.settings.preferences.SystemSettingSwitchPreference;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Navigation extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "Navigation";

    private static final String KEY_NAVIGATION_BAR_ENABLED = "force_show_navbar";
    private static final String KEY_LAYOUT_SETTINGS = "layout_settings";
    private static final String KEY_NAVIGATION_BAR_ARROWS = "navigation_bar_menu_arrow_keys";

    private Preference mLayoutSettings;
    private SwitchPreference mNavigationBar;
    private SystemSettingSwitchPreference mNavigationArrows;

    private boolean mIsNavSwitchingMode = false;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ancient_settings_navigation);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        final boolean defaultToNavigationBar = getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        final boolean navigationBarEnabled = Settings.System.getIntForUser(
                resolver, Settings.System.FORCE_SHOW_NAVBAR,
                defaultToNavigationBar ? 1 : 0, UserHandle.USER_CURRENT) != 0;

        mNavigationBar = (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_ENABLED);
        mNavigationBar.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FORCE_SHOW_NAVBAR,
                defaultToNavigationBar ? 1 : 0) == 1));
        mNavigationBar.setOnPreferenceChangeListener(this);

        final boolean isThreeButtonNavbarEnabled = AncientUtils.isThemeEnabled("com.android.internal.systemui.navbar.threebutton");
        mLayoutSettings = (Preference) findPreference(KEY_LAYOUT_SETTINGS);
        mLayoutSettings.setEnabled(isThreeButtonNavbarEnabled);

        mNavigationArrows = (SystemSettingSwitchPreference) findPreference(KEY_NAVIGATION_BAR_ARROWS);

        mHandler = new Handler();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNavigationBar) {
            boolean value = (Boolean) newValue;
            if (mIsNavSwitchingMode) {
                return false;
            }
            mIsNavSwitchingMode = true;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FORCE_SHOW_NAVBAR, value ? 1 : 0);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsNavSwitchingMode = false;
                }
            }, 1500);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }
}
