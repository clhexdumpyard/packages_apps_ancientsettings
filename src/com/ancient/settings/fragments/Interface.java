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

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.MonetWannabe;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;

import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import androidx.preference.*;
import androidx.preference.Preference.OnPreferenceChangeListener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.os.UserHandle.USER_SYSTEM;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.display.OverlayCategoryPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.ancient.settings.display.AccentColorPreferenceController;
import com.ancient.settings.display.QsTileStylePreferenceController;
import com.ancient.settings.display.DgCornerStylePreferenceController;
import com.ancient.settings.display.SbHeightStylePreferenceController;
import com.ancient.settings.display.SbNavStylePreferenceController;
import com.ancient.settings.display.AnSwitchStylePreferenceController;
import com.ancient.settings.display.AnAclockStylePreferenceController;
import com.ancient.settings.display.SbQsbgStylePreferenceController;
import com.ancient.settings.display.SbPaddingStylePreferenceController;
import com.ancient.settings.preferences.SystemSettingListPreference;
import com.ancient.settings.preferences.SystemSettingSwitchPreference;
import com.ancient.settings.preferences.SystemSettingSeekBarPreference;
import com.ancient.settings.preferences.SecureSettingSwitchPreference;
import com.ancient.settings.preferences.SecureSettingSeekBarPreference;
import com.ancient.settings.preferences.SecureSettingListPreference;

