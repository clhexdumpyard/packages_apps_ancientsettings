/*
 * Copyright (C) 2019-2020 AOSP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ancient.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.RemoteException;
import android.os.Bundle;
import android.os.UserHandle;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;

import com.android.internal.util.ancient.AncientUtils;

import java.util.ArrayList;
import java.util.List;

public class SbMarginStylePreferenceController extends AbstractPreferenceController implements
        Preference.OnPreferenceChangeListener {

    private static final String ANCI_QS_MARGIN = "ANCI_QS_MARGIN";

    private ListPreference mSbMarginStyle;

    public SbMarginStylePreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return ANCI_QS_MARGIN;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mSbMarginStyle = screen.findPreference(ANCI_QS_MARGIN);
        int sbMarginStyle = Settings.System.getIntForUser(mContext.getContentResolver(),
                "ANCI_QS_MARGIN", 0, UserHandle.USER_CURRENT);
        int valueIndex = mSbMarginStyle.findIndexOfValue(String.valueOf(sbMarginStyle));
        mSbMarginStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mSbMarginStyle.setSummary(mSbMarginStyle.getEntry());
        mSbMarginStyle.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSbMarginStyle) {
            int sbMarginStyleValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(mContext.getContentResolver(),
                    "ANCI_QS_MARGIN", sbMarginStyleValue, UserHandle.USER_CURRENT);
            mSbMarginStyle.setSummary(mSbMarginStyle.getEntries()[sbMarginStyleValue]);
            AncientUtils.showSystemUiRestartDialog();
            return true;
        }
        return false;
    }
}
