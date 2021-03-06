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
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.*;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.ancient.AncientUtils;
import com.android.internal.util.ancient.udfps.UdfpsUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.ancient.settings.preferences.SystemSettingSwitchPreference;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.io.FileDescriptor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import com.ancient.settings.fragments.UdfpsIconPicker;

public class LockScreen extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String FINGERPRINT_CATEGORY = "lockscreen_ui_finterprint_category";
    private static final String FINGERPRINT_SUCCESS_VIB = "fingerprint_success_vib";
    private static final String FINGERPRINT_ERROR_VIB = "fingerprint_error_vib";
    private static final String ENABLE_UDFPS_START_HAPTIC_FEEDBACK = "enable_udfps_start_haptic_feedback";
    private static final String FOD_NIGHT_LIGHT = "fod_night_light";
    private static final String SCREEN_OFF_FOD = "screen_off_fod";
    private static final String CUSTOM_FOD_ICON_KEY = "custom_fp_icon_enabled";
    private static final String CUSTOM_FP_FILE_SELECT = "custom_fp_file_select";
    private static final int REQUEST_PICK_IMAGE = 0;
    private static final String AOD_SCHEDULE_KEY = "always_on_display_schedule";

    private Preference mCustomFPImage;
    private PreferenceCategory mFingerprintCategory;
    private SystemSettingSwitchPreference mCustomFodIcon;
    private Preference mUdfpsIconPicker;
    private FingerprintManager mFingerprintManager;
    private SwitchPreference mFingerprintSuccessVib;
    private SwitchPreference mFingerprintErrorVib;
    private SystemSettingSwitchPreference mUdfpsHapticFeedback;
    private SystemSettingSwitchPreference mFodNightLight;
    private SystemSettingSwitchPreference mScreenOffFOD;

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

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();
        final PackageManager mPm = getActivity().getPackageManager();
        final PreferenceCategory fpCategory = (PreferenceCategory)
                findPreference("lockscreen_ui_finterprint_category");

        mFingerprintCategory = (PreferenceCategory) findPreference(FINGERPRINT_CATEGORY);
        mFingerprintManager = (FingerprintManager)
                getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintSuccessVib = (SwitchPreference) findPreference(FINGERPRINT_SUCCESS_VIB);
        mFingerprintErrorVib = (SwitchPreference) findPreference(FINGERPRINT_ERROR_VIB);
        mUdfpsHapticFeedback = findPreference(ENABLE_UDFPS_START_HAPTIC_FEEDBACK);
        mFodNightLight = findPreference(FOD_NIGHT_LIGHT);
        mScreenOffFOD = findPreference(SCREEN_OFF_FOD);
        mUdfpsIconPicker = (Preference) prefSet.findPreference("udfps_icon_picker");

        mCustomFPImage = findPreference(CUSTOM_FP_FILE_SELECT);
        final String customIconURI = Settings.System.getString(getContext().getContentResolver(),
               Settings.System.OMNI_CUSTOM_FP_ICON);
        if (!TextUtils.isEmpty(customIconURI)) {
            setPickerIcon(customIconURI);
        }

        mCustomFodIcon = (SystemSettingSwitchPreference) findPreference(CUSTOM_FOD_ICON_KEY);
        boolean val = Settings.System.getIntForUser(getActivity().getContentResolver(),
                Settings.System.OMNI_CUSTOM_FP_ICON_ENABLED, 0, UserHandle.USER_CURRENT) == 1;
        mCustomFodIcon.setOnPreferenceChangeListener(this);
        if (val) {
            mUdfpsIconPicker.setEnabled(false);
        } else {
            mUdfpsIconPicker.setEnabled(true);
        }

        if (mPm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) &&
                 mFingerprintManager != null) {
            if (!mFingerprintManager.isHardwareDetected()){
                prefSet.removePreference(mFingerprintCategory);
                prefSet.removePreference(fpCategory);
            } else {
                mFingerprintSuccessVib.setChecked((Settings.System.getInt(getContentResolver(),
                        Settings.System.FP_SUCCESS_VIBRATE, 1) == 1));
                mFingerprintSuccessVib.setOnPreferenceChangeListener(this);
                mFingerprintErrorVib.setChecked((Settings.System.getInt(getContentResolver(),
                        Settings.System.FP_ERROR_VIBRATE, 1) == 1));
                mFingerprintErrorVib.setOnPreferenceChangeListener(this);
                if (UdfpsUtils.hasUdfpsSupport(getActivity())) {
                    mUdfpsHapticFeedback.setChecked((Settings.System.getInt(getContentResolver(),
                            Settings.System.ENABLE_UDFPS_START_HAPTIC_FEEDBACK, 1) == 1));
                    mUdfpsHapticFeedback.setOnPreferenceChangeListener(this);
                    mFodNightLight.setChecked((Settings.System.getInt(getContentResolver(),
                            Settings.System.FOD_NIGHT_LIGHT, 0) == 1));
                    mFodNightLight.setOnPreferenceChangeListener(this);
                    mScreenOffFOD.setChecked((Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_OFF_FOD, 1) == 1));
                    mScreenOffFOD.setOnPreferenceChangeListener(this);
                } else {
                    fpCategory.removePreference(mUdfpsHapticFeedback);
                    fpCategory.removePreference(mFodNightLight);
                    fpCategory.removePreference(mScreenOffFOD);
                }
            }
        } else {
            prefSet.removePreference(mFingerprintCategory);
            prefSet.removePreference(fpCategory);
        }

        boolean udfpsResPkgInstalled = AncientUtils.isPackageInstalled(getContext(),
                "org.ancient.udfps.resources");
        PreferenceCategory udfps = (PreferenceCategory) prefSet.findPreference("udfps_category");
        if (!udfpsResPkgInstalled) {
            prefSet.removePreference(udfps);
        }

        mAODPref = findPreference(AOD_SCHEDULE_KEY);
        updateAlwaysOnSummary();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mCustomFPImage) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFingerprintSuccessVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FP_SUCCESS_VIBRATE, value ? 1 : 0);
            return true;
        } else if (preference == mFingerprintErrorVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FP_ERROR_VIBRATE, value ? 1 : 0);
            return true;
        } else if (preference == mUdfpsHapticFeedback) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ENABLE_UDFPS_START_HAPTIC_FEEDBACK, value ? 1 : 0);
            return true;
        } else if (preference == mScreenOffFOD) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SCREEN_OFF_FOD, value ? 1 : 0);
            return true;
        } else if (preference == mFodNightLight) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FOD_NIGHT_LIGHT, value ? 0 : 0);
            return true;
        } else if (preference == mCustomFodIcon) {
            boolean val = (Boolean) newValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.OMNI_CUSTOM_FP_ICON_ENABLED, val ? 1 : 0,
                    UserHandle.USER_CURRENT);
            if (val) {
                mUdfpsIconPicker.setEnabled(false);
            } else {
                mUdfpsIconPicker.setEnabled(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
       if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
           Uri uri = null;
           if (result != null) {
               uri = result.getData();
               setPickerIcon(uri.toString());
               Settings.System.putString(getContentResolver(), Settings.System.OMNI_CUSTOM_FP_ICON,
                   uri.toString());
            }
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_CANCELED) {
            mCustomFPImage.setIcon(new ColorDrawable(Color.TRANSPARENT));
            Settings.System.putString(getContentResolver(), Settings.System.OMNI_CUSTOM_FP_ICON, "");
        }
    }

    private void setPickerIcon(String uri) {
        try {
                ParcelFileDescriptor parcelFileDescriptor =
                    getContext().getContentResolver().openFileDescriptor(Uri.parse(uri), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                Drawable d = new BitmapDrawable(getResources(), image);
                mCustomFPImage.setIcon(d);
            }
            catch (Exception e) {}
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }
}
