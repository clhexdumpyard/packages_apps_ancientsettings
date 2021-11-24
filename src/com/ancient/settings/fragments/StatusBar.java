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

import static android.os.UserHandle.USER_CURRENT;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.content.om.IOverlayManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;

import androidx.preference.*;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.ancient.settings.preferences.SystemSettingSwitchPreference;

import com.android.internal.util.ancient.AncientUtils;
import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.util.ancient.AncientUtils;
import com.ancient.settings.preferences.SystemSettingSwitchPreference;

public class StatusBar extends SettingsPreferenceFragment implements
    Preference.OnPreferenceChangeListener {

    private static final String PREF_STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String PREF_STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String COMBINE_STATUSBAR_SIGNAL = "combine_statusbar_signal";
    private static final String KEY_STATUS_BAR_LOGO = "status_bar_logo";
    private static final String CHARGING_BLEND_COLOR = "CHARGING_BLEND_COLOR";
    private static final String FILL_BLEND_COLOR = "FILL_BLEND_COLOR";
    private static final String CUSTOM_BLEND_COLOR = "CUSTOM_BLEND_COLOR";
    
    private static final String CHARGING_BLEND_OVERLAY = "charging.blend.on";
    private static final String FILL_BLEND_OVERLAY = "fill.blend.on"; 
    private static final String CUSTOM_BLEND_OVERLAY = "custom.blend.on";     

    private static final int BATTERY_STYLE_PORTRAIT = 0;
    private static final int BATTERY_STYLE_TEXT = 19;
    private static final int BATTERY_STYLE_HIDDEN = 20;
    private static final int BATTERY_PERCENT_HIDDEN = 0;

    private ListPreference mBatteryPercent;
    private ListPreference mBatteryStyle;
    private int mBatteryPercentValue;
    private SystemSettingSwitchPreference mCombineStatusbarSignal;
    private SwitchPreference mShowAncientLogo;
    private ColorPickerPreference mblendCC;
    private ColorPickerPreference mblendFC;
    private SystemSettingSwitchPreference mblendSwitch;
    
    private IOverlayManager mOverlayService;  

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.ancient_settings_statusbar);
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
        
        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));

        int batterystyle = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_STYLE, BATTERY_STYLE_PORTRAIT, UserHandle.USER_CURRENT);

        mBatteryStyle = (ListPreference) findPreference(PREF_STATUS_BAR_BATTERY_STYLE);
        mBatteryStyle.setValue(String.valueOf(batterystyle));
        mBatteryStyle.setSummary(mBatteryStyle.getEntry());
        mBatteryStyle.setOnPreferenceChangeListener(this);

        mBatteryPercentValue = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, BATTERY_PERCENT_HIDDEN, UserHandle.USER_CURRENT);
        mBatteryPercent = (ListPreference) findPreference(PREF_STATUS_BAR_SHOW_BATTERY_PERCENT);
        mBatteryPercent.setValue(String.valueOf(mBatteryPercentValue));
        mBatteryPercent.setSummary(mBatteryPercent.getEntry());
        mBatteryPercent.setOnPreferenceChangeListener(this);

        mBatteryPercent.setEnabled(
                batterystyle != BATTERY_STYLE_TEXT && batterystyle != BATTERY_STYLE_HIDDEN);

        mCombineStatusbarSignal = (SystemSettingSwitchPreference) findPreference(COMBINE_STATUSBAR_SIGNAL);
        mCombineStatusbarSignal.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.COMBINE_STATUSBAR_SIGNAL, 0) == 1));
        mCombineStatusbarSignal.setOnPreferenceChangeListener(this);

        mShowAncientLogo = (SwitchPreference) findPreference(KEY_STATUS_BAR_LOGO);
        mShowAncientLogo.setChecked((Settings.System.getInt(getContentResolver(),
        Settings.System.STATUS_BAR_LOGO, 0) == 1));
        mShowAncientLogo.setOnPreferenceChangeListener(this);
        
        mblendCC = (ColorPickerPreference) findPreference(CHARGING_BLEND_COLOR);
        int blendCC = Settings.System.getInt(getContentResolver(),
                "CHARGING_BLEND_COLOR", 0xFF00FF00);
        mblendCC.setNewPreviewColor(blendCC);
        mblendCC.setAlphaSliderEnabled(true);
        String blendCCHex = String.format("#%08x", (0xFF00FF00 & blendCC));
        if (blendCCHex.equals("#FF00FF00")) {
            mblendCC.setSummary(R.string.color_default);
        } else {
            mblendCC.setSummary(blendCCHex);
        }
        mblendCC.setOnPreferenceChangeListener(this);
        
        mblendFC = (ColorPickerPreference) findPreference(FILL_BLEND_COLOR);
        int blendFC = Settings.System.getInt(getContentResolver(),
                "FILL_BLEND_COLOR", 0xFF00FF00);
        mblendFC.setNewPreviewColor(blendFC);
        mblendFC.setAlphaSliderEnabled(true);
        String blendFCHex = String.format("#%08x", (0xFF00FF00 & blendFC));
        if (blendFCHex.equals("#FF00FF00")) {
            mblendFC.setSummary(R.string.color_default);
        } else {
            mblendFC.setSummary(blendCCHex);
        }
        mblendFC.setOnPreferenceChangeListener(this);  
        
        mblendSwitch = (SystemSettingSwitchPreference) findPreference(CUSTOM_BLEND_COLOR);
        mblendSwitch.setChecked((Settings.System.getInt(getContentResolver(),
        "CUSTOM_BLEND_COLOR", 0) == 1));
        mblendSwitch.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBatteryStyle) {
            int batterystyle = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, batterystyle,
                UserHandle.USER_CURRENT);
            int index = mBatteryStyle.findIndexOfValue((String) newValue);
            mBatteryStyle.setSummary(mBatteryStyle.getEntries()[index]);
            mBatteryPercent.setEnabled(
                    batterystyle != BATTERY_STYLE_TEXT && batterystyle != BATTERY_STYLE_HIDDEN);
            return true;
        } else if (preference == mBatteryPercent) {
            mBatteryPercentValue = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, mBatteryPercentValue,
                    UserHandle.USER_CURRENT);
            int index = mBatteryPercent.findIndexOfValue((String) newValue);
            mBatteryPercent.setSummary(mBatteryPercent.getEntries()[index]);
            return true;
        } else if (preference == mCombineStatusbarSignal) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.COMBINE_STATUSBAR_SIGNAL, value ? 1 : 0);
            AncientUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if  (preference == mShowAncientLogo) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_LOGO, value ? 1 : 0);
            return true;
        } else if (preference == mblendCC) {
            String blendCC = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (blendCC.equals("#FF00FF00")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(blendCC);
            }
            int intblendCC = ColorPickerPreference.convertToColorInt(blendCC);
            Settings.System.putInt(getContentResolver(),
                    "CHARGING_BLEND_COLOR", intblendCC);
            try {
                mOverlayService.setEnabled(CHARGING_BLEND_OVERLAY, false, USER_CURRENT);    
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
            return true;   
        } else if (preference == mblendFC) {
            String blendFC = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (blendFC.equals("#FF00FF00")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(blendFC);
            }
            int intblendFC = ColorPickerPreference.convertToColorInt(blendFC);
            Settings.System.putInt(getContentResolver(),
                    "FILL_BLEND_COLOR", intblendFC);
            try {
                mOverlayService.setEnabledExclusiveInCategory(CHARGING_BLEND_OVERLAY, USER_CURRENT);    
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
            return true; 
        } else if  (preference == mblendSwitch) {
            boolean valuecrot = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    "CUSTOM_BLEND_COLOR", valuecrot ? 1 : 0);
            if (valuecrot == false) {
                   try {
                      mOverlayService.setEnabled(CUSTOM_BLEND_OVERLAY, false, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
            } else if (valuecrot == true) {
                   try {
                       mOverlayService.setEnabledExclusiveInCategory(CUSTOM_BLEND_OVERLAY, USER_CURRENT);    
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }
}
