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
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.ancient.AncientUtils;
import com.android.internal.util.ancient.fod.FodUtils;
import com.android.internal.util.ancient.ThemesUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.ancient.settings.preferences.CustomSeekBarPreference;
import com.ancient.settings.preferences.SystemSettingSeekBarPreference;
import com.ancient.settings.preferences.SystemSettingSwitchPreference;
import com.ancient.settings.utils.DeviceUtils;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class LockScreen extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String AOD_SCHEDULE_KEY = "always_on_display_schedule";
    private static final String FINGERPRINT_ERROR_VIB = "fingerprint_error_vib";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String FOD_ANIMATION_CATEGORY = "fod_animations";
    private static final String FOD_ICON_PICKER_CATEGORY = "fod_icon_picker";
    private static final String LOCKSCREEN_MAX_NOTIF_CONFIG = "lockscreen_max_notif_cofig";
    private static final String LOCKSCREEN_BLUR = "LOCKSCREEN_BLUR";
    private static final String AMBIENT_ICONS_COLOR = "AMBIENT_ICONS_COLOR";
    private static final String AMBIENT_ICONS_LOCKSCREEN = "AMBIENT_ICONS_LOCKSCREEN"; 
    private static final String AMBIENT_ICONS_SIZE = "AMBIENT_ICONS_SIZE";     
        
    private IOverlayManager mOverlayService;
    private UiModeManager mUiModeManager;    

    private ColorPickerPreference mAmbientIconsColor;    
    private FingerprintManager mFingerprintManager;
    private SwitchPreference mFingerprintErrorVib;
    private SwitchPreference mFingerprintVib;
    private PreferenceCategory mFODIconPickerCategory;
    private CustomSeekBarPreference mMaxKeyguardNotifConfig;
    private Preference mLockscreenBlur;  
    private SystemSettingSwitchPreference mAmbientIconsLockscreen;
    private SystemSettingSeekBarPreference mAmbeyenSize;    

    static final int MODE_DISABLED = 0;
    static final int MODE_NIGHT = 1;
    static final int MODE_TIME = 2;
    static final int MODE_MIXED_SUNSET = 3;
    static final int MODE_MIXED_SUNRISE = 4;

    Preference mAODPref;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.ancient_settings_lockscreen);
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final PackageManager mPm = getActivity().getPackageManager();
        Resources resources = getResources();
        Context mContext = getContext();
        ContentResolver resolver = getActivity().getContentResolver();
            
        mUiModeManager = getContext().getSystemService(UiModeManager.class);

        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));    

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SwitchPreference) findPreference(FINGERPRINT_VIB);
        if (mPm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) &&
                 mFingerprintManager != null) {
            if (!mFingerprintManager.isHardwareDetected()){
                prefScreen.removePreference(mFingerprintVib);
            } else {
                mFingerprintVib.setChecked((Settings.System.getInt(getContentResolver(),
                        Settings.System.FINGERPRINT_SUCCESS_VIB, 1) == 1));
                mFingerprintVib.setOnPreferenceChangeListener(this);
            }
        } else {
           prefScreen.removePreference(mFingerprintVib);
        }

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintErrorVib = (SwitchPreference) findPreference(FINGERPRINT_ERROR_VIB);
        if (mPm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) &&
                 mFingerprintManager != null) {
            if (!mFingerprintManager.isHardwareDetected()){
                prefScreen.removePreference(mFingerprintErrorVib);
            } else {
                mFingerprintErrorVib.setChecked((Settings.System.getInt(getContentResolver(),
                        Settings.System.FINGERPRINT_ERROR_VIB, 1) == 1));
                mFingerprintErrorVib.setOnPreferenceChangeListener(this);
            }
        } else {
            prefScreen.removePreference(mFingerprintErrorVib);
        }

        mFODIconPickerCategory = findPreference(FOD_ICON_PICKER_CATEGORY);
        if (mFODIconPickerCategory != null && !FodUtils.hasFodSupport(getContext())) {
            prefScreen.removePreference(mFODIconPickerCategory);
        }

        final PreferenceCategory fodCat = (PreferenceCategory) prefScreen
                .findPreference(FOD_ANIMATION_CATEGORY);
        final boolean isFodAnimationResources = AncientUtils.isPackageInstalled(getContext(),
                      getResources().getString(com.android.internal.R.string.config_fodAnimationPackage));
        if (!isFodAnimationResources) {
            prefScreen.removePreference(fodCat);
        }

        mMaxKeyguardNotifConfig = (CustomSeekBarPreference) findPreference(LOCKSCREEN_MAX_NOTIF_CONFIG);
        int kgconf = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, 3);
        mMaxKeyguardNotifConfig.setValue(kgconf);
        mMaxKeyguardNotifConfig.setOnPreferenceChangeListener(this);

        mAODPref = findPreference(AOD_SCHEDULE_KEY);
        updateAlwaysOnSummary();
            
        mLockscreenBlur = (Preference) findPreference(LOCKSCREEN_BLUR);
        if (!DeviceUtils.isBlurSupported()) {
            prefScreen.removePreference(mLockscreenBlur);
        }
            
        mAmbientIconsLockscreen = (SystemSettingSwitchPreference) findPreference(AMBIENT_ICONS_LOCKSCREEN);
        mAmbientIconsLockscreen.setChecked((Settings.System.getInt(getContentResolver(),
                "AMBIENT_ICONS_LOCKSCREEN", 0) == 1));
        mAmbientIconsLockscreen.setOnPreferenceChangeListener(this);

        // Ambient Icons Color
        mAmbientIconsColor = (ColorPickerPreference) findPreference(AMBIENT_ICONS_COLOR);
        int intColor = Settings.System.getInt(getContentResolver(),
                "AMBIENT_ICONS_COLOR", Color.WHITE);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mAmbientIconsColor.setNewPreviewColor(intColor);
        mAmbientIconsColor.setSummary(hexColor);
        mAmbientIconsColor.setOnPreferenceChangeListener(this); 
            
        mAmbeyenSize = (SystemSettingSeekBarPreference) findPreference(AMBIENT_ICONS_SIZE);
        int asconf = Settings.System.getInt(getContentResolver(),
                "AMBIENT_ICONS_SIZE", 80);
        mAmbeyenSize.setValue(asconf);
        mAmbeyenSize.setOnPreferenceChangeListener(this);    
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAlwaysOnSummary();
    }

    private void updateAlwaysOnSummary() {
        if (mAODPref == null) return;
        int mode = Settings.Secure.getIntForUser(getActivity().getContentResolver(),
                Settings.Secure.DOZE_ALWAYS_ON_AUTO_MODE, 0, UserHandle.USER_CURRENT);
        switch (mode) {
            default:
            case MODE_DISABLED:
                mAODPref.setSummary(R.string.disabled);
                break;
            case MODE_NIGHT:
                mAODPref.setSummary(R.string.night_display_auto_mode_twilight);
                break;
            case MODE_TIME:
                mAODPref.setSummary(R.string.night_display_auto_mode_custom);
                break;
            case MODE_MIXED_SUNSET:
                mAODPref.setSummary(R.string.always_on_display_schedule_mixed_sunset);
                break;
            case MODE_MIXED_SUNRISE:
                mAODPref.setSummary(R.string.always_on_display_schedule_mixed_sunrise);
                break;
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mFingerprintVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.FINGERPRINT_SUCCESS_VIB, value ? 1 : 0);
            return true;
        } else if (preference == mMaxKeyguardNotifConfig) {
            int kgconf = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, kgconf);
            return true;
        } else if (preference == mFingerprintErrorVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.FINGERPRINT_ERROR_VIB, value ? 1 : 0);
            return true;
        } else if (preference == mAmbientIconsLockscreen) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    "AMBIENT_ICONS_LOCKSCREEN", value ? 1 : 0);
            AncientUtils.showSystemUiRestartDialog(getContext());
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }          
            return true;
        } else if (preference == mAmbientIconsColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                .parseInt(String.valueOf(newValue)));
            mAmbientIconsColor.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    "AMBIENT_ICONS_COLOR", intHex);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }      
            return true;
        } else if (preference == mAmbeyenSize) {
            int asconf = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    "AMBIENT_ICONS_SIZE", asconf);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }    
            return true;        
        }
        return false;
    }
        
    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();   
        Settings.System.putIntForUser(resolver,
                "LOCKSCREEN_BLUR", 0, UserHandle.USER_CURRENT);  
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
                    sir.xmlResId = R.xml.ancient_settings_lockscreen;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    
                    if (!DeviceUtils.isBlurSupported()) {
                        keys.add(LOCKSCREEN_BLUR);
                    }    
                        
                    return keys;
                }
    };
}
