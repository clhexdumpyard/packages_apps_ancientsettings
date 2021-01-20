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
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;

import java.util.ArrayList;
import java.util.List;

public class AnAclockStylePreferenceController extends AbstractPreferenceController implements
        Preference.OnPreferenceChangeListener {

    private static final String ACLOCK_STYLE = "aclock_style";

    private ListPreference mAnAclockStyle;

    public AnAclockStylePreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return ACLOCK_STYLE;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mAnAclockStyle = screen.findPreference(ACLOCK_STYLE);
        int anAclockStyle = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.ACLOCK_STYLE, 0, UserHandle.USER_CURRENT);
        int valueIndex = mAnAclockStyle.findIndexOfValue(String.valueOf(anAclockStyle));
        mAnAclockStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mAnAclockStyle.setSummary(mAnAclockStyle.getEntry());
        mAnAclockStyle.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAnAclockStyle) {
            int anAclockStyleValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(mContext.getContentResolver(),
                    Settings.System.ACLOCK_STYLE, anAclockStyleValue, UserHandle.USER_CURRENT);
            mAnAclockStyle.setSummary(mAnAclockStyle.getEntries()[anAclockStyleValue]);
            return true;
        }
        return false;
    }
}
