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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto; 

import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.ancient.settings.preferences.CustomSeekBarPreference;
import com.ancient.settings.preferences.SystemSettingListPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Misc extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    public static final String TAG = "Misc";

    private static final String RINGTONE_FOCUS_MODE = "ringtone_focus_mode";
    private static final String KEY_PULSE_BRIGHTNESS = "ambient_pulse_brightness";
    private static final String KEY_DOZE_BRIGHTNESS = "ambient_doze_brightness";
    private static final String ANCIENT_SLIDER_TINT = "ANCIENT_SLIDER_TINT";
    private static final String ANCIENT_THUMB_TINT = "ANCIENT_THUMB_TINT";
    private static final String ANCIENT_SLIDERBG_TINT = "ANCIENT_SLIDERBG_TINT";
    private static final String ANCIENT_VICON_TINT = "ANCIENT_VICON_TINT";
    private static final String ANCIENT_PERSEN_TINT = "ANCIENT_PERSEN_TINT";

    private ListPreference mHeadsetRingtoneFocus;
    private CustomSeekBarPreference mPulseBrightness;
    private CustomSeekBarPreference mDozeBrightness;
    private ColorPickerPreference rgbBrightPicker;
    private ColorPickerPreference rgbThumbPicker;
    private ColorPickerPreference rgbBPPicker;
    private ColorPickerPreference rgbPersenPicker;
    private ColorPickerPreference rgbIconPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ancient_settings_misc);
        final ContentResolver resolver = getActivity().getContentResolver();
        
        rgbIconPicker = (ColorPickerPreference) findPreference(ANCIENT_VICON_TINT);
        int mbacaColorpi = Settings.System.getInt(getContentResolver(),
                "ANCIENT_VICON_TINT", 0xFFFF0000);
        rgbIconPicker.setNewPreviewColor(mbacaColorpi);
        rgbIconPicker.setAlphaSliderEnabled(true);
        String mbacaColorHexpi = String.format("#%08x", (0xFFFF0000 & mbacaColorpi));
        if (mbacaColorHexpi.equals("#ffff0000")) {
            rgbIconPicker.setSummary(R.string.color_default);
        } else {
            rgbIconPicker.setSummary(mbacaColorHexpi);
        }
        rgbIconPicker.setOnPreferenceChangeListener(this);
        
        rgbPersenPicker = (ColorPickerPreference) findPreference(ANCIENT_PERSEN_TINT);
        int mbacaColorp = Settings.System.getInt(getContentResolver(),
                "ANCIENT_PERSEN_TINT", 0xFFFF0000);
        rgbPersenPicker.setNewPreviewColor(mbacaColorp);
        rgbPersenPicker.setAlphaSliderEnabled(true);
        String mbacaColorHexp = String.format("#%08x", (0xFFFF0000 & mbacaColorp));
        if (mbacaColorHexp.equals("#ffff0000")) {
            rgbPersenPicker.setSummary(R.string.color_default);
        } else {
            rgbPersenPicker.setSummary(mbacaColorHexp);
        }
        rgbPersenPicker.setOnPreferenceChangeListener(this);

        rgbBPPicker = (ColorPickerPreference) findPreference(ANCIENT_SLIDERBG_TINT);
        int mbacaColor = Settings.System.getInt(getContentResolver(),
                "ANCIENT_SLIDERBG_TINT", 0xFFFF0000);
        rgbBPPicker.setNewPreviewColor(mbacaColor);
        rgbBPPicker.setAlphaSliderEnabled(true);
        String mbacaColorHex = String.format("#%08x", (0xFFFF0000 & mbacaColor));
        if (mbacaColorHex.equals("#ffff0000")) {
            rgbBPPicker.setSummary(R.string.color_default);
        } else {
            rgbBPPicker.setSummary(mbacaColorHex);
        }
        rgbBPPicker.setOnPreferenceChangeListener(this);
                
        rgbThumbPicker = (ColorPickerPreference) findPreference(ANCIENT_THUMB_TINT);
        int mbacaColore = Settings.System.getInt(getContentResolver(),
                "ANCIENT_THUMB_TINT", 0xFFFF0000);
        rgbThumbPicker.setNewPreviewColor(mbacaColore);
        rgbThumbPicker.setAlphaSliderEnabled(true);
        String mbacaColoreHex = String.format("#%08x", (0xFFFF0000 & mbacaColore));
        if (mbacaColoreHex.equals("#ffff0000")) {
            rgbThumbPicker.setSummary(R.string.color_default);
        } else {
            rgbThumbPicker.setSummary(mbacaColoreHex);
        }
        rgbThumbPicker.setOnPreferenceChangeListener(this);   
        
        rgbBrightPicker = (ColorPickerPreference) findPreference(ANCIENT_SLIDER_TINT);
        int mbacaColored = Settings.System.getInt(getContentResolver(),
                "ANCIENT_SLIDER_TINT", 0xFFFF0000);
        rgbBrightPicker.setNewPreviewColor(mbacaColored);
        rgbBrightPicker.setAlphaSliderEnabled(true);
        String mbacaColoredHex = String.format("#%08x", (0xFFFF0000 & mbacaColored));
        if (mbacaColoredHex.equals("#ffff0000")) {
            rgbBrightPicker.setSummary(R.string.color_default);
        } else {
            rgbBrightPicker.setSummary(mbacaColoredHex);
        }
        rgbBrightPicker.setOnPreferenceChangeListener(this);   

        mHeadsetRingtoneFocus = (ListPreference) findPreference(RINGTONE_FOCUS_MODE);
        int mHeadsetRingtoneFocusValue = Settings.Global.getInt(resolver,
                Settings.Global.RINGTONE_FOCUS_MODE, 0);
        mHeadsetRingtoneFocus.setValue(Integer.toString(mHeadsetRingtoneFocusValue));
        mHeadsetRingtoneFocus.setSummary(mHeadsetRingtoneFocus.getEntry());
        mHeadsetRingtoneFocus.setOnPreferenceChangeListener(this);

        int defaultDoze = getResources().getInteger(
                com.android.internal.R.integer.config_screenBrightnessDoze);
        int defaultPulse = getResources().getInteger(
                com.android.internal.R.integer.config_screenBrightnessPulse);
        if (defaultPulse == -1) {
            defaultPulse = defaultDoze;
        }

        mPulseBrightness = (CustomSeekBarPreference) findPreference(KEY_PULSE_BRIGHTNESS);
        int value = Settings.System.getInt(getContentResolver(),
                Settings.System.PULSE_BRIGHTNESS, defaultPulse);
        mPulseBrightness.setValue(value);
        mPulseBrightness.setOnPreferenceChangeListener(this);

        mDozeBrightness = (CustomSeekBarPreference) findPreference(KEY_DOZE_BRIGHTNESS);
        value = Settings.System.getInt(getContentResolver(),
                Settings.System.DOZE_BRIGHTNESS, defaultDoze);
        mDozeBrightness.setValue(value);
        mDozeBrightness.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mHeadsetRingtoneFocus) {
            int mHeadsetRingtoneFocusValue = Integer.valueOf((String) newValue);
            int index = mHeadsetRingtoneFocus.findIndexOfValue((String) newValue);
            mHeadsetRingtoneFocus.setSummary(
                    mHeadsetRingtoneFocus.getEntries()[index]);
            Settings.Global.putInt(getContentResolver(), Settings.Global.RINGTONE_FOCUS_MODE,
                    mHeadsetRingtoneFocusValue);
            return true;
        } else if (preference == rgbIconPicker) {
            String hexapi = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexapi.equals("#FFFF0000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexapi);
            }
            int intHexapi = ColorPickerPreference.convertToColorInt(hexapi);
            Settings.System.putInt(getContentResolver(),
                    "ANCIENT_VICON_TINT", intHexapi);
            return true;  
        } else if (preference == rgbPersenPicker) {
            String hexaBp = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexaBp.equals("#FFFF0000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexaBp);
            }
            int intHexaBp = ColorPickerPreference.convertToColorInt(hexaBp);
            Settings.System.putInt(getContentResolver(),
                    "ANCIENT_PERSEN_TINT", intHexaBp);
            return true;    
        } else if (preference == rgbBrightPicker) {
            String hexa = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexa.equals("#FFFF0000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexa);
            }
            int intHexa = ColorPickerPreference.convertToColorInt(hexa);
            Settings.System.putInt(getContentResolver(),
                    "ANCIENT_SLIDER_TINT", intHexa);
            return true;  
        } else if (preference == rgbThumbPicker) {
            String hexaB = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexaB.equals("#FFFF0000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexaB);
            }
            int intHexaB = ColorPickerPreference.convertToColorInt(hexaB);
            Settings.System.putInt(getContentResolver(),
                    "ANCIENT_THUMB_TINT", intHexaB);
            return true;    
        } else if (preference == rgbBPPicker) {
            String hexaBA = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexaBA.equals("#FFFF0000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexaBA);
            }
            int intHexaBA = ColorPickerPreference.convertToColorInt(hexaBA);
            Settings.System.putInt(getContentResolver(),
                    "ANCIENT_SLIDERBG_TINT", intHexaBA);    
            return true;           
        } else if (preference == mPulseBrightness) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PULSE_BRIGHTNESS, value);
            return true;
        } else if (preference == mDozeBrightness) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.DOZE_BRIGHTNESS, value);
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
                    sir.xmlResId = R.xml.ancient_settings_misc;
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
