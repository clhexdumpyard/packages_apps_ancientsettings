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
import android.content.Context;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import androidx.preference.*;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.util.ancient.AncientUtils;
import com.ancient.settings.preferences.SystemSettingSwitchPreference;
import com.ancient.settings.preferences.SystemSettingListPreference;
import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class StatusBar extends SettingsPreferenceFragment implements
    Preference.OnPreferenceChangeListener, Indexable {

    private static final String STATUSBAR_DUAL_ROW = "statusbar_dual_row";
    private static final String KEY_STATUS_BAR_LOGO = "status_bar_logo";
    private static final String STATUSBAR_DUAL_STYLE = "statusbar_dual_style";
    private static final String NABIL_BACKGROUNDCLOCKSB_COLOR = "nabil_backgroundclocksb_color";
    private static final String NABIL_BACKGROUNDCLOCKSB_GRADIENT1 = "nabil_backgroundclocksb_gradient1";
    private static final String NABIL_BACKGROUNDCLOCKSB_GRADIENT2 = "nabil_backgroundclocksb_gradient2";
    private static final String NABIL_BACKGROUNDCLOCKSB_STROKECOLOR = "nabil_backgroundclocksb_strokecolor";

    private ColorPickerPreference mBacka;
    private ColorPickerPreference mBackb;
    private ColorPickerPreference mBackc;
    private ColorPickerPreference mBackd;
    private SystemSettingSwitchPreference mStatusbarDualRow;
    private SystemSettingListPreference mStatusbarDualStyle;
    private SwitchPreference mShowAncientLogo;
    private ListPreference mLogoStyle;
    private IOverlayManager mOverlayService;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.ancient_settings_statusbar);
        PreferenceScreen prefSet = getPreferenceScreen();
        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));

        mStatusbarDualRow = (SystemSettingSwitchPreference) findPreference(STATUSBAR_DUAL_ROW);
        mStatusbarDualRow.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUSBAR_DUAL_ROW, 0) == 1));
        mStatusbarDualRow.setOnPreferenceChangeListener(this);

        mShowAncientLogo = (SwitchPreference) findPreference(KEY_STATUS_BAR_LOGO);
        mShowAncientLogo.setChecked((Settings.System.getInt(getContentResolver(),
             Settings.System.STATUS_BAR_LOGO, 0) == 1));
        mShowAncientLogo.setOnPreferenceChangeListener(this);

        mStatusbarDualStyle = (SystemSettingListPreference) findPreference("statusbar_dual_style");
        int StatusbarDualStyle = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUSBAR_DUAL_STYLE,
                0, UserHandle.USER_CURRENT);
        mStatusbarDualStyle.setValue(String.valueOf(StatusbarDualStyle));
        mStatusbarDualStyle.setSummary(mStatusbarDualStyle.getEntry());
        mStatusbarDualStyle.setOnPreferenceChangeListener(this);

        mLogoStyle = (ListPreference) findPreference("status_bar_logo_style");
        int logoStyle = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_LOGO_STYLE,
                0, UserHandle.USER_CURRENT);
        mLogoStyle.setValue(String.valueOf(logoStyle));
        mLogoStyle.setSummary(mLogoStyle.getEntry());
        mLogoStyle.setOnPreferenceChangeListener(this);
        
        mBacka = (ColorPickerPreference) findPreference(NABIL_BACKGROUNDCLOCKSB_COLOR);
        int mbacaColor = Settings.System.getInt(getContentResolver(),
                "nabil_backgroundclocksb_color", 0xFFFF0000);
        mBacka.setNewPreviewColor(mbacaColor);
        mBacka.setAlphaSliderEnabled(true);
        String mbacaColorHex = String.format("#%08x", (0xFFFF0000 & mbacaColor));
        if (mbacaColorHex.equals("#ffff0000")) {
            mBacka.setSummary(R.string.color_default);
        } else {
            mBacka.setSummary(mbacaColorHex);
        }
        mBacka.setOnPreferenceChangeListener(this);
        
        mBackb = (ColorPickerPreference) findPreference(NABIL_BACKGROUNDCLOCKSB_GRADIENT1);
        int mbacabColor = Settings.System.getInt(getContentResolver(),
                "nabil_backgroundclocksb_gradient1", 0xFFFF0000);
        mBackb.setNewPreviewColor(mbacabColor);
        mBackb.setAlphaSliderEnabled(true);
        String mbacabColorHex = String.format("#%08x", (0xFFFF0000 & mbacabColor));
        if (mbacabColorHex.equals("#ffff0000")) {
            mBackb.setSummary(R.string.color_default);
        } else {
            mBackb.setSummary(mbacabColorHex);
        }
        mBackb.setOnPreferenceChangeListener(this);
        
        mBackc = (ColorPickerPreference) findPreference(NABIL_BACKGROUNDCLOCKSB_GRADIENT2);
        int mbacacColor = Settings.System.getInt(getContentResolver(),
                "nabil_backgroundclocksb_gradient2", 0xFFFF00C9);
        mBackc.setNewPreviewColor(mbacacColor);
        mBackc.setAlphaSliderEnabled(true);
        String mbacacColorHex = String.format("#%08x", (0xFFFF00C9 & mbacacColor));
        if (mbacacColorHex.equals("#ffff00c9")) {
            mBackc.setSummary(R.string.color_default);
        } else {
            mBackc.setSummary(mbacacColorHex);
        }
        mBackc.setOnPreferenceChangeListener(this);
        
        mBackd = (ColorPickerPreference) findPreference(NABIL_BACKGROUNDCLOCKSB_STROKECOLOR);
        int mbacadColor = Settings.System.getInt(getContentResolver(),
                "nabil_backgroundclocksb_strokecolor", 0xFFFFFFFF);
        mBackd.setNewPreviewColor(mbacadColor);
        mBackd.setAlphaSliderEnabled(true);
        String mbacacColorHex = String.format("#%08x", (0xFFFFFFFF & mbacadColor));
        if (mbacadColorHex.equals("#ffffffff")) {
            mBackd.setSummary(R.string.color_default);
        } else {
            mBackd.setSummary(mbacadColorHex);
        }
        mBackd.setOnPreferenceChangeListener(this);
        
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusbarDualRow) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_DUAL_ROW, value ? 1 : 0);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;
        } else if  (preference == mShowAncientLogo) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_LOGO, value ? 1 : 0);
            return true;
        } else if (preference.equals(mStatusbarDualStyle)) {
            int StatusbarDualStyle = Integer.parseInt(((String) newValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.STATUSBAR_DUAL_STYLE, StatusbarDualStyle, UserHandle.USER_CURRENT);
            int index = mStatusbarDualStyle.findIndexOfValue((String) newValue);
            mStatusbarDualStyle.setSummary(
                    mStatusbarDualStyle.getEntries()[index]);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;
        } else if (preference.equals(mLogoStyle)) {
            int logoStyle = Integer.parseInt(((String) newValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_LOGO_STYLE, logoStyle, UserHandle.USER_CURRENT);
            int index = mLogoStyle.findIndexOfValue((String) newValue);
            mLogoStyle.setSummary(
                    mLogoStyle.getEntries()[index]);
            return true;
        } else if (preference == mBacka) {
            String hexa = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hexa.equals("#ffff0000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexa);
            }
            int intHexa = ColorPickerPreference.convertToColorInt(hexa);
            Settings.System.putInt(getContentResolver(),
                    "nabil_backgroundclocksb_color", intHexa);
            return true;  
        } else if (preference == mBackb) {
            String hexb = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hexb.equals("#ffff0000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexb);
            }
            int intHexb = ColorPickerPreference.convertToColorInt(hexb);
            Settings.System.putInt(getContentResolver(),
                    "nabil_backgroundclocksb_gradient1", intHexb);
            return true;   
        } else if (preference == mBackc) {
            String hexc = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hexc.equals("#ffff00c9")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexc);
            }
            int intHexc = ColorPickerPreference.convertToColorInt(hexc);
            Settings.System.putInt(getContentResolver(),
                    "nabil_backgroundclocksb_gradient2", intHexc);
            return true;
        } else if (preference == mBackd) {
            String hexd = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hexd.equals("#ffff00c9")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexd);
            }
            int intHexd = ColorPickerPreference.convertToColorInt(hexd);
            Settings.System.putInt(getContentResolver(),
                    "nabil_backgroundclocksb_strokecolor", intHexd);
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
                    sir.xmlResId = R.xml.ancient_settings_statusbar;
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
