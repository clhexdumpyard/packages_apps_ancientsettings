/*
 * Copyright (C) 2017-2019 The Dirty Unicorns Project
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

package com.ancient.settings.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.search.BaseSearchIndexProvider;

import com.android.internal.util.ancient.AncientUtils;

@SearchIndexable
public class FooterCarrierLabel extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_CUSTOM_FOOTERCARRIER_LABEL = "CUSTOM_FOOTERCARRIER_LABEL";
    private static final String KEY_STATUS_BAR_SHOW_FOOTERCARRIER = "STATUS_BAR_SHOW_FOOTERCARRIER";

    private ListPreference mShowCarrierLabel;
    private Preference mCustomCarrierLabel;

    private String mCustomCarrierLabelText;

    private int showCarrierLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ancient_footercarrier_label);
        final ContentResolver resolver = getActivity().getContentResolver();

        mShowCarrierLabel = (ListPreference) findPreference(KEY_STATUS_BAR_SHOW_FOOTERCARRIER);
        showCarrierLabel = Settings.System.getInt(resolver,
                "STATUS_BAR_SHOW_FOOTERCARRIER", 1);
        CharSequence[] NonNotchEntries = { getResources().getString(R.string.show_footercarrier_disabled),
                getResources().getString(R.string.show_footercarrier_footer) };
        CharSequence[] NotchEntries = { getResources().getString(R.string.show_footercarrier_disabled),
                getResources().getString(R.string.show_footercarrier_footer) };
        CharSequence[] NonNotchValues = {"0", "1"};
        CharSequence[] NotchValues = {"0", "1"};
        mShowCarrierLabel.setEntries(AncientUtils.hasNotch(getActivity()) ? NotchEntries : NonNotchEntries);
        mShowCarrierLabel.setEntryValues(AncientUtils.hasNotch(getActivity()) ? NotchValues : NonNotchValues);
        mShowCarrierLabel.setValue(String.valueOf(showCarrierLabel));
        mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntry());
        mShowCarrierLabel.setOnPreferenceChangeListener(this);
        mCustomCarrierLabel = (Preference) findPreference(KEY_CUSTOM_FOOTERCARRIER_LABEL);
        updateCustomLabelTextSummary();

        mCustomCarrierLabel.setEnabled(!mShowCarrierLabel.getEntryValues()
                [showCarrierLabel].equals("0"));
    }

    private void updateCustomLabelTextSummary() {
        mCustomCarrierLabelText = Settings.System.getString(
            getActivity().getContentResolver(), "CUSTOM_FOOTERCARRIER_LABEL");

        if (TextUtils.isEmpty(mCustomCarrierLabelText)) {
            mCustomCarrierLabel.setSummary(R.string.custom_footercarrier_label_notset);
        } else {
            mCustomCarrierLabel.setSummary(mCustomCarrierLabelText);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mShowCarrierLabel) {
            int showCarrierLabel = Integer.valueOf((String) newValue);
            int index = mShowCarrierLabel.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, "STATUS_BAR_SHOW_FOOTERCARRIER", showCarrierLabel);
            mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntries()[index]);
            mCustomCarrierLabel.setEnabled(!mShowCarrierLabel.getEntryValues()
                    [showCarrierLabel].equals("0"));
            return true;
         }
        return false;        
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(final Preference preference) {
        super.onPreferenceTreeClick(preference);
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference.getKey().equals(KEY_CUSTOM_FOOTERCARRIER_LABEL)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_footercarrier_label_title);
            alert.setMessage(R.string.custom_footercarrier_label_explain);

            LinearLayout container = new LinearLayout(getActivity());
            container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(55, 20, 55, 20);
            final EditText input = new EditText(getActivity());
            int maxLength = 10;
            input.setLayoutParams(lp);
            input.setGravity(android.view.Gravity.TOP| Gravity.START);
            input.setText(TextUtils.isEmpty(mCustomCarrierLabelText) ? "" : mCustomCarrierLabelText);
            input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
            container.addView(input);
            alert.setView(container);
            alert.setPositiveButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString().trim();
                            Settings.System.putString(resolver, "CUSTOM_FOOTERCARRIER_LABEL", value);
                            updateCustomLabelTextSummary();
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_CUSTOM_CARRIER_LABEL_CHANGED);
                            getActivity().sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
        }
        return true;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.ancient_footercarrier_label);
}