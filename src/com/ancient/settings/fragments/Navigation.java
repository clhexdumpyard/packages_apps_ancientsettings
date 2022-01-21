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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.ServiceManager;
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

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.ancient.settings.preferences.SystemSettingSwitchPreference;
import com.ancient.settings.preferences.SystemSettingListPreference;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Navigation extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "Navigation";

    private static final String KEY_NAVIGATION_BAR_ENABLED = "force_show_navbar";
    private static final String KEY_LAYOUT_SETTINGS = "layout_settings";
    private static final String KEY_NAVIGATION_BAR_ARROWS = "navigation_bar_menu_arrow_keys";
    private static final String NAVBAR_ICON_STYLE = "NAVBAR_ICON_STYLE";
    
    private static final String NAVBAR_OVERLAY_STYLE1 = "com.android.theme.navbar.android";
    private static final String NAVBAR_OVERLAY_STYLE2 = "com.android.theme.navbar.asus"; 
    private static final String NAVBAR_OVERLAY_STYLE3 = "com.android.theme.navbar.moto";        
    private static final String NAVBAR_OVERLAY_STYLE4 = "com.android.theme.navbar.nexus"; 
    private static final String NAVBAR_OVERLAY_STYLE5 = "com.android.theme.navbar.old"; 
    private static final String NAVBAR_OVERLAY_STYLE6 = "com.android.theme.navbar.oneplus";
    private static final String NAVBAR_OVERLAY_STYLE7 = "com.android.theme.navbar.oneui"; 
    private static final String NAVBAR_OVERLAY_STYLE8 = "com.android.theme.navbar.sammy";        
    private static final String NAVBAR_OVERLAY_STYLE9 = "com.android.theme.navbar.tecno"; 
    private static final String NAVBAR_OVERLAY_STYLE10 = "com.android.theme.navbar.anc1"; 
    private static final String NAVBAR_OVERLAY_STYLE11 = "com.android.theme.navbar.anc2";
    private static final String NAVBAR_OVERLAY_STYLE12 = "com.android.theme.navbar.anc3"; 
    private static final String NAVBAR_OVERLAY_STYLE13 = "com.android.theme.navbar.anc4";        
    private static final String NAVBAR_OVERLAY_STYLE14 = "com.android.theme.navbar.anc5";    
    

    private Preference mLayoutSettings;
    private SwitchPreference mNavigationBar;
    private SystemSettingSwitchPreference mNavigationArrows;
    private SystemSettingListPreference idcNvIconStyle;   

    private boolean mIsNavSwitchingMode = false;

    private Handler mHandler;
    private IOverlayManager mOverlayService; 

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
                
        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));    
                
        idcNvIconStyle = (SystemSettingListPreference) findPreference("NAVBAR_ICON_STYLE");
        int nvIconStyle = Settings.System.getIntForUser(getContentResolver(),
                "NAVBAR_ICON_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndexnvicon = idcNvIconStyle.findIndexOfValue(String.valueOf(nvIconStyle));
        idcNvIconStyle.setValueIndex(valueIndexnvicon >= 0 ? valueIndexnvicon : 0);
        idcNvIconStyle.setSummary(idcNvIconStyle.getEntry());
        idcNvIconStyle.setOnPreferenceChangeListener(this);         

        mNavigationBar = (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_ENABLED);
        mNavigationBar.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FORCE_SHOW_NAVBAR,
                defaultToNavigationBar ? 1 : 0) == 1));
        mNavigationBar.setOnPreferenceChangeListener(this);

        mLayoutSettings = (Preference) findPreference(KEY_LAYOUT_SETTINGS);

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
        } else if (preference == idcNvIconStyle) {
            int nvIconStyle = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "NAVBAR_ICON_STYLE", nvIconStyle, UserHandle.USER_CURRENT);
            idcNvIconStyle.setSummary(idcNvIconStyle.getEntries()[nvIconStyle]);
                if (nvIconStyle == 0) {
                   try {
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE1, false, USER_CURRENT);
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE2, false, USER_CURRENT);
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE3, false, USER_CURRENT);
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE4, false, USER_CURRENT);
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE5, false, USER_CURRENT); 
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE6, false, USER_CURRENT);
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE7, false, USER_CURRENT);
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE8, false, USER_CURRENT);
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE9, false, USER_CURRENT);
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE10, false, USER_CURRENT); 
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE11, false, USER_CURRENT);  
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE12, false, USER_CURRENT);
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE13, false, USER_CURRENT);
                      mOverlayService.setEnabled(NAVBAR_OVERLAY_STYLE14, false, USER_CURRENT);  
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (nvIconStyle == 1) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE1, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (nvIconStyle == 2) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE2, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 3) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE3, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 4) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE4, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 5) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE5, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 6) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE6, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (nvIconStyle == 7) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE7, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 8) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE8, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 9) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE9, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 10) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE10, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 11) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE11, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 12) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE12, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 13) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE13, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (nvIconStyle == 14) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(NAVBAR_OVERLAY_STYLE14, USER_CURRENT);     
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