import com.android.internal.util.ancient.ThemesUtils;
import com.android.internal.util.ancient.AncientUtils;
import com.android.internal.graphics.palette.Palette;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.settings.R;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Interface extends DashboardFragment implements
        OnPreferenceChangeListener, Indexable {

    private static final String TAG = "Interface";
    private static final String PREF_THEME_SWITCH = "theme_switch";
    private static final String AVATARVIEWVIS = "AvatarViewVis";
    private static final String ANCIENT_COLLAPSED_BASE_STYLE = "ancient_collapsed_base_style";
    private static final String COLLAPSEONOFF = "collapseonoff";
    private static final String ANCIENT_HOMEPAGE_BACKGROUND = "ancient_homepage_background";
    private static final String ANCIENT_COLLAPSE_HEADER = "ancient_collapse_header";
    private static final String HOMECOLLAPSEONOFF = "homecollapseonoff";
    private static final String ANCIENT_COLLAPSETOOL_BG = "ancient_collapsetool_bg";
    private static final String ANCI_QS_MARGIN = "ANCI_QS_MARGIN";
    private static final String ANCI_STATUSBAR_ICON = "ANCI_STATUSBAR_ICON";
    private static final String ANCI_SHAPE_ICON = "ANCI_SHAPE_ICON";
    private static final String BOUNCYONOFF = "bouncyonoff";
    private static final String HOMEBOLDONOFF = "homeboldonoff";
    private static final String ANCI_CUSTOM_TOPLEVEL = "ANCI_CUSTOM_TOPLEVEL";
    private static final String DATA_CON_STYLE = "DATA_CON_STYLE";
    private static final String JEMBT_LEBAT_KIRI = "JEMBT_LEBAT_KIRI";
    private static final String JEMBT_LEBAT_KANAN = "JEMBT_LEBAT_KANAN";
    private static final String PREF_RGB_LIGHT_ACCENT_PICKER = "rgb_light_accent_picker";
    private static final String PREF_RGB_DARK_ACCENT_PICKER = "rgb_dark_accent_picker";
    private static final String PREF_MONET_ENGINE = "monet_engine";
    private static final String PREF_MONET_PALETTE = "monet_palette";
    private static final String PREF_FONTER_STYLE = "FONTER_STYLE";
    private static final String PREF_QS_TO_STOCK = "QS_TO_STOCK";
    private static final String PREF_QQS_CLOCKFAKE_SWITCH = "QQS_CLOCKFAKE_SWITCH";
    private static final String PREF_ANCI_HEADER_HEIGHT = "ANCI_HEADER_HEIGHT";
    private static final String PREF_QS_CLOCK_WARNA = "QS_CLOCK_WARNA";
    private static final String PREF_JAM_HEADER_GRAVITY = "JAM_HEADER_GRAVITY";
    private static final String PREF_ANCI_PANNELTOP_PAD = "ANCI_PANNELTOP_PAD";
    private static final String PREF_ANCI_BI_CLOCK = "ANCI_BI_CLOCK";
    private static final String PREF_ANCI_TOP_PAD = "ANCI_TOP_PAD";
    private static final String PREF_ANCIENT_UI_QSCLOCK_STYLE = "ANCIENT_UI_QSCLOCK_STYLE";
    private static final String PREF_ANCIENT_UI_QQSCLOCK_GRAVITY = "ANCIENT_UI_QQSCLOCK_GRAVITY";
    private static final String PREF_ANCIENT_UI_QSCLOCK_GRAVITY = "ANCIENT_UI_QSCLOCK_GRAVITY";
    private static final String PREF_JAM_HEADER_SIZE = "JAM_HEADER_SIZE";
	
    private IOverlayManager mOverlayService;
    private UiModeManager mUiModeManager;

    private ListPreference mThemeSwitch;
    private SystemSettingListPreference mAvatarViewVis;
    private SystemSettingListPreference mAncientCollapsedBaseStyle;
    private SystemSettingSwitchPreference mAncientCollapsedOnoff;
    private ListPreference mAncientHomepageBackground;
    private SystemSettingListPreference mAncientCollapseHeader;
    private SystemSettingListPreference mAncientHomeCollapsedOnoff;
    private SystemSettingListPreference mAncientCollapseToolBg;
    private SystemSettingListPreference mSbMarginStyle;
    private SystemSettingListPreference mSbStatbarIconStyle;
    private SystemSettingListPreference mSbShapeIconStyle;
    private SystemSettingSwitchPreference mAncientBouncyOnoff;
    private SystemSettingSwitchPreference mAncientBoldOnoff;
    private SystemSettingListPreference mAncientCusTop;
    private SystemSettingListPreference mAnciDatacon;
    private SystemSettingSeekBarPreference mPkiri;
    private SystemSettingSeekBarPreference mPkanan;
    private ColorPickerPreference rgbLiAccentPicker;
    private ColorPickerPreference rgbDaAccentPicker;
    private SecureSettingSwitchPreference mMonetOnoff;
    private ListPreference mMonetPallete;
    private SystemSettingListPreference mFonterStyle;
    private SystemSettingListPreference mAncientuiOnoff;
    private SystemSettingSwitchPreference mAnciHeadclockOnoff;
    private SystemSettingSeekBarPreference mAnciHeadSize;
    private SystemSettingListPreference mJamPalsu;
    private SystemSettingListPreference mJamhedgrav;
    private SystemSettingSeekBarPreference mPanneltop;
    private SystemSettingSeekBarPreference mBi;	
    private SystemSettingSeekBarPreference mTopad;
    private SystemSettingListPreference mAncUIa;
    private SystemSettingListPreference mAncUIb;
    private SystemSettingListPreference mAncUIc;
    private SystemSettingListPreference mAncUId;
	
    private static final int VIBRANT = 0;
    private static final int LIGHT_VIBRANT = 1;
    private static final int DARK_VIBRANT = 2;
    private static final int DOMINANT = 3;
    private static final int MUTED = 4;
    private static final int LIGHT_MUTED = 5;
    private static final int DARK_MUTED = 6;

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.ancient_settings_interface;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mUiModeManager = getContext().getSystemService(UiModeManager.class);

        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));

        setupThemeSwitchPref();

        mAvatarViewVis = (SystemSettingListPreference) findPreference("AvatarViewVis");
        mAvatarViewVis.setOnPreferenceChangeListener(this);
        mAncientCollapsedBaseStyle = (SystemSettingListPreference) findPreference("ancient_collapsed_base_style");
        mAncientCollapsedBaseStyle.setOnPreferenceChangeListener(this);
        mAncientHomepageBackground = (ListPreference) findPreference("ancient_homepage_background");
        mAncientHomepageBackground.setOnPreferenceChangeListener(this);
        mAncientCollapseHeader = (SystemSettingListPreference) findPreference("ancient_collapse_header");
        mAncientCollapseHeader.setOnPreferenceChangeListener(this);
        mAncientCollapseToolBg = (SystemSettingListPreference) findPreference("ancient_collapsetool_bg");
        mAncientCollapseToolBg.setOnPreferenceChangeListener(this);

        mSbMarginStyle = (SystemSettingListPreference) findPreference("ANCI_QS_MARGIN");
        int sbMarginStyle = Settings.System.getIntForUser(getContentResolver(),
                "ANCI_QS_MARGIN", 0, UserHandle.USER_CURRENT);
        int valueIndex = mSbMarginStyle.findIndexOfValue(String.valueOf(sbMarginStyle));
        mSbMarginStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mSbMarginStyle.setSummary(mSbMarginStyle.getEntry());
        mSbMarginStyle.setOnPreferenceChangeListener(this);

        mSbStatbarIconStyle = (SystemSettingListPreference) findPreference("ANCI_STATUSBAR_ICON");
        int sbStatbarIconStyle = Settings.System.getIntForUser(getContentResolver(),
                "ANCI_STATUSBAR_ICON", 0, UserHandle.USER_CURRENT);
        int valueIndexIcon = mSbStatbarIconStyle.findIndexOfValue(String.valueOf(sbStatbarIconStyle));
        mSbStatbarIconStyle.setValueIndex(valueIndexIcon >= 0 ? valueIndexIcon : 0);
        mSbStatbarIconStyle.setSummary(mSbStatbarIconStyle.getEntry());
        mSbStatbarIconStyle.setOnPreferenceChangeListener(this);

        mSbShapeIconStyle = (SystemSettingListPreference) findPreference("ANCI_SHAPE_ICON");
        int sbShapeIconStyle = Settings.System.getIntForUser(getContentResolver(),
                "ANCI_SHAPE_ICON", 0, UserHandle.USER_CURRENT);
        int valueIndexShape = mSbShapeIconStyle.findIndexOfValue(String.valueOf(sbShapeIconStyle));
        mSbShapeIconStyle.setValueIndex(valueIndexShape >= 0 ? valueIndexShape : 0);
        mSbShapeIconStyle.setSummary(mSbShapeIconStyle.getEntry());
        mSbShapeIconStyle.setOnPreferenceChangeListener(this);

        mAncientBouncyOnoff = (SystemSettingSwitchPreference) findPreference("bouncyonoff");
        mAncientBouncyOnoff.setOnPreferenceChangeListener(this);

        mAncientBoldOnoff = (SystemSettingSwitchPreference) findPreference("homeboldonoff");
        mAncientBoldOnoff.setOnPreferenceChangeListener(this);

        mAncientCusTop = (SystemSettingListPreference) findPreference("ANCI_CUSTOM_TOPLEVEL");
        int sbSeetingStyle  = Settings.System.getIntForUser(getContentResolver(),
                "ANCI_CUSTOM_TOPLEVEL", 0, UserHandle.USER_CURRENT);
        int valueIndexSeet = mAncientCusTop.findIndexOfValue(String.valueOf(sbSeetingStyle ));
        mAncientCusTop.setValueIndex(valueIndexSeet >= 0 ? valueIndexSeet : 0);
        mAncientCusTop.setSummary(mAncientCusTop.getEntry());
        mAncientCusTop.setOnPreferenceChangeListener(this);

        mAnciDatacon = (SystemSettingListPreference) findPreference("DATA_CON_STYLE");
        int sbDataconStyle  = Settings.System.getIntForUser(getContentResolver(),
                "DATA_CON_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndexDat = mAnciDatacon.findIndexOfValue(String.valueOf(sbDataconStyle));
        mAnciDatacon.setValueIndex(valueIndexDat >= 0 ? valueIndexDat : 0);
        mAnciDatacon.setSummary(mAnciDatacon.getEntry());
        mAnciDatacon.setOnPreferenceChangeListener(this);

        mPkiri = (SystemSettingSeekBarPreference) findPreference(JEMBT_LEBAT_KIRI);
        int kirik = Settings.System.getInt(getContentResolver(),
                "JEMBT_LEBAT_KIRI", 0);
        mPkiri.setValue(kirik);
        mPkiri.setOnPreferenceChangeListener(this);

        mPkanan = (SystemSettingSeekBarPreference) findPreference(JEMBT_LEBAT_KANAN);
        int kuntul = Settings.System.getInt(getContentResolver(),
                "JEMBT_LEBAT_KANAN", 0);
        mPkanan.setValue(kuntul);
        mPkanan.setOnPreferenceChangeListener(this);

        rgbLiAccentPicker = (ColorPickerPreference) findPreference(PREF_RGB_LIGHT_ACCENT_PICKER);
        String colorVala = Settings.Secure.getStringForUser(getContentResolver(),
                "accent_light", UserHandle.USER_CURRENT);
        int colora = (colorVala == null)
                ? Color.WHITE
                : Color.parseColor("#" + colorVala);
        rgbLiAccentPicker.setNewPreviewColor(colora);
        rgbLiAccentPicker.setOnPreferenceChangeListener(this);

        rgbDaAccentPicker = (ColorPickerPreference) findPreference(PREF_RGB_DARK_ACCENT_PICKER);
        String colorValb = Settings.Secure.getStringForUser(getContentResolver(),
                "accent_dark", UserHandle.USER_CURRENT);
        int colorb = (colorValb == null)
                ? Color.WHITE
                : Color.parseColor("#" + colorValb);
        rgbDaAccentPicker.setNewPreviewColor(colorb);
        rgbDaAccentPicker.setOnPreferenceChangeListener(this);

        mMonetOnoff = (SecureSettingSwitchPreference) findPreference(PREF_MONET_ENGINE);
        mMonetOnoff.setChecked((Settings.Secure.getInt(getActivity().getContentResolver(),
                Settings.Secure.MONET_ENGINE, 0) == 1));
        int mMonetSwitch = Settings.Secure.getInt(getActivity().getContentResolver(),
                Settings.Secure.MONET_ENGINE, 0);
	if (mMonetSwitch == 0) {
            rgbLiAccentPicker.setEnabled(true);
            rgbDaAccentPicker.setEnabled(true);
        } else {
            rgbLiAccentPicker.setEnabled(false);
            rgbDaAccentPicker.setEnabled(false);
        }
        mMonetOnoff.setOnPreferenceChangeListener(this);
       
        int paletteType = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.MONET_PALETTE, VIBRANT, UserHandle.USER_CURRENT);
        mMonetPallete = findPreference("monet_palette");
        mMonetPallete.setValue(String.valueOf(paletteType));
        mMonetPallete.setSummary(mMonetPallete.getEntry());
        mMonetPallete.setOnPreferenceChangeListener(this);   

        mFonterStyle = (SystemSettingListPreference) findPreference(PREF_FONTER_STYLE);
        int anFonterStyle = Settings.System.getIntForUser(getContentResolver(),
                "FONTER_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndexFont = mFonterStyle.findIndexOfValue(String.valueOf(anFonterStyle));
        mFonterStyle.setValueIndex(valueIndexFont >= 0 ? valueIndexFont : 0);
        mFonterStyle.setSummary(mFonterStyle.getEntry());
        mFonterStyle.setOnPreferenceChangeListener(this);

        mAnciHeadclockOnoff = (SystemSettingSwitchPreference) findPreference(PREF_QQS_CLOCKFAKE_SWITCH);
        mAnciHeadclockOnoff.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                "QQS_CLOCKFAKE_SWITCH", 0) == 1));
        mAnciHeadclockOnoff.setOnPreferenceChangeListener(this);

        mAnciHeadSize = (SystemSettingSeekBarPreference) findPreference(PREF_ANCI_HEADER_HEIGHT);
        mAnciHeadSize.setOnPreferenceChangeListener(this);
	    
	mPanneltop = (SystemSettingSeekBarPreference) findPreference(PREF_ANCI_PANNELTOP_PAD);
        mPanneltop.setOnPreferenceChangeListener(this);
	
	mBi = (SystemSettingSeekBarPreference) findPreference(PREF_ANCI_BI_CLOCK);
        mBi.setOnPreferenceChangeListener(this);
	
	mTopad = (SystemSettingSeekBarPreference) findPreference(PREF_ANCI_TOP_PAD);
        mTopad.setOnPreferenceChangeListener(this);    

        mJamPalsu = (SystemSettingListPreference) findPreference(PREF_QS_CLOCK_WARNA);
        int setWarna = Settings.System.getIntForUser(getContentResolver(),
                "QS_CLOCK_WARNA", 0, UserHandle.USER_CURRENT);
        int valueIndexWarna = mJamPalsu.findIndexOfValue(String.valueOf(setWarna));
        mJamPalsu.setValueIndex(valueIndexWarna >= 0 ? valueIndexWarna : 0);
        mJamPalsu.setSummary(mJamPalsu.getEntry());
        mJamPalsu.setOnPreferenceChangeListener(this);

	mAncientCollapsedOnoff = (SystemSettingSwitchPreference) findPreference("collapseonoff");
        mAncientCollapsedOnoff.setOnPreferenceChangeListener(this);
	    
	mAncientHomeCollapsedOnoff = (SystemSettingListPreference) findPreference("homecollapseonoff");
	int homecollapse = Settings.System.getInt(getActivity().getContentResolver(),
		"homecollapseonoff", 0);  
	if (homecollapse == 0) {
            mAncientBoldOnoff.setEnabled(false);
	    mAvatarViewVis.setEnabled(false);
        } else {
	    mAncientBoldOnoff.setEnabled(true);
	    mAvatarViewVis.setEnabled(true);
        }    
        mAncientHomeCollapsedOnoff.setOnPreferenceChangeListener(this);   
	    
	mJamhedgrav = (SystemSettingListPreference) findPreference(PREF_JAM_HEADER_GRAVITY); 
	int jamhedgrav = Settings.System.getIntForUser(getContentResolver(),
                "JAM_HEADER_GRAVITY", 0, UserHandle.USER_CURRENT);
        int jamhedgrava = mJamhedgrav.findIndexOfValue(String.valueOf(jamhedgrav));
        mJamhedgrav.setValueIndex(jamhedgrava >= 0 ? jamhedgrava : 0);
        mJamhedgrav.setSummary(mJamhedgrav.getEntry());
        mJamhedgrav.setOnPreferenceChangeListener(this); 
	    
	mAncUIa = (SystemSettingListPreference) findPreference("ANCIENT_UI_QSCLOCK_STYLE");
        int sbAncUIaStyle = Settings.System.getIntForUser(getContentResolver(),
                "ANCIENT_UI_QSCLOCK_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndexAncUIa = mAncUIa.findIndexOfValue(String.valueOf(sbAncUIaStyle));
        mAncUIa.setValueIndex(valueIndexAncUIa >= 0 ? valueIndexAncUIa : 0);
        mAncUIa.setSummary(mAncUIa.getEntry());
        mAncUIa.setOnPreferenceChangeListener(this);
		
	mAncUIb = (SystemSettingListPreference) findPreference("ANCIENT_UI_QQSCLOCK_GRAVITY");
        int sbAncUIbStyle = Settings.System.getIntForUser(getContentResolver(),
                "ANCIENT_UI_QQSCLOCK_GRAVITY", 0, UserHandle.USER_CURRENT);
        int valueIndexAncUIb = mAncUIb.findIndexOfValue(String.valueOf(sbAncUIbStyle));
        mAncUIb.setValueIndex(valueIndexAncUIb >= 0 ? valueIndexAncUIb : 0);
        mAncUIb.setSummary(mAncUIb.getEntry());
        mAncUIb.setOnPreferenceChangeListener(this);
		
	mAncUIc = (SystemSettingListPreference) findPreference("ANCIENT_UI_QSCLOCK_GRAVITY");
        int sbAncUIcStyle = Settings.System.getIntForUser(getContentResolver(),
                "ANCIENT_UI_QSCLOCK_GRAVITY", 0, UserHandle.USER_CURRENT);
        int valueIndexAncUIc = mAncUIc.findIndexOfValue(String.valueOf(sbAncUIcStyle));
        mAncUIc.setValueIndex(valueIndexAncUIc >= 0 ? valueIndexAncUIc : 0);
        mAncUIc.setSummary(mAncUIc.getEntry());
        mAncUIc.setOnPreferenceChangeListener(this);   
	    
	mAncUId = (SystemSettingListPreference) findPreference("JAM_HEADER_SIZE");
        int sbAncUIdStyle = Settings.System.getIntForUser(getContentResolver(),
                "JAM_HEADER_SIZE", 0, UserHandle.USER_CURRENT);
        int valueIndexAncUId = mAncUId.findIndexOfValue(String.valueOf(sbAncUIdStyle));
        mAncUId.setValueIndex(valueIndexAncUId >= 0 ? valueIndexAncUId : 0);
        mAncUId.setSummary(mAncUId.getEntry());
        mAncUId.setOnPreferenceChangeListener(this);      
	    
	mAncientuiOnoff = (SystemSettingListPreference) findPreference(PREF_QS_TO_STOCK);
        int setAnciToStock = Settings.System.getIntForUser(getContentResolver(),
                "QS_TO_STOCK", 0, UserHandle.USER_CURRENT);
        int valueAnciToStock = mAncientuiOnoff.findIndexOfValue(String.valueOf(setAnciToStock));
        mAncientuiOnoff.setValueIndex(valueAnciToStock >= 0 ? valueAnciToStock : 0);
        mAncientuiOnoff.setSummary(mAncientuiOnoff.getEntry());
        int mAnciToStock = Settings.System.getInt(getActivity().getContentResolver(),
                "QS_TO_STOCK", 0);
        if (mAnciToStock == 1) {
	    mAnciHeadclockOnoff.setEnabled(false);
	    mAnciHeadSize.setEnabled(false);
	    mJamhedgrav.setEnabled(false);
	    mPanneltop.setEnabled(true);
    	    mBi.setEnabled(true);
    	    mTopad.setEnabled(true);
	    mAncUIa.setEnabled(true);
            mAncUIb.setEnabled(true);
            mAncUIc.setEnabled(true);
	    mAncUId.setEnabled(false);
	    mJamPalsu.setEnabled(false);  
	} else if (mAnciToStock == 2) {
	    mAnciHeadclockOnoff.setEnabled(false);
	    mAnciHeadSize.setEnabled(true);
	    mJamhedgrav.setEnabled(false);
	    mPanneltop.setEnabled(false);
    	    mBi.setEnabled(false);
    	    mTopad.setEnabled(false);
	    mAncUIa.setEnabled(false);
            mAncUIb.setEnabled(false);
            mAncUIc.setEnabled(false);	
	    mJamPalsu.setEnabled(false); 
	    mAncUId.setEnabled(false);
        } else {
            mAnciHeadclockOnoff.setEnabled(true);
	    mAnciHeadSize.setEnabled(true);
	    mJamhedgrav.setEnabled(true);
	    mPanneltop.setEnabled(false);
    	    mBi.setEnabled(false);
    	    mTopad.setEnabled(false);
	    mAncUIa.setEnabled(false);
            mAncUIb.setEnabled(false);
            mAncUIc.setEnabled(false);	
	    mJamPalsu.setEnabled(true); 
	    mAncUId.setEnabled(true);
        }
        mAncientuiOnoff.setOnPreferenceChangeListener(this);    
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new QsTileStylePreferenceController(context));
        controllers.add(new DgCornerStylePreferenceController(context));
        controllers.add(new SbNavStylePreferenceController(context));
        controllers.add(new AnSwitchStylePreferenceController(context));
        controllers.add(new AnAclockStylePreferenceController(context));
        //controllers.add(new SbBrightnStylePreferenceController(context));
        controllers.add(new SbQsbgStylePreferenceController(context));
        return controllers;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mThemeSwitch) {
            String theme_switch = (String) objValue;
            final Context context = getContext();
            switch (theme_switch) {
                case "1":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.PITCH_BLACK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.DARK_GREY);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.MATERIAL_OCEAN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_NO, ThemesUtils.ANCIENT_CLEAR);
                    break;
                case "2":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.DARK_GREY);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.MATERIAL_OCEAN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.ANCIENT_CLEAR);
                    break;
                case "3":
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.DARK_GREY);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.MATERIAL_OCEAN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.ANCIENT_CLEAR);
                    break;
                case "4":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.DARK_GREY);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.MATERIAL_OCEAN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.ANCIENT_CLEAR);
                    break;
                case "5":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.DARK_GREY);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.MATERIAL_OCEAN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.ANCIENT_CLEAR);
                    break;
                case "6":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.DARK_GREY);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.MATERIAL_OCEAN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.ANCIENT_CLEAR);
                    break;
                case "7":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.DARK_GREY);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.MATERIAL_OCEAN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.ANCIENT_CLEAR);
                    break;
                case "8":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.DARK_GREY);
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.MATERIAL_OCEAN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.ANCIENT_CLEAR);
                    break;
                case "9":
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.SOLARIZED_DARK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.BAKED_GREEN);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.CHOCO_X);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.PITCH_BLACK);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.DARK_GREY);
                    handleBackgrounds(false, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.MATERIAL_OCEAN);
                    handleBackgrounds(true, context, UiModeManager.MODE_NIGHT_YES, ThemesUtils.ANCIENT_CLEAR);
                    break;
            }
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == mAvatarViewVis) {
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;
        } else if (preference == mAncientCollapsedBaseStyle) {
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;
        } else if (preference == mAncientHomepageBackground) {
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;
        } else if (preference == mAncientCollapseHeader) {
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;
        } else if (preference == mAncientCollapseToolBg) {
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;
        } else if (preference == mSbMarginStyle) {
            int sbMarginStyleValue = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "ANCI_QS_MARGIN", sbMarginStyleValue, UserHandle.USER_CURRENT);
            mSbMarginStyle.setSummary(mSbMarginStyle.getEntries()[sbMarginStyleValue]);
            AncientUtils.showSystemUiRestartDialog(getContext());
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == mSbStatbarIconStyle) {
            int sbStatbarIconStyleValue = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "ANCI_STATUSBAR_ICON", sbStatbarIconStyleValue, UserHandle.USER_CURRENT);
            mSbStatbarIconStyle.setSummary(mSbStatbarIconStyle.getEntries()[sbStatbarIconStyleValue]);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == mSbShapeIconStyle) {
            int sbShapeIconStyleValue = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "ANCI_SHAPE_ICON", sbShapeIconStyleValue, UserHandle.USER_CURRENT);
            mSbShapeIconStyle.setSummary(mSbShapeIconStyle.getEntries()[sbShapeIconStyleValue]);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == mAncientBouncyOnoff) {
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == mAncientBoldOnoff) {
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == mAncientCusTop) {
            int sbSeetingStyleValue = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "ANCI_CUSTOM_TOPLEVEL", sbSeetingStyleValue, UserHandle.USER_CURRENT);
            mAncientCusTop.setSummary(mAncientCusTop.getEntries()[sbSeetingStyleValue]);
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == mAnciDatacon) {
            int sbDataconStyleValue = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "DATA_CON_STYLE", sbDataconStyleValue, UserHandle.USER_CURRENT);
            mAnciDatacon.setSummary(mAnciDatacon.getEntries()[sbDataconStyleValue]);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == mPkiri) {
            int kirik = (Integer) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    "JEMBT_LEBAT_KIRI", kirik);
            return true;
        } else if (preference == mPkanan) {
            int kuntul = (Integer) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    "JEMBT_LEBAT_KANAN", kuntul);
            return true;
        } else if (preference == rgbLiAccentPicker) {
            int colora = (Integer) objValue;
            String hexColora = String.format("%08X", (0xFFFFFFFF & colora));
            Settings.Secure.putStringForUser(getContentResolver(),
                        "accent_light",
                        hexColora, UserHandle.USER_CURRENT);
            try {
                 mOverlayService.reloadAssets("android", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == rgbDaAccentPicker) {
            int colorb = (Integer) objValue;
            String hexColorb = String.format("%08X", (0xFFFFFFFF & colorb));
            Settings.Secure.putStringForUser(getContentResolver(),
                        "accent_dark",
                        hexColorb, UserHandle.USER_CURRENT);
            try {
                 mOverlayService.reloadAssets("android", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
        } else if (preference == mMonetOnoff) {
            boolean value = (Boolean) objValue;
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.MONET_ENGINE, value ? 1 : 0);
            try {
                 mOverlayService.reloadAssets("android", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
	    int mMonetSwitch = Settings.Secure.getInt(getActivity().getContentResolver(),
                Settings.Secure.MONET_ENGINE, 0);
	    if (mMonetSwitch == 0) {
		rgbLiAccentPicker.setEnabled(true);
		rgbDaAccentPicker.setEnabled(true);
	    } else {
		rgbLiAccentPicker.setEnabled(false);
		rgbDaAccentPicker.setEnabled(false);
	    }
            return true;
        } else if (preference == mMonetPallete) {
            int paletteType = Integer.parseInt((String) objValue);
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.MONET_PALETTE, paletteType, UserHandle.USER_CURRENT);
	    int indexx = mMonetPallete.findIndexOfValue((String) objValue);
            mMonetPallete.setSummary(mMonetPallete.getEntries()[indexx]);
            return true;
	} else if (preference == mFonterStyle) {
            int anFonterStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "FONTER_STYLE", anFonterStyle, UserHandle.USER_CURRENT);
            mFonterStyle.setSummary(mFonterStyle.getEntries()[anFonterStyle]);
            try {
                 mOverlayService.reloadAssets("android", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;
	} else if (preference == mAnciHeadclockOnoff) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    "QQS_CLOCKFAKE_SWITCH", value ? 1 : 0);
            return true;
	} else if (preference == mAnciHeadSize) {
            return true;
	} else if (preference == mPanneltop) {
            return true;
	} else if (preference == mBi) {
            return true;
	} else if (preference == mTopad) {
            return true;
	} else if (preference == mAncUIa) {
            int anAncUIaStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "ANCIENT_UI_QSCLOCK_STYLE", anAncUIaStyle, UserHandle.USER_CURRENT);
            mAncUIa.setSummary(mAncUIa.getEntries()[anAncUIaStyle]);
            return true;
	} else if (preference == mAncUIb) {
            int anAncUIbStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "ANCIENT_UI_QQSCLOCK_GRAVITY", anAncUIbStyle, UserHandle.USER_CURRENT);
            mAncUIb.setSummary(mAncUIb.getEntries()[anAncUIbStyle]);
            return true;
	} else if (preference == mAncUIc) {
            int anAncUIcStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "ANCIENT_UI_QSCLOCK_GRAVITY", anAncUIcStyle, UserHandle.USER_CURRENT);
            mAncUIc.setSummary(mAncUIc.getEntries()[anAncUIcStyle]);
            return true;
        } else if (preference == mAncUId) {
            int anAncUIdStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "JAM_HEADER_SIZE", anAncUIdStyle, UserHandle.USER_CURRENT);
            mAncUId.setSummary(mAncUIc.getEntries()[anAncUIdStyle]);
            return true;
	} else if (preference == mJamPalsu) {
            int setWarna = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "QS_CLOCK_WARNA", setWarna, UserHandle.USER_CURRENT);
            mJamPalsu.setSummary(mJamPalsu.getEntries()[setWarna]);
            return true;
	} else if (preference == mAncientCollapsedOnoff) {
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;
		
        } else if (preference == mAncientHomeCollapsedOnoff) {
            try {
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
	    int homecollapse = Settings.System.getInt(getActivity().getContentResolver(),
		"homecollapseonoff", 0);  
	    if (homecollapse == 0) {
                mAncientBoldOnoff.setEnabled(false);
	        mAvatarViewVis.setEnabled(false);
            } else {
	       mAncientBoldOnoff.setEnabled(true);
	       mAvatarViewVis.setEnabled(true);
      	    }    
            return true;
	} else if (preference == mJamhedgrav) {
            int jamhedgrav = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "JAM_HEADER_GRAVITY", jamhedgrav, UserHandle.USER_CURRENT);
            mJamhedgrav.setSummary(mJamhedgrav.getEntries()[jamhedgrav]);
            return true;
	} else if (preference == mAncientuiOnoff) {
            int setAnciToStock = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "QS_TO_STOCK", setAnciToStock, UserHandle.USER_CURRENT);
            mAncientuiOnoff.setSummary(mAncientuiOnoff.getEntries()[setAnciToStock]);
	    int mAnciToStock = Settings.System.getInt(getActivity().getContentResolver(),
                "QS_TO_STOCK", 0);
	    if (mAnciToStock == 1) {
	       mAnciHeadclockOnoff.setEnabled(false);
	       mAnciHeadSize.setEnabled(false);
	       mJamhedgrav.setEnabled(false);
	       mPanneltop.setEnabled(true);
    	       mBi.setEnabled(true);
    	       mTopad.setEnabled(true);
		mAncUIa.setEnabled(true);
               mAncUIb.setEnabled(true);
               mAncUIc.setEnabled(true);
		mJamPalsu.setEnabled(false);   
		mAncUId.setEnabled(false);    
	    } else if (mAnciToStock == 2) {
	       mAnciHeadclockOnoff.setEnabled(false);
	       mAnciHeadSize.setEnabled(true);
	       mJamhedgrav.setEnabled(false);
	       mPanneltop.setEnabled(false);
    	       mBi.setEnabled(false);
    	       mTopad.setEnabled(false);
		mAncUIa.setEnabled(false);
               mAncUIb.setEnabled(false);
               mAncUIc.setEnabled(false);
		mJamPalsu.setEnabled(false); 
		mAncUId.setEnabled(false);  
            } else {
               mAnciHeadclockOnoff.setEnabled(true);
	       mAnciHeadSize.setEnabled(true);
	       mJamhedgrav.setEnabled(true);
	       mPanneltop.setEnabled(false);
    	       mBi.setEnabled(false);
    	       mTopad.setEnabled(false);
		mAncUIa.setEnabled(false);
               mAncUIb.setEnabled(false);
               mAncUIc.setEnabled(false);
		mJamPalsu.setEnabled(true); 
	        mAncUId.setEnabled(true);    
            }
            return true;	
        }
        return false;
    }

    private void setupThemeSwitchPref() {
        mThemeSwitch = (ListPreference) findPreference(PREF_THEME_SWITCH);
        mThemeSwitch.setOnPreferenceChangeListener(this);
        if (AncientUtils.isThemeEnabled("com.android.theme.ancientclear.system")) {
            mThemeSwitch.setValue("9");
        } else if (AncientUtils.isThemeEnabled("com.android.theme.materialocean.system")) {
            mThemeSwitch.setValue("8");
        } else if (AncientUtils.isThemeEnabled("com.android.theme.darkgrey.system")) {
            mThemeSwitch.setValue("7");
        } else if (AncientUtils.isThemeEnabled("com.android.theme.pitchblack.system")) {
            mThemeSwitch.setValue("6");
        } else if (AncientUtils.isThemeEnabled("com.android.theme.chocox.system")) {
            mThemeSwitch.setValue("5");
        } else if (AncientUtils.isThemeEnabled("com.android.theme.bakedgreen.system")) {
            mThemeSwitch.setValue("4");
        } else if (AncientUtils.isThemeEnabled("com.android.theme.solarizeddark.system")) {
            mThemeSwitch.setValue("3");
        } else if (mUiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            mThemeSwitch.setValue("2");
        } else {
            mThemeSwitch.setValue("1");
        }
    }

    private void handleBackgrounds(Boolean state, Context context, int mode, String[] overlays) {
        if (context != null) {
            Objects.requireNonNull(context.getSystemService(UiModeManager.class))
                    .setNightMode(mode);
        }
        for (int i = 0; i < overlays.length; i++) {
            String background = overlays[i];
            try {
                mOverlayService.setEnabled(background, state, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.ANCIENT_SETTINGS;
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
                    sir.xmlResId = R.xml.ancient_settings_interface;
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
