/*
 * Copyright (C) 2019 AospExtended ROM Project
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

import static android.view.DisplayCutout.BOUNDS_POSITION_LEFT;
import static android.view.DisplayCutout.BOUNDS_POSITION_RIGHT;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.SearchIndexableResource;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.ancient.AncientUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;

import com.ancient.settings.preferences.SystemSettingSwitchPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.ancient.settings.preferences.SystemSettingListPreference;

import com.android.settingslib.search.Indexable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClockDateSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String TAG = "ClockDateSettings";

    private static final String PREF_AM_PM_STYLE = "statusbar_clock_am_pm_style";
    private static final String PREF_CLOCK_DATE_DISPLAY = "statusbar_clock_date_display";
    private static final String PREF_CLOCK_DATE_STYLE = "statusbar_clock_date_style";
    private static final String PREF_CLOCK_DATE_FORMAT = "statusbar_clock_date_format";
    private static final String PREF_STATUS_BAR_CLOCK = "statusbar_clock";
    private static final String PREF_CLOCK_DATE_POSITION = "statusbar_clock_date_position";
    private static final String PREF_CLOCK_SECONDS = "statusbar_clock_seconds";
    private static final String PREF_CLOCK_STYLE = "statusbar_clock_style";
    
    private static final String STATUS_BAR_ANCI_CLOCK = "STATUS_BAR_ANCI_CLOCK";
    private static final String IDC_TRANSCLOCK_BG_STROKEKOLOR = "IDC_TRANSCLOCK_BG_STROKEKOLOR";
    private static final String IDC_TRANSCLOCK_BG_KOLOR = "IDC_TRANSCLOCK_BG_KOLOR";
    private static final String IDC_TRANSCLOCK_BG_GRADIENTA = "IDC_TRANSCLOCK_BG_GRADIENTA";
    private static final String IDC_TRANSCLOCK_BG_GRADIENTB = "IDC_TRANSCLOCK_BG_GRADIENTB";
    private static final String IDC_TRANSCLOCK_BG_GRADIENTC = "IDC_TRANSCLOCK_BG_GRADIENTC";

    public static final int CLOCK_DATE_STYLE_LOWERCASE = 1;
    public static final int CLOCK_DATE_STYLE_UPPERCASE = 2;
    private static final int CUSTOM_CLOCK_DATE_FORMAT_INDEX = 18;

    private ListPreference mClockAmPmStyle;
    private ListPreference mClockDateDisplay;
    private ListPreference mClockDateStyle;
    private ListPreference mClockDateFormat;
    private ListPreference mClockDatePosition;
    private ListPreference mStatusBarClockStyle;
    private SwitchPreference mStatusBarClock;
    private SwitchPreference mStatusBarSecondsShow;
    private SystemSettingSwitchPreference mtransSwitch;
    private ColorPickerPreference mColst;
    private ColorPickerPreference mColsi;
    private ColorPickerPreference mColga;
    private ColorPickerPreference mColgb;
    private ColorPickerPreference mColgc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ancient_settings_clock_date);

        mClockAmPmStyle = (ListPreference) findPreference(PREF_AM_PM_STYLE);
        mClockAmPmStyle.setOnPreferenceChangeListener(this);
        mClockAmPmStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE,
                0)));
        boolean is24hour = DateFormat.is24HourFormat(getActivity());
        if (is24hour) {
            mClockAmPmStyle.setSummary(R.string.status_bar_am_pm_info);
        } else {
            mClockAmPmStyle.setSummary(mClockAmPmStyle.getEntry());
        }
        mClockAmPmStyle.setEnabled(!is24hour);

        mClockDateDisplay = (ListPreference) findPreference(PREF_CLOCK_DATE_DISPLAY);
        mClockDateDisplay.setOnPreferenceChangeListener(this);
        mClockDateDisplay.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_DATE_DISPLAY,
                0)));
        mClockDateDisplay.setSummary(mClockDateDisplay.getEntry());

        mClockDateStyle = (ListPreference) findPreference(PREF_CLOCK_DATE_STYLE);
        mClockDateStyle.setOnPreferenceChangeListener(this);
        mClockDateStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_DATE_STYLE,
                0)));
        mClockDateStyle.setSummary(mClockDateStyle.getEntry());

        mClockDateFormat = (ListPreference) findPreference(PREF_CLOCK_DATE_FORMAT);
        mClockDateFormat.setOnPreferenceChangeListener(this);
        String value = Settings.System.getString(getActivity().getContentResolver(),
                Settings.System.STATUSBAR_CLOCK_DATE_FORMAT);
        if (value == null || value.isEmpty()) {
            value = "EEE";
        }
        int index = mClockDateFormat.findIndexOfValue((String) value);
        if (index == -1) {
            mClockDateFormat.setValueIndex(CUSTOM_CLOCK_DATE_FORMAT_INDEX);
        } else {
            mClockDateFormat.setValue(value);
        }

        parseClockDateFormats();

        mStatusBarClock = (SwitchPreference) findPreference(PREF_STATUS_BAR_CLOCK);
        mStatusBarClock.setChecked((Settings.System.getInt(
                getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUSBAR_CLOCK, 1) == 1));
        mStatusBarClock.setOnPreferenceChangeListener(this);

        mStatusBarSecondsShow = (SwitchPreference) findPreference(PREF_CLOCK_SECONDS);
        mStatusBarSecondsShow.setChecked((Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_SECONDS, 0) == 1));
        mStatusBarSecondsShow.setOnPreferenceChangeListener(this);

        mStatusBarClockStyle = (ListPreference) findPreference(PREF_CLOCK_STYLE);

        int clockStyle = Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_STYLE, 0);
        mStatusBarClockStyle.setValue(String.valueOf(clockStyle));
        mStatusBarClockStyle.setSummary(mStatusBarClockStyle.getEntry());
        mStatusBarClockStyle.setOnPreferenceChangeListener(this);

        mClockDatePosition = (ListPreference) findPreference(PREF_CLOCK_DATE_POSITION);
        mClockDatePosition.setOnPreferenceChangeListener(this);
        mClockDatePosition.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_DATE_POSITION,
                0)));
        mClockDatePosition.setSummary(mClockDatePosition.getEntry());

         boolean mClockDateToggle = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_DISPLAY, 0) != 0;
        if (!mClockDateToggle) {
            mClockDateStyle.setEnabled(false);
            mClockDateFormat.setEnabled(false);
            mClockDatePosition.setEnabled(false);
        }
        
        mtransSwitch = (SystemSettingSwitchPreference) findPreference(STATUS_BAR_ANCI_CLOCK);
        mtransSwitch.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                "STATUS_BAR_ANCI_CLOCK", 0) == 1));
        mtransSwitch.setOnPreferenceChangeListener(this);
        
        mColst = (ColorPickerPreference) findPreference(IDC_TRANSCLOCK_BG_STROKEKOLOR);
        int mColstColor = Settings.System.getInt(getContentResolver(),
                "IDC_TRANSCLOCK_BG_STROKEKOLOR", 0x00000000);
        mColst.setNewPreviewColor(mColstColor);
        mColst.setAlphaSliderEnabled(true);
        String mColstColorHex = String.format("#%08x", (0x00000000 & mColstColor));
        if (mColstColorHex.equals("#00000000")) {
            mColst.setSummary(R.string.color_default);
        } else {
            mColst.setSummary(mColstColorHex);
        }
        mColst.setOnPreferenceChangeListener(this);
        
        mColsi = (ColorPickerPreference) findPreference(IDC_TRANSCLOCK_BG_KOLOR);
        int mColsiColor = Settings.System.getInt(getContentResolver(),
                "IDC_TRANSCLOCK_BG_KOLOR", 0xffffffff);
        mColsi.setNewPreviewColor(mColsiColor);
        mColsi.setAlphaSliderEnabled(true);
        String mColsiColorHex = String.format("#%08x", (0xffffffff & mColsiColor));
        if (mColsiColorHex.equals("#ffffffff")) {
            mColsi.setSummary(R.string.color_default);
        } else {
            mColsi.setSummary(mColsiColorHex);
        }
        mColsi.setOnPreferenceChangeListener(this);
        
        mColga = (ColorPickerPreference) findPreference(IDC_TRANSCLOCK_BG_GRADIENTA);
        int mColgaColor = Settings.System.getInt(getContentResolver(),
                "IDC_TRANSCLOCK_BG_GRADIENTA", 0xffffffff);
        mColga.setNewPreviewColor(mColgaColor);
        mColga.setAlphaSliderEnabled(true);
        String mColgaColorHex = String.format("#%08x", (0xffffffff & mColgaColor));
        if (mColgaColorHex.equals("#ffffffff")) {
            mColga.setSummary(R.string.color_default);
        } else {
            mColga.setSummary(mColgaColorHex);
        }
        mColga.setOnPreferenceChangeListener(this);
        
        mColgb = (ColorPickerPreference) findPreference(IDC_TRANSCLOCK_BG_GRADIENTB);
        int mColgbColor = Settings.System.getInt(getContentResolver(),
                "IDC_TRANSCLOCK_BG_GRADIENTB", 0xffffffff);
        mColgb.setNewPreviewColor(mColgbColor);
        mColgb.setAlphaSliderEnabled(true);
        String mColgbColorHex = String.format("#%08x", (0xffffffff & mColgbColor));
        if (mColgbColorHex.equals("#ffffffff")) {
            mColgb.setSummary(R.string.color_default);
        } else {
            mColgb.setSummary(mColgbColorHex);
        }
        mColgb.setOnPreferenceChangeListener(this);
        
        mColgc = (ColorPickerPreference) findPreference(IDC_TRANSCLOCK_BG_GRADIENTC);
        int mColgcColor = Settings.System.getInt(getContentResolver(),
                "IDC_TRANSCLOCK_BG_GRADIENTC", 0xffffffff);
        mColgc.setNewPreviewColor(mColgcColor);
        mColgc.setAlphaSliderEnabled(true);
        String mColgcColorHex = String.format("#%08x", (0xffffffff & mColgcColor));
        if (mColgcColorHex.equals("#ffffffff")) {
            mColgc.setSummary(R.string.color_default);
        } else {
            mColgc.setSummary(mColgcColorHex);
        }
        mColgc.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        final boolean hasNotch = AncientUtils.hasNotch(getActivity());
        final int notchType = AncientUtils.getCutoutType(getActivity());
        Log.v(TAG,"notchType: " + notchType);

        // Adjust status bar preferences for RTL
        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            if (hasNotch && !(notchType == BOUNDS_POSITION_LEFT || notchType == BOUNDS_POSITION_RIGHT)) {
                mStatusBarClockStyle.setEntries(R.array.statusbar_clock_style_entries_notch_rtl);
                mStatusBarClockStyle.setEntryValues(R.array.statusbar_clock_style_values_notch_rtl);
            } else {
                mStatusBarClockStyle.setEntries(R.array.statusbar_clock_style_entries_rtl);
                mStatusBarClockStyle.setEntryValues(R.array.statusbar_clock_style_values_rtl);
            }
        } else if (hasNotch && !(notchType == BOUNDS_POSITION_LEFT || notchType == BOUNDS_POSITION_RIGHT)) {
            mStatusBarClockStyle.setEntries(R.array.statusbar_clock_style_entries_notch);
            mStatusBarClockStyle.setEntryValues(R.array.statusbar_clock_style_values_notch);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        AlertDialog dialog;

        if (preference == mClockAmPmStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockAmPmStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE, val);
            mClockAmPmStyle.setSummary(mClockAmPmStyle.getEntries()[index]);
            return true;
        } else if (preference == mClockDateDisplay) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockDateDisplay.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_DISPLAY, val);
            mClockDateDisplay.setSummary(mClockDateDisplay.getEntries()[index]);
            if (val == 0) {
                mClockDateStyle.setEnabled(false);
                mClockDateFormat.setEnabled(false);
                mClockDatePosition.setEnabled(false);
            } else {
                mClockDateStyle.setEnabled(true);
                mClockDateFormat.setEnabled(true);
                mClockDatePosition.setEnabled(true);
            }
            return true;
        } else if (preference == mClockDateStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockDateStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_STYLE, val);
            mClockDateStyle.setSummary(mClockDateStyle.getEntries()[index]);
            parseClockDateFormats();
            return true;
        } else if (preference == mStatusBarClock) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mClockDateFormat) {
            int index = mClockDateFormat.findIndexOfValue((String) newValue);

             if (index == CUSTOM_CLOCK_DATE_FORMAT_INDEX) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.clock_date_string_edittext_title);
                alert.setMessage(R.string.clock_date_string_edittext_summary);

                final EditText input = new EditText(getActivity());
                String oldText = Settings.System.getString(
                    getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_FORMAT);
                if (oldText != null) {
                    input.setText(oldText);
                }
                alert.setView(input);

                alert.setPositiveButton(R.string.menu_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        String value = input.getText().toString();
                        if (value.equals("")) {
                            return;
                        }
                        Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.STATUSBAR_CLOCK_DATE_FORMAT, value);
                         return;
                    }
                });

                alert.setNegativeButton(R.string.menu_cancel,
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        return;
                    }
                });
                dialog = alert.create();
                dialog.show();
            } else {
                if ((String) newValue != null) {
                    Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_CLOCK_DATE_FORMAT, (String) newValue);
                }
            }
            return true;
        } else if (preference == mClockDatePosition) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockDatePosition.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_POSITION, val);
            mClockDatePosition.setSummary(mClockDatePosition.getEntries()[index]);
            parseClockDateFormats();
            return true;
        } else if (preference == mStatusBarClockStyle) {
            int clockStyle = Integer.parseInt((String) newValue);
            int index = mStatusBarClockStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_STYLE, clockStyle);
            mStatusBarClockStyle.setSummary(mStatusBarClockStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarSecondsShow) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_SECONDS, value ? 1 : 0);
            return true;
        } else if (preference == mtransSwitch) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    "STATUS_BAR_ANCI_CLOCK", value ? 1 : 0);
            return true;
        } else if (preference == mColst) {
            String hexst = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexst.equals("#00000000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexst);
            }
            int intHexst = ColorPickerPreference.convertToColorInt(hexst);
            Settings.System.putInt(getContentResolver(),
                    "IDC_TRANSCLOCK_BG_STROKEKOLOR", intHexst);
            return true;
        } else if (preference == mColsi) {
            String hexsi = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexsi.equals("#ffffffff")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexsi);
            }
            int intHexsi = ColorPickerPreference.convertToColorInt(hexsi);
            Settings.System.putInt(getContentResolver(),
                    "IDC_TRANSCLOCK_BG_KOLOR", intHexsi);
            return true;
        } else if (preference == mColga) {
            String hexga = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexga.equals("#ffffffff")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexga);
            }
            int intHexga = ColorPickerPreference.convertToColorInt(hexga);
            Settings.System.putInt(getContentResolver(),
                    "IDC_TRANSCLOCK_BG_GRADIENTA", intHexga);
            return true;
        } else if (preference == mColgb) {
            String hexgb = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexgb.equals("#ffffffff")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexgb);
            }
            int intHexga = ColorPickerPreference.convertToColorInt(hexgb);
            Settings.System.putInt(getContentResolver(),
                    "IDC_TRANSCLOCK_BG_GRADIENTB", intHexga);
            return true;
        } else if (preference == mColgc) {
            String hexgc = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexgc.equals("#ffffffff")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexgc);
            }
            int intHexgc = ColorPickerPreference.convertToColorInt(hexgc);
            Settings.System.putInt(getContentResolver(),
                    "IDC_TRANSCLOCK_BG_GRADIENTC", intHexgc);
            return true;
        }
        return false;
    }

    private void parseClockDateFormats() {
        String[] dateEntries = getResources().getStringArray(R.array.clock_date_format_entries_values);
        CharSequence parsedDateEntries[];
        parsedDateEntries = new String[dateEntries.length];
        Date now = new Date();
         int lastEntry = dateEntries.length - 1;
        int dateFormat = Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_DATE_STYLE, 0);
        for (int i = 0; i < dateEntries.length; i++) {
            if (i == lastEntry) {
                parsedDateEntries[i] = dateEntries[i];
            } else {
                String newDate;
                CharSequence dateString = DateFormat.format(dateEntries[i], now);
                if (dateFormat == CLOCK_DATE_STYLE_LOWERCASE) {
                    newDate = dateString.toString().toLowerCase();
                } else if (dateFormat == CLOCK_DATE_STYLE_UPPERCASE) {
                    newDate = dateString.toString().toUpperCase();
                } else {
                    newDate = dateString.toString();
                }
                 parsedDateEntries[i] = newDate;
            }
        }
        mClockDateFormat.setEntries(parsedDateEntries);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    final ArrayList<SearchIndexableResource> result = new ArrayList<>();
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.ancient_settings_clock_date;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    final List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}
