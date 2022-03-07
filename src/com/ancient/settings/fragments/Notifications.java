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
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto; 
import com.android.internal.util.ancient.AncientUtils;

import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.ancient.settings.preferences.SystemSettingListPreference;
import com.ancient.settings.preferences.SystemSettingSwitchPreference;
import com.ancient.settings.preferences.CustomSeekBarPreference;
import com.ancient.settings.preferences.SystemSettingMasterSwitchPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.android.internal.util.ancient.AncientUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class Notifications extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    public static final String TAG = "Notifications";
    private static final String NOTIFICATION_MATERIAL_DISMISS_COLOR_STYLE = "NOTIFICATION_MATERIAL_DISMISS_COLOR_STYLE";
    private static final String NOTIFICATION_MATERIAL_DISMISS_COLOR_BGSTYLE = "NOTIFICATION_MATERIAL_DISMISS_COLOR_BGSTYLE";
    private static final String STATUSBAR_ICONS_STYLE = "STATUSBAR_ICONS_STYLE";
    private static final String HEADER_ICONS_STYLE = "HEADER_ICONS_STYLE";
    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String KEY_EDGE_LIGHTNING = "pulse_ambient_light";
    private static final String PREF_FLASH_ON_CALL = "flashlight_on_call";
    private static final String PREF_FLASH_ON_CALL_DND = "flashlight_on_call_ignore_dnd";
    private static final String PREF_FLASH_ON_CALL_RATE = "flashlight_on_call_rate";

    private Preference mChargingLeds;
    private ColorPickerPreference mCColorIc;
    private ColorPickerPreference mCColorBg;
    private SystemSettingSwitchPreference mSBColorIc;
    private SystemSettingSwitchPreference mHEADColorIc;
    private SystemSettingMasterSwitchPreference mEdgeLightning;
    private CustomSeekBarPreference mFlashOnCallRate;
    private SystemSettingListPreference mFlashOnCall;
    private SystemSettingSwitchPreference mFlashOnCallIgnoreDND;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ancient_settings_notifications);
        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!AncientUtils.isVoiceCapable(getActivity())) {
                prefScreen.removePreference(incallVibCategory);
        }

        mFlashOnCallRate = (CustomSeekBarPreference)
                findPreference(PREF_FLASH_ON_CALL_RATE);
        int value = Settings.System.getInt(resolver,
                Settings.System.FLASHLIGHT_ON_CALL_RATE, 1);
        mFlashOnCallRate.setValue(value);
        mFlashOnCallRate.setOnPreferenceChangeListener(this);

        mFlashOnCallIgnoreDND = (SystemSettingSwitchPreference)
                findPreference(PREF_FLASH_ON_CALL_DND);
        value = Settings.System.getInt(resolver,
                Settings.System.FLASHLIGHT_ON_CALL, 0);
        mFlashOnCallIgnoreDND.setVisible(value > 1);
        mFlashOnCallRate.setVisible(value != 0);

        mFlashOnCall = (SystemSettingListPreference)
                findPreference(PREF_FLASH_ON_CALL);
        mFlashOnCall.setSummary(mFlashOnCall.getEntries()[value]);
        mFlashOnCall.setOnPreferenceChangeListener(this);

        mChargingLeds = (Preference) findPreference("charging_light");
        if (mChargingLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            prefScreen.removePreference(mChargingLeds);
        }
        
        mCColorIc = (ColorPickerPreference) findPreference(NOTIFICATION_MATERIAL_DISMISS_COLOR_STYLE);
        int mbacaColorpikas = Settings.System.getInt(getContentResolver(),
                "NOTIFICATION_MATERIAL_DISMISS_COLOR_STYLE", 0xFFFF0000);
        mCColorIc.setNewPreviewColor(mbacaColorpikas);
        mCColorIc.setAlphaSliderEnabled(true);
        String mbacaColorHexpikas = String.format("#%08x", (0xFFFF0000 & mbacaColorpikas));
        if (mbacaColorHexpikas.equals("#ffff0000")) {
            mCColorIc.setSummary(R.string.color_default);
        } else {
            mCColorIc.setSummary(mbacaColorHexpikas);
        }
        mCColorIc.setOnPreferenceChangeListener(this);

        mCColorBg = (ColorPickerPreference) findPreference(NOTIFICATION_MATERIAL_DISMISS_COLOR_BGSTYLE);
        int mbacaColorpikasa = Settings.System.getInt(getContentResolver(),
                "NOTIFICATION_MATERIAL_DISMISS_COLOR_BGSTYLE", 0xFFFF0000);
        mCColorBg.setNewPreviewColor(mbacaColorpikasa);
        mCColorBg.setAlphaSliderEnabled(true);
        String mbacaColorHexpikasa = String.format("#%08x", (0xFFFF0000 & mbacaColorpikasa));
        if (mbacaColorHexpikasa.equals("#ffff0000")) {
            mCColorBg.setSummary(R.string.color_default);
        } else {
            mCColorBg.setSummary(mbacaColorHexpikasa);
        }
        mCColorBg.setOnPreferenceChangeListener(this);
        
        mSBColorIc = (SystemSettingSwitchPreference) findPreference(STATUSBAR_ICONS_STYLE);
        mSBColorIc.setOnPreferenceChangeListener(this);
        
         mHEADColorIc = (SystemSettingSwitchPreference) findPreference(HEADER_ICONS_STYLE);
        mHEADColorIc.setOnPreferenceChangeListener(this);

        mEdgeLightning = (SystemSettingMasterSwitchPreference)
                findPreference(KEY_EDGE_LIGHTNING);
        boolean enabled = Settings.System.getIntForUser(resolver,
                KEY_EDGE_LIGHTNING, 0, UserHandle.USER_CURRENT) == 1;
        mEdgeLightning.setChecked(enabled);
        mEdgeLightning.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mCColorIc) {
            String hexapikas = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hexapikas.equals("#FFFF0000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexapikas);
            }
            int intHexapikas = ColorPickerPreference.convertToColorInt(hexapikas);
            Settings.System.putInt(getContentResolver(),
                    "NOTIFICATION_MATERIAL_DISMISS_COLOR_STYLE", intHexapikas);
            return true;  
        } else if (preference == mCColorBg) {
            String hexapikasa = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hexapikasa.equals("#FFFF0000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexapikasa);
            }
            int intHexapikasa = ColorPickerPreference.convertToColorInt(hexapikasa);
            Settings.System.putInt(getContentResolver(),
                    "NOTIFICATION_MATERIAL_DISMISS_COLOR_BGSTYLE", intHexapikasa);
            return true; 
        } else if (preference == mSBColorIc) {
            AncientUtils.showSystemUiRestartDialog(getContext());
            return true; 
        } else if (preference == mHEADColorIc) {
            AncientUtils.showSystemUiRestartDialog(getContext());
            return true; 
        } else if (preference == mFlashOnCall) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putInt(resolver,
                    Settings.System.FLASHLIGHT_ON_CALL, value);
            mFlashOnCall.setSummary(mFlashOnCall.getEntries()[value]);
            mFlashOnCallIgnoreDND.setVisible(value > 1);
            mFlashOnCallRate.setVisible(value != 0);
            return true;
        } else if (preference == mFlashOnCallRate) {
            int value = (Integer) objValue;
            Settings.System.putInt(resolver,
                    Settings.System.FLASHLIGHT_ON_CALL_RATE, value);
            return true;
        } else if (preference == mEdgeLightning) {
            boolean value = (Boolean) objValue;
            Settings.System.putIntForUser(resolver, KEY_EDGE_LIGHTNING,
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }

    /**
     * For Search.
     */

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.ancient_settings_notifications);
}
