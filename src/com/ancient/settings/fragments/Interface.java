/*
 * Copyright (C) 2019 Rebellion-OS
 * Copyright (C) 2019 Ancient-OS
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

import static android.os.UserHandle.USER_SYSTEM;
import android.app.ActivityManagerNative;
import android.app.UiModeManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import androidx.annotation.VisibleForTesting;
import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

import com.ancient.settings.preferences.SystemSettingListPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Interface extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    public static final String TAG = "Interface";
    
    private String MONET_ENGINE_COLOR_OVERRIDE = "monet_engine_color_override";
    private static final String SIGNAL_WIPI_COMBINED = "SIGNAL_WIPI_COMBINED";
    private static final String QS_TILE_BGO = "QS_TILE_BGO";
    private static final String SIGNAL_WIPI_IKONT = "SIGNAL_WIPI_IKONT";
    
    private Context mContext;
    
    private IOverlayManager mOverlayService;
    
    private ColorPickerPreference mMonetColor;
    private SystemSettingListPreference mAa;
    private SystemSettingListPreference mAb;
    private SystemSettingListPreference mAc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));

        addPreferencesFromResource(R.xml.ancient_settings_interface);
        
        mContext = getActivity();
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen screen = getPreferenceScreen();
        
        mMonetColor = (ColorPickerPreference) screen.findPreference(MONET_ENGINE_COLOR_OVERRIDE);
        int intColor = Settings.Secure.getInt(resolver, MONET_ENGINE_COLOR_OVERRIDE, Color.WHITE);
        String hexColor = String.format("#%08x", (0xffffff & intColor));
        mMonetColor.setNewPreviewColor(intColor);
        mMonetColor.setSummary(hexColor);
        mMonetColor.setOnPreferenceChangeListener(this);
        
        mAa = (SystemSettingListPreference) findPreference(SIGNAL_WIPI_COMBINED);
        int sbaStyle = Settings.System.getIntForUser(getContentResolver(),
                "SIGNAL_WIPI_COMBINED", 0, UserHandle.USER_CURRENT);
        int valuebaIndex = mAa.findIndexOfValue(String.valueOf(sbaStyle));
        mAa.setValueIndex(valuebaIndex >= 0 ? valuebaIndex : 0);
        mAa.setSummary(mAa.getEntry());
        mAa.setOnPreferenceChangeListener(this);
        
        mAb = (SystemSettingListPreference) findPreference(QS_TILE_BGO);
        int sbaaStyle = Settings.System.getIntForUser(getContentResolver(),
                "QS_TILE_BGO", 0, UserHandle.USER_CURRENT);
        int valuebaaIndex = mAb.findIndexOfValue(String.valueOf(sbaaStyle));
        mAb.setValueIndex(valuebaaIndex >= 0 ? valuebaIndex : 0);
        mAb.setSummary(mAb.getEntry());
        mAb.setOnPreferenceChangeListener(this);
        
        mAc = (SystemSettingListPreference) findPreference(SIGNAL_WIPI_IKONT);
        int sbabStyle = Settings.System.getIntForUser(getContentResolver(),
                "SIGNAL_WIPI_IKONT", 0, UserHandle.USER_CURRENT);
        int valuebabIndex = mAc.findIndexOfValue(String.valueOf(sbabStyle));
        mAc.setValueIndex(valuebabIndex >= 0 ? valuebabIndex : 0);
        mAc.setSummary(mAc.getEntry());
        mAc.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.ANCIENT_SETTINGS;
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mMonetColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                .parseInt(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.Secure.putInt(resolver,
                MONET_ENGINE_COLOR_OVERRIDE, intHex);
            return true;
        } else if (preference == mAa) {
            int mauStyleValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "SIGNAL_WIPI_COMBINED", mauStyleValue, UserHandle.USER_CURRENT);
            mAa.setSummary(mAa.getEntries()[mauStyleValue]);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;	
        } else if (preference == mAb) {
            int maucStyleValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "QS_TILE_BGO", maucStyleValue, UserHandle.USER_CURRENT);
            mAb.setSummary(mAb.getEntries()[maucStyleValue]);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;	
        } else if (preference == mAc) {
            int mauccStyleValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "SIGNAL_WIPI_IKONT", mauccStyleValue, UserHandle.USER_CURRENT);
            mAc.setSummary(mAc.getEntries()[mauccStyleValue]);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;	
        } 
        return false;
    }
}
