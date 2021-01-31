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

import static android.view.WindowManagerPolicyConstants.NAV_BAR_MODE_GESTURAL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.Vibrator;
import android.os.SystemProperties;
import android.provider.Settings;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto; 
import com.android.internal.util.hwkeys.ActionUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.ancient.settings.preferences.SystemSettingListPreference;
import com.ancient.settings.preferences.SystemSettingSwitchPreference;

public class Navigation extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "Navigation";
    private static final String NAVBAR_VISIBILITY = "navbar_visibility";
    private static final String LAYOUT_SETTINGS = "navbar_layout_views";
    private static final String NAVIGATION_BAR_INVERSE = "navbar_inverse_layout";
    private static final String PULSE_CATEGORY = "pulse_category";

    private SwitchPreference mNavbarVisibility;
    private Preference mLayoutSettings;
    private SwitchPreference mSwapNavButtons;
    private Preference mPulse;

    private boolean mIsNavSwitchingMode = false;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ancient_settings_navigation);
        final Resources res = getResources();

        mNavbarVisibility = (SwitchPreference) findPreference(NAVBAR_VISIBILITY);

        boolean showing = Settings.System.getInt(getContentResolver(),
                Settings.System.FORCE_SHOW_NAVBAR,
                ActionUtils.hasNavbarByDefault(getActivity()) ? 1 : 0) != 0;
        updateBarVisibleAndUpdatePrefs(showing);
        mNavbarVisibility.setOnPreferenceChangeListener(this);

        mLayoutSettings = findPreference(LAYOUT_SETTINGS);
        mSwapNavButtons = findPreference(NAVIGATION_BAR_INVERSE);
        int navMode = res.getInteger(
                com.android.internal.R.integer.config_navBarInteractionMode);
        if (navMode == NAV_BAR_MODE_GESTURAL) {
            mSwapNavButtons.setEnabled(false);
            mSwapNavButtons.setSummary(R.string.navbar_gesture_enabled);
            mLayoutSettings.setEnabled(false);
            mLayoutSettings.setSummary(R.string.navbar_gesture_enabled);
        }

        mPulse = findPreference(PULSE_CATEGORY);
        if (!getResources().getBoolean(R.bool.pulse_category_isVisible)) {
            getPreferenceScreen().removePreference(mPulse);
            mPulse = null;
        } else {
            updatePulseEnablement(showing);
        }

        mHandler = new Handler();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mNavbarVisibility)) {
            if (mIsNavSwitchingMode) {
                return false;
            }
            mIsNavSwitchingMode = true;
            boolean showing = ((Boolean)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.FORCE_SHOW_NAVBAR,
                    showing ? 1 : 0);
            updateBarVisibleAndUpdatePrefs(showing);
            updatePulseEnablement(showing);
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

    private void updateBarVisibleAndUpdatePrefs(boolean showing) {
        mNavbarVisibility.setChecked(showing);
    }

    private void updatePulseEnablement(boolean navBarShowing) {
        if (mPulse == null) return;
        if (!navBarShowing) {
            mPulse.setEnabled(false);
            mPulse.setSummary(R.string.pulse_unavailable_no_navbar);
        } else {
            mPulse.setEnabled(true);
            mPulse.setSummary(R.string.pulse_settings_summary);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }
}
