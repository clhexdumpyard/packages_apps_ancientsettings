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
import com.android.internal.util.ancient.ThemesUtils;
import com.android.internal.util.ancient.AncientUtils;

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
    private static final String KEY_CHARGING_LIGHT = "charging_light";
    private static final String LED_CATEGORY = "led";
    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String NOTIFICATION_HEADERS = "notification_headers";
    private static final String CENTER_NOTIFICATION_HEADER = "center_notification_headers";
    private static final String NOTIFICATION_PULSE_COLOR = "ambient_notification_light_color";
    private static final String NOTIFICATION_PULSE_DURATION = "notification_pulse_duration";
    private static final String NOTIFICATION_PULSE_REPEATS = "notification_pulse_repeats";
    private static final String PULSE_COLOR_MODE_PREF = "ambient_notification_light_color_mode";
    private static final String NOTIFICATION_MATERIAL_DISMISS = "notification_material_dismiss";
    private static final String HEADER_ICONS_STYLE = "HEADER_ICONS_STYLE";
    private static final String STATUSBAR_ICONS_STYLE = "STATUSBAR_ICONS_STYLE";
    private static final String PREF_NOTIFICATION_HEADERTEXT_COLOR = "NOTIFICATION_HEADERTEXT_COLOR";
    private static final String PREF_RETICKER_STATUS = "reticker_status";
    private static final String PREF_RETICKER_COLORED = "reticker_colored";

    private IOverlayManager mOverlayService;

    private ColorPickerPreference mEdgeLightColorPreference;
    private CustomSeekBarPreference mEdgeLightDurationPreference;
    private CustomSeekBarPreference mEdgeLightRepeatCountPreference;
    private ListPreference mColorMode;
    private Preference mChargingLeds;
    private PreferenceCategory mLedCategory;
    private SystemSettingSwitchPreference mNotificationHeader;
    private SystemSettingSwitchPreference mCenterNotificationHeader;
    private SystemSettingSwitchPreference mNotificationmaterialDismiss;
    private SystemSettingSwitchPreference mHeaderIcon;
    private SystemSettingSwitchPreference mStatusbarIcon;
    private SystemSettingListPreference mHeaderTextColorCustom;
    private SystemSettingSwitchPreference mRetickera;
    private SystemSettingSwitchPreference mRetickerb;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ancient_settings_notifications);
        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();
        
        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));

        boolean hasLED = res.getBoolean(
                com.android.internal.R.bool.config_hasNotificationLed);
        if (hasLED) {
            mChargingLeds = findPreference(KEY_CHARGING_LIGHT);
            if (mChargingLeds != null
                    && !res.getBoolean(
                            com.android.internal.R.bool.config_intrusiveBatteryLed)) {
                prefScreen.removePreference(mChargingLeds);
            }
        } else {
            mLedCategory = findPreference(LED_CATEGORY);
            mLedCategory.setVisible(false);
        }

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!AncientUtils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(incallVibCategory);
        }

        mHeaderTextColorCustom = (SystemSettingListPreference) findPreference(PREF_NOTIFICATION_HEADERTEXT_COLOR);
        int mHeaderTextColor = Settings.System.getIntForUser(getContentResolver(),
                "NOTIFICATION_HEADERTEXT_COLOR", 0, UserHandle.USER_CURRENT);
        int valueIndexhed = mHeaderTextColorCustom.findIndexOfValue(String.valueOf(mHeaderTextColor));
        mHeaderTextColorCustom.setValueIndex(valueIndexhed >= 0 ? valueIndexhed : 0);
        mHeaderTextColorCustom.setSummary(mHeaderTextColorCustom.getEntry());
        mHeaderTextColorCustom.setOnPreferenceChangeListener(this);

        mNotificationHeader = findPreference(NOTIFICATION_HEADERS);
        mNotificationHeader.setChecked((Settings.System.getInt(resolver,
                Settings.System.NOTIFICATION_HEADERS, 1) == 1));
        mNotificationHeader.setOnPreferenceChangeListener(this);

        mCenterNotificationHeader = (SystemSettingSwitchPreference) findPreference(CENTER_NOTIFICATION_HEADER);
        mCenterNotificationHeader.setChecked((Settings.System.getInt(resolver,
                Settings.System.CENTER_NOTIFICATION_HEADERS, 0) == 1));
        mCenterNotificationHeader.setOnPreferenceChangeListener(this);

        mNotificationmaterialDismiss = findPreference(NOTIFICATION_MATERIAL_DISMISS);
        mNotificationmaterialDismiss.setChecked((Settings.System.getInt(resolver,
                Settings.System.NOTIFICATION_MATERIAL_DISMISS, 1) == 1));
        mNotificationmaterialDismiss.setOnPreferenceChangeListener(this);

        mHeaderIcon = findPreference(HEADER_ICONS_STYLE);
        mHeaderIcon.setChecked((Settings.System.getInt(resolver,
                "HEADER_ICONS_STYLE", 1) == 1));
        mHeaderIcon.setOnPreferenceChangeListener(this);

        mStatusbarIcon = findPreference(STATUSBAR_ICONS_STYLE);
        mStatusbarIcon.setChecked((Settings.System.getInt(resolver,
                "STATUSBAR_ICONS_STYLE", 1) == 1));
        mStatusbarIcon.setOnPreferenceChangeListener(this);

        mEdgeLightRepeatCountPreference = (CustomSeekBarPreference) findPreference(NOTIFICATION_PULSE_REPEATS);
        mEdgeLightRepeatCountPreference.setOnPreferenceChangeListener(this);
        int repeats = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_REPEATS, 0);
        mEdgeLightRepeatCountPreference.setValue(repeats);

        mEdgeLightDurationPreference = (CustomSeekBarPreference) findPreference(NOTIFICATION_PULSE_DURATION);
        mEdgeLightDurationPreference.setOnPreferenceChangeListener(this);
        int duration = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_DURATION, 2);
        mEdgeLightDurationPreference.setValue(duration);

        mColorMode = (ListPreference) findPreference(PULSE_COLOR_MODE_PREF);
        int value;
        boolean colorModeAutomatic = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 0) != 0;
        boolean colorModeAccent = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_ACCENT, 0) != 0;
        if (colorModeAutomatic) {
            value = 0;
        } else if (colorModeAccent) {
            value = 1;
        } else {
            value = 2;
        }

        mColorMode.setValue(Integer.toString(value));
        mColorMode.setSummary(mColorMode.getEntry());
        mColorMode.setOnPreferenceChangeListener(this);

        mEdgeLightColorPreference = (ColorPickerPreference) findPreference(NOTIFICATION_PULSE_COLOR);
        int edgeLightColor = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_COLOR, 0xFF1A73E8);
        mEdgeLightColorPreference.setNewPreviewColor(edgeLightColor);
        mEdgeLightColorPreference.setAlphaSliderEnabled(false);
        String edgeLightColorHex = String.format("#%08x", (0xFF1A73E8 & edgeLightColor));
        if (edgeLightColorHex.equals("#ff1a73e8")) {
            mEdgeLightColorPreference.setSummary(R.string.color_default);
        } else {
            mEdgeLightColorPreference.setSummary(edgeLightColorHex);
        }
        mEdgeLightColorPreference.setOnPreferenceChangeListener(this);
        
        mRetickera = (SystemSettingSwitchPreference) findPreference(PREF_RETICKER_STATUS);
        mRetickera.setOnPreferenceChangeListener(this);
        
        mRetickerb = (SystemSettingSwitchPreference) findPreference(PREF_RETICKER_COLORED);
        mRetickerb.setOnPreferenceChangeListener(this);    

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNotificationHeader) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    Settings.System.NOTIFICATION_HEADERS, value ? 1 : 0);
            return true;
        } else if (preference == mHeaderTextColorCustom) {
            int mHeaderTextColor = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "NOTIFICATION_HEADERTEXT_COLOR", mHeaderTextColor, UserHandle.USER_CURRENT);
            mHeaderTextColorCustom.setSummary(mHeaderTextColorCustom.getEntries()[mHeaderTextColor]);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == mCenterNotificationHeader) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    Settings.System.CENTER_NOTIFICATION_HEADERS, value ? 0 : 1);
            AncientUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mHeaderIcon) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    "HEADER_ICONS_STYLE", value ? 1 : 0);
            AncientUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mStatusbarIcon) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    "STATUSBAR_ICONS_STYLE", value ? 1 : 0);
            AncientUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mEdgeLightColorPreference) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hex.equals("#ff1a73e8")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_PULSE_COLOR, intHex);
            return true;
        } else if (preference == mEdgeLightRepeatCountPreference) {
            int value = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_PULSE_REPEATS, value);
            return true;
        } else if (preference == mEdgeLightDurationPreference) {
            int value = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_PULSE_DURATION, value);
            return true;
        } else if (preference == mColorMode) {
            int value = Integer.valueOf((String) objValue);
            int index = mColorMode.findIndexOfValue((String) objValue);
            mColorMode.setSummary(mColorMode.getEntries()[index]);
            if (value == 0) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 1);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_ACCENT, 0);
            } else if (value == 1) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 0);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_ACCENT, 1);
            } else {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 0);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_ACCENT, 0);
            }
            return true;
        } else if (preference == mNotificationmaterialDismiss) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    Settings.System.NOTIFICATION_MATERIAL_DISMISS, value ? 1 : 0);
            return true;
        } else if (preference == mRetickera) {
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;    
        } else if (preference == mRetickerb) {
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
                "NOTIFICATION_BG_ALPHA", 255, UserHandle.USER_CURRENT);
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
