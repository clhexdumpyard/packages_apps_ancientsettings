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
import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto; 

import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.ancient.settings.preferences.SystemSettingEditTextPreference;
import com.ancient.settings.preferences.SystemSettingMasterSwitchPreference;
import com.ancient.settings.preferences.SystemSettingSwitchPreference;
import com.ancient.settings.preferences.SystemSettingListPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.util.ancient.ThemesUtils;
import com.android.internal.util.ancient.AncientUtils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class QuickSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
    private static final String FOOTER_TEXT_STRING = "footer_text_string";
    private static final String STATUS_BAR_CUSTOM_HEADER = "status_bar_custom_header";
    private static final String PREF_QS_MEDIA_PLAYER = "qs_media_player";
    private static final String QS_BG_FUL = "QS_BG_FUL";
    private static final String QQS_TILE_STYLE = "QQS_TILE_STYLE";
    private static final String PENGUSIR_ALARM = "PENGUSIR_ALARM";
    private static final String PENGUSIR_RINGER = "PENGUSIR_RINGER";
    private static final String ANCIENTBRIGHTNESSCOLOR = "AncientBrightnessColor";
    private static final String ANCIENTTHUMBCOLOR = "AncientThumbColor";
    private static final String ANCIENTBACKPROGRESSCOLOR = "AncientBackProgressColor";
    private static final String QS_BG_GRAD_WARNASATU = "QS_BG_GRAD_WARNASATU";
    private static final String QS_BG_GRAD_WARNADUA = "QS_BG_GRAD_WARNADUA";
    private static final String QS_BG_GRAD_WARNATIGA = "QS_BG_GRAD_WARNATIGA";
    private static final String QS_BG_SATU_WARNA = "QS_BG_SATU_WARNA";
    private static final String QS_BG_SATU_WARNADUA = "QS_BG_SATU_WARNADUA";

    private UiModeManager mUiModeManager;

    private ListPreference mSmartPulldown;
    private SystemSettingEditTextPreference mFooterString;
    private SystemSettingMasterSwitchPreference mCustomHeader;
    private SystemSettingSwitchPreference mQsMedia;
    private SystemSettingSwitchPreference mQsBgFul;
    private SystemSettingSwitchPreference mQsAlarm;
    private SystemSettingSwitchPreference mQsRinger;
    private SystemSettingListPreference mQqsTile;
    private IOverlayManager mOverlayService;
    private ColorPickerPreference rgbBrightPicker;
    private ColorPickerPreference rgbThumbPicker;
    private ColorPickerPreference rgbBPPicker;
    private ColorPickerPreference grad1;
    private ColorPickerPreference grad2;
    private ColorPickerPreference grad3;
    private ColorPickerPreference satuwarna;
    private ColorPickerPreference satuwarna12;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ancient_settings_quicksettings);  
        final ContentResolver resolver = getActivity().getContentResolver();
        mUiModeManager = getContext().getSystemService(UiModeManager.class);
        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
             
        grad1 = (ColorPickerPreference) findPreference(QS_BG_GRAD_WARNASATU);
        int mgradL = Settings.System.getInt(getContentResolver(),
                "QS_BG_GRAD_WARNASATU", 0x11000000);
        grad1.setNewPreviewColor(mgradL);
        grad1.setAlphaSliderEnabled(true);
        String mgradHexaL = String.format("#%08x", (0x11000000 & mgradL));
        if (mgradHexaL.equals("#11000000")) {
            grad1.setSummary(R.string.color_default);
        } else {
            grad1.setSummary(mgradHexaL);
        }
        grad1.setOnPreferenceChangeListener(this);
        
        grad2 = (ColorPickerPreference) findPreference(QS_BG_GRAD_WARNADUA);
        int mgradL2 = Settings.System.getInt(getContentResolver(),
                "QS_BG_GRAD_WARNADUA", 0x11000000);
        grad2.setNewPreviewColor(mgradL2);
        grad2.setAlphaSliderEnabled(true);
        String mgradHexaL2 = String.format("#%08x", (0x11000000 & mgradL2));
        if (mgradHexaL2.equals("#11000000")) {
            grad2.setSummary(R.string.color_default);
        } else {
            grad2.setSummary(mgradHexaL2);
        }
        grad2.setOnPreferenceChangeListener(this);
        
        grad3 = (ColorPickerPreference) findPreference(QS_BG_GRAD_WARNATIGA);
        int mgradL3 = Settings.System.getInt(getContentResolver(),
                "QS_BG_GRAD_WARNATIGA", 0x11000000);
        grad3.setNewPreviewColor(mgradL3);
        grad3.setAlphaSliderEnabled(true);
        String mgradHexaL3 = String.format("#%08x", (0x11000000 & mgradL3));
        if (mgradHexaL3.equals("#11000000")) {
            grad3.setSummary(R.string.color_default);
        } else {
            grad3.setSummary(mgradHexaL3);
        }
        grad3.setOnPreferenceChangeListener(this);
        
        satuwarna12 = (ColorPickerPreference) findPreference(QS_BG_SATU_WARNADUA);
        int mgradL41 = Settings.System.getInt(getContentResolver(),
                "QS_BG_SATU_WARNADUA", 0x11000000);
        satuwarna12.setNewPreviewColor(mgradL41);
        satuwarna12.setAlphaSliderEnabled(true);
        String mgradHexaL41 = String.format("#%08x", (0x11000000 & mgradL41));
        if (mgradHexaL41.equals("#11000000")) {
            satuwarna12.setSummary(R.string.color_default);
        } else {
            satuwarna12.setSummary(mgradHexaL41);
        }
        satuwarna12.setOnPreferenceChangeListener(this);
        
        satuwarna = (ColorPickerPreference) findPreference(QS_BG_SATU_WARNA);
        int mgradL4 = Settings.System.getInt(getContentResolver(),
                "QS_BG_SATU_WARNA", 0x11000000);
        satuwarna.setNewPreviewColor(mgradL4);
        satuwarna.setAlphaSliderEnabled(true);
        String mgradHexaL4 = String.format("#%08x", (0x11000000 & mgradL4));
        if (mgradHexaL4.equals("#11000000")) {
            satuwarna.setSummary(R.string.color_default);
        } else {
            satuwarna.setSummary(mgradHexaL4);
        }
        satuwarna.setOnPreferenceChangeListener(this);
       
        rgbBPPicker = (ColorPickerPreference) findPreference(ANCIENTBACKPROGRESSCOLOR);
        int mbacaColor = Settings.System.getInt(getContentResolver(),
                "AncientBackProgressColor", 0xFFFF0000);
        rgbBPPicker.setNewPreviewColor(mbacaColor);
        rgbBPPicker.setAlphaSliderEnabled(true);
        String mbacaColorHex = String.format("#%08x", (0xFFFF0000 & mbacaColor));
        if (mbacaColorHex.equals("#ffff0000")) {
            rgbBPPicker.setSummary(R.string.color_default);
        } else {
            rgbBPPicker.setSummary(mbacaColorHex);
        }
        rgbBPPicker.setOnPreferenceChangeListener(this);
                
        rgbThumbPicker = (ColorPickerPreference) findPreference(ANCIENTTHUMBCOLOR);
        int mbacaColore = Settings.System.getInt(getContentResolver(),
                "AncientThumbColor", 0xFFFF0000);
        rgbThumbPicker.setNewPreviewColor(mbacaColore);
        rgbThumbPicker.setAlphaSliderEnabled(true);
        String mbacaColoreHex = String.format("#%08x", (0xFFFF0000 & mbacaColore));
        if (mbacaColoreHex.equals("#ffff0000")) {
            rgbThumbPicker.setSummary(R.string.color_default);
        } else {
            rgbThumbPicker.setSummary(mbacaColoreHex);
        }
        rgbThumbPicker.setOnPreferenceChangeListener(this);   
        
        rgbBrightPicker = (ColorPickerPreference) findPreference(ANCIENTBRIGHTNESSCOLOR);
        int mbacaColored = Settings.System.getInt(getContentResolver(),
                "AncientBrightnessColor", 0xFFFF0000);
        rgbBrightPicker.setNewPreviewColor(mbacaColored);
        rgbBrightPicker.setAlphaSliderEnabled(true);
        String mbacaColoredHex = String.format("#%08x", (0xFFFF0000 & mbacaColored));
        if (mbacaColoredHex.equals("#ffff0000")) {
            rgbBrightPicker.setSummary(R.string.color_default);
        } else {
            rgbBrightPicker.setSummary(mbacaColoredHex);
        }
        rgbBrightPicker.setOnPreferenceChangeListener(this);   

        mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
        mSmartPulldown.setOnPreferenceChangeListener(this);
        int smartPulldown = Settings.System.getInt(resolver,
                Settings.System.QS_SMART_PULLDOWN, 0);
        mSmartPulldown.setValue(String.valueOf(smartPulldown));
        updateSmartPulldownSummary(smartPulldown);

        mFooterString = (SystemSettingEditTextPreference) findPreference(FOOTER_TEXT_STRING);
        mFooterString.setOnPreferenceChangeListener(this);
        String footerString = Settings.System.getString(getContentResolver(),
                FOOTER_TEXT_STRING);
        if (footerString != null && footerString != "")
            mFooterString.setText(footerString);
        else {
            mFooterString.setText("CraftWithHeart");
            Settings.System.putString(getActivity().getContentResolver(),
                    Settings.System.FOOTER_TEXT_STRING, "CraftWithHeart");
        }

        mCustomHeader = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_CUSTOM_HEADER);
        int qsHeader = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CUSTOM_HEADER, 0);
        mCustomHeader.setChecked(qsHeader != 0);
        mCustomHeader.setOnPreferenceChangeListener(this);

        mQsMedia = (SystemSettingSwitchPreference) findPreference(PREF_QS_MEDIA_PLAYER);
        mQsMedia.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.QS_MEDIA_PLAYER, 0) == 1));
        mQsMedia.setOnPreferenceChangeListener(this);

        mQsBgFul = (SystemSettingSwitchPreference) findPreference(QS_BG_FUL);
        mQsBgFul.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                "QS_BG_FUL", 0) == 1));
        mQsBgFul.setOnPreferenceChangeListener(this);
        
        mQsAlarm = (SystemSettingSwitchPreference) findPreference(PENGUSIR_ALARM);
        mQsAlarm.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                "PENGUSIR_ALARM", 0) == 1));
        mQsAlarm.setOnPreferenceChangeListener(this);
        
        mQsRinger = (SystemSettingSwitchPreference) findPreference(PENGUSIR_RINGER);
        mQsRinger.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                "PENGUSIR_RINGER", 0) == 1));
        mQsRinger.setOnPreferenceChangeListener(this);

        mQqsTile = (SystemSettingListPreference) findPreference("QQS_TILE_STYLE");
        int sbQqstileStyle   = Settings.System.getIntForUser(getContentResolver(),
                "QQS_TILE_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndexQqstile = mQqsTile.findIndexOfValue(String.valueOf(sbQqstileStyle ));
        mQqsTile.setValueIndex(valueIndexQqstile >= 0 ? valueIndexQqstile : 0);
        mQqsTile.setSummary(mQqsTile.getEntry());
        mQqsTile.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mSmartPulldown) {
            int smartPulldown = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver, Settings.System.QS_SMART_PULLDOWN, smartPulldown);
            updateSmartPulldownSummary(smartPulldown);
            return true;
        } else if (preference == grad1) {
            String hexagradi = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexagradi.equals("#11000000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexagradi);
            }
            int intHexaGradi = ColorPickerPreference.convertToColorInt(hexagradi);
            Settings.System.putInt(getContentResolver(),
                    "QS_BG_GRAD_WARNASATU", intHexaGradi);
            return true;  
        } else if (preference == grad2) {
            String hexagradie = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexagradie.equals("#11000000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexagradie);
            }
            int intHexaGradie = ColorPickerPreference.convertToColorInt(hexagradie);
            Settings.System.putInt(getContentResolver(),
                    "QS_BG_GRAD_WARNADUA", intHexaGradie);
            return true;  
        } else if (preference == grad3) {
            String hexagradien = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexagradien.equals("#11000000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexagradien);
            }
            int intHexaGradien = ColorPickerPreference.convertToColorInt(hexagradien);
            Settings.System.putInt(getContentResolver(),
                    "QS_BG_GRAD_WARNATIGA", intHexaGradien);
            return true;  
        } else if (preference == satuwarna) {
            String hexagradiena = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexagradiena.equals("#11000000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexagradiena);
            }
            int intHexaGradiena = ColorPickerPreference.convertToColorInt(hexagradiena);
            Settings.System.putInt(getContentResolver(),
                    "QS_BG_SATU_WARNA", intHexaGradiena);
            return true;  
         } else if (preference == satuwarna12) {
            String hexagradiena12 = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexagradiena12.equals("#11000000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexagradiena12);
            }
            int intHexaGradiena12 = ColorPickerPreference.convertToColorInt(hexagradiena12);
            Settings.System.putInt(getContentResolver(),
                    "QS_BG_SATU_WARNADUA", intHexaGradiena12);
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
                    "AncientBrightnessColor", intHexa);
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
                    "AncientThumbColor", intHexaB);
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
                    "AncientBackProgressColor", intHexaBA);    
            return true;       
        } else if (preference == mFooterString) {
            String value = (String) newValue;
            if (value != "" && value != null)
                Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.FOOTER_TEXT_STRING, value);
            else {
                mFooterString.setText("CraftWithHeart");
                Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.FOOTER_TEXT_STRING, "CraftWithHeart");
            }
            return true;
        } else if (preference == mCustomHeader) {
            boolean header = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_CUSTOM_HEADER, header ? 1 : 0);
            return true;
        } else if (preference == mQsMedia) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.QS_MEDIA_PLAYER, value ? 1 : 0);
            AncientUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mQsBgFul) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    "QS_BG_FUL", value ? 1 : 0);
            //AncientUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mQqsTile) {
            int sbQqstileStyleValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "QQS_TILE_STYLE", sbQqstileStyleValue, UserHandle.USER_CURRENT);
            mQqsTile.setSummary(mQqsTile.getEntries()[sbQqstileStyleValue]);
            return true;
        } else if (preference == mQsAlarm) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    "PENGUSIR_ALARM", value ? 1 : 0);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }        
            return true;    
        } else if (preference == mQsRinger) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    "PENGUSIR_RINGER", value ? 1 : 0);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }        
            return true;     
        }
        return false;
    }

    private void updateSmartPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Smart pulldown deactivated
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));
        } else if (value == 3) {
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_none_summary));
        } else {
            String type = res.getString(value == 1
                    ? R.string.smart_pulldown_dismissable
                    : R.string.smart_pulldown_ongoing);
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));
        }
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
                    sir.xmlResId = R.xml.ancient_settings_quicksettings;
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
