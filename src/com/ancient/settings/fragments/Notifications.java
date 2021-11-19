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

import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.ancient.settings.preferences.SystemSettingListPreference;
import com.ancient.settings.preferences.SystemSettingSwitchPreference;
import com.ancient.settings.preferences.CustomSeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Notifications extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    public static final String TAG = "Notifications";
    private static final String NOTIFICATION_MATERIAL_DISMISS_COLOR_STYLE = "NOTIFICATION_MATERIAL_DISMISS_COLOR_STYLE";
    private static final String NOTIFICATION_MATERIAL_DISMISS_COLOR_BGSTYLE = "NOTIFICATION_MATERIAL_DISMISS_COLOR_BGSTYLE";

    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";

    private Preference mChargingLeds;
    private ColorPickerPreference mCColorIc;
    private ColorPickerPreference mCColorBg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ancient_settings_notifications);
        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!AncientUtils.isVoiceCapable(getActivity())) {
                prefSet.removePreference(incallVibCategory);
        }

        mChargingLeds = (Preference) findPreference("charging_light");
        if (mChargingLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            prefSet.removePreference(mChargingLeds);
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

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.ancient_settings_notifications;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}
