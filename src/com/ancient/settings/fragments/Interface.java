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

import static android.os.UserHandle.USER_CURRENT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.SettingsPreferenceFragment;

import com.ancient.settings.preferences.SystemSettingListPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.development.OverlayCategoryPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class Interface extends DashboardFragment implements OnPreferenceChangeListener {

    public static final String TAG = "Interface";

    private String MONET_ENGINE_COLOR_OVERRIDE = "monet_engine_color_override";
    
    private static final String HOMEPAGE_THEME = "HOMEPAGE_THEME";
    
    private static final String HOMEPAGE_THEME_OVERLAY = "com.idc.settings.hompage.stock";
    private static final String HOMEPAGE_THEME_ANDROID_OVERLAY = "com.idc.android.hompage.stock";   
    private static final String HOMEPAGE_THEME_SETTINGS_OVERLAY = "com.idc.ganteng.hompage.stock";   

    private Context mContext;
    
    private SystemSettingListPreference mhomeSwitch;
    private ColorPickerPreference mMonetColor;
    
    private IOverlayManager mOverlayService;  

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mContext = getActivity();
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen screen = getPreferenceScreen();

        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
        
        mMonetColor = (ColorPickerPreference) screen.findPreference(MONET_ENGINE_COLOR_OVERRIDE);
        int intColor = Settings.Secure.getInt(resolver, MONET_ENGINE_COLOR_OVERRIDE, Color.WHITE);
        String hexColor = String.format("#%08x", (0xffffff & intColor));
        mMonetColor.setNewPreviewColor(intColor);
        mMonetColor.setSummary(hexColor);
        mMonetColor.setOnPreferenceChangeListener(this);
        
        mhomeSwitch = (SystemSettingListPreference) findPreference("HOMEPAGE_THEME");
        int smhomeStyle = Settings.System.getIntForUser(getContentResolver(),
                "HOMEPAGE_THEME", 1, UserHandle.USER_CURRENT);
        int valueIndexh = mhomeSwitch.findIndexOfValue(String.valueOf(smhomeStyle));
        mhomeSwitch.setValueIndex(valueIndexh >= 0 ? valueIndexh : 0);
        mhomeSwitch.setSummary(mhomeSwitch.getEntry());
        mhomeSwitch.setOnPreferenceChangeListener(this);
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
        } else if  (preference == mhomeSwitch) {
            int smhomeStyle = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "HOMEPAGE_THEME", smhomeStyle, UserHandle.USER_CURRENT);
            mhomeSwitch.setSummary(mhomeSwitch.getEntries()[smhomeStyle]);
            if (smhomeStyle == 1) {
                   try {
                      mOverlayService.setEnabled(HOMEPAGE_THEME_OVERLAY, false, USER_CURRENT);
                      mOverlayService.setEnabled(HOMEPAGE_THEME_ANDROID_OVERLAY, false, USER_CURRENT); 
                      mOverlayService.setEnabled(HOMEPAGE_THEME_SETTINGS_OVERLAY, false, USER_CURRENT); 
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
            } else if (smhomeStyle == 2) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(HOMEPAGE_THEME_ANDROID_OVERLAY, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
            } else if (smhomeStyle == 3) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(HOMEPAGE_THEME_SETTINGS_OVERLAY, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
            } else {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(HOMEPAGE_THEME_OVERLAY, USER_CURRENT); 
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
            }
            return true;
        } 
        return false;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.ancient_settings_interface;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.font"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.adaptive_icon_shape"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.icon_pack"));
        return controllers;
    }

    /**
     * For Search.
     */

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.ancient_settings_interface);
}
