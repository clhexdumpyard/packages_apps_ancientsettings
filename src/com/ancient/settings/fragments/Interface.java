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
import android.content.Intent;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.res.Resources;
import android.graphics.Color;
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
import com.ancient.settings.display.QsClockStylePreferenceController;
import com.ancient.settings.display.DgCornerStylePreferenceController;
import com.ancient.settings.display.SbHeightStylePreferenceController;
import com.ancient.settings.display.SbNavStylePreferenceController;
import com.ancient.settings.display.AnSwitchStylePreferenceController;
import com.ancient.settings.display.AnAclockStylePreferenceController;
import com.ancient.settings.display.SbBrightnStylePreferenceController;
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
    private static final String PREF_ACCENTER_STYLE = "ACCENTER_STYLE";  
    private static final String PREF_FONTER_STYLE = "FONTER_STYLE";     
  
    private IOverlayManager mOverlayService;
    private UiModeManager mUiModeManager;

    private ListPreference mThemeSwitch;
    private SystemSettingListPreference mAvatarViewVis;
    private SystemSettingListPreference mAncientCollapsedBaseStyle;
    private SystemSettingSwitchPreference mAncientCollapsedOnoff;
    private ListPreference mAncientHomepageBackground;
    private SystemSettingListPreference mAncientCollapseHeader;
    private SystemSettingSwitchPreference mAncientHomeCollapsedOnoff;
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
    private SecureSettingListPreference mMonetPallete;
    private SystemSettingListPreference mAccenterStyle;
    private SystemSettingListPreference mFonterStyle;    
    
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
        mAncientCollapsedOnoff = (SystemSettingSwitchPreference) findPreference("collapseonoff");
        mAncientCollapsedOnoff.setOnPreferenceChangeListener(this);
        mAncientHomepageBackground = (ListPreference) findPreference("ancient_homepage_background");
        mAncientHomepageBackground.setOnPreferenceChangeListener(this);
        mAncientCollapseHeader = (SystemSettingListPreference) findPreference("ancient_collapse_header");
        mAncientCollapseHeader.setOnPreferenceChangeListener(this);
        mAncientHomeCollapsedOnoff = (SystemSettingSwitchPreference) findPreference("homecollapseonoff");
        mAncientHomeCollapsedOnoff.setOnPreferenceChangeListener(this);
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
        if (colorVala.equals("#ff1a73e8")) {
            mAccenterStyle.setEnabled(true);
        } else {
            mAccenterStyle.setEnabled(false);
        }    
        rgbLiAccentPicker.setOnPreferenceChangeListener(this);   
            
        rgbDaAccentPicker = (ColorPickerPreference) findPreference(PREF_RGB_DARK_ACCENT_PICKER);
        String colorValb = Settings.Secure.getStringForUser(getContentResolver(),
                "accent_dark", UserHandle.USER_CURRENT);
        int colorb = (colorValb == null)
                ? Color.WHITE
                : Color.parseColor("#" + colorValb);
        rgbDaAccentPicker.setNewPreviewColor(colorb);
        if (colorValb.equals("#ff1a73e8")) {
            mAccenterStyle.setEnabled(true);
        } else {
            mAccenterStyle.setEnabled(false);
        }        
        rgbDaAccentPicker.setOnPreferenceChangeListener(this); 
            
        mMonetOnoff = (SecureSettingSwitchPreference) findPreference(PREF_MONET_ENGINE);
        mMonetOnoff.setChecked((Settings.Secure.getInt(getActivity().getContentResolver(),
                Settings.Secure.MONET_ENGINE, 0) == 1));
        int mMonetSwitch = Settings.Secure.getInt(getActivity().getContentResolver(),
                Settings.Secure.MONET_ENGINE, 0);    
        if (mMonetSwitch == 1) {
            rgbLiAccentPicker.setEnabled(false);
            rgbDaAccentPicker.setEnabled(false);
	    mAccenterStyle.setEnabled(false);
        } else {
            rgbLiAccentPicker.setEnabled(true);
            rgbDaAccentPicker.setEnabled(true);
	    mAccenterStyle.setEnabled(true);
        }    
        mMonetOnoff.setOnPreferenceChangeListener(this); 
            
        mMonetPallete = (SecureSettingListPreference) findPreference(PREF_MONET_PALETTE);
        int paletteType = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.MONET_PALETTE, 0, UserHandle.USER_CURRENT);
        int valueIndexMon = mMonetPallete.findIndexOfValue(String.valueOf(paletteType));
        mMonetPallete.setValueIndex(valueIndexMon >= 0 ? valueIndexMon : 0);
        mMonetPallete.setSummary(mMonetPallete.getEntry());          
        mMonetPallete.setOnPreferenceChangeListener(this); 
            
        mAccenterStyle = (SystemSettingListPreference) findPreference(PREF_ACCENTER_STYLE);
        int anAccenterStyle = Settings.System.getIntForUser(getContentResolver(),
                "ACCENTER_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndexAccent = mAccenterStyle.findIndexOfValue(String.valueOf(anAccenterStyle));
        mAccenterStyle.setValueIndex(valueIndexAccent >= 0 ? valueIndexAccent : 0);
        mAccenterStyle.setSummary(mAccenterStyle.getEntry());
        if (valueIndexAccent == 0) {
	    rgbLiAccentPicker.setEnabled(true);
            rgbDaAccentPicker.setEnabled(true);
        } else {
            rgbLiAccentPicker.setEnabled(false);
            rgbDaAccentPicker.setEnabled(false);
        }   
        mAccenterStyle.setOnPreferenceChangeListener(this);

	mFonterStyle = (SystemSettingListPreference) findPreference(PREF_FONTER_STYLE);
        int anFonterStyle = Settings.System.getIntForUser(getContentResolver(),
                "FONTER_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndexFont = mFonterStyle.findIndexOfValue(String.valueOf(anFonterStyle));
        mFonterStyle.setValueIndex(valueIndexFont >= 0 ? valueIndexFont : 0);
        mFonterStyle.setSummary(mFonterStyle.getEntry());  
        mFonterStyle.setOnPreferenceChangeListener(this);     

    }
      
    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.accent_color"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.font"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.adaptive_icon_shape"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.icon_pack.android"));
        controllers.add(new QsTileStylePreferenceController(context));
        controllers.add(new QsClockStylePreferenceController(context));
        controllers.add(new DgCornerStylePreferenceController(context));
        controllers.add(new SbHeightStylePreferenceController(context));
        controllers.add(new SbNavStylePreferenceController(context));
        controllers.add(new AnSwitchStylePreferenceController(context));
        controllers.add(new AnAclockStylePreferenceController(context));
        controllers.add(new SbBrightnStylePreferenceController(context));
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
        } else if (preference == mAncientCollapsedOnoff) {
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
        } else if (preference == mAncientHomeCollapsedOnoff) {
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
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
            return true;   
        } else if (preference == mPkanan) {
            int kuntul = (Integer) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    "JEMBT_LEBAT_KANAN", kuntul);
            try {
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
            } catch (RemoteException ignored) {
            }
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
             if (hexColora.equals("#ff1a73e8")) {
            	mAccenterStyle.setEnabled(true);
             } else {
           	mAccenterStyle.setEnabled(false);
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
	     if (hexColorb.equals("#ff1a73e8")) {
            	mAccenterStyle.setEnabled(true);
             } else {
           	mAccenterStyle.setEnabled(false);
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
            if (mMonetSwitch == 1) {
                rgbLiAccentPicker.setEnabled(false);
                rgbDaAccentPicker.setEnabled(false);
		mAccenterStyle.setEnabled(false);    
            } else {
                rgbLiAccentPicker.setEnabled(true);
                rgbDaAccentPicker.setEnabled(true);
		mAccenterStyle.setEnabled(false);    
            }       
            return true; 
        } else if (preference == mMonetPallete) {
            int paletteType = Integer.valueOf((String) objValue);
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.MONET_PALETTE, paletteType, UserHandle.USER_CURRENT);
            mMonetPallete.setSummary(mMonetPallete.getEntries()[paletteType]);          
            try {
                 mOverlayService.reloadAssets("android", UserHandle.USER_CURRENT);   
                 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
            }   
             return true; 
        } else if (preference == mAccenterStyle) {
            int anAccenterStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "ACCENTER_STYLE", anAccenterStyle, UserHandle.USER_CURRENT);
            mAccenterStyle.setSummary(mAccenterStyle.getEntries()[anAccenterStyle]);
            try {
                 mOverlayService.reloadAssets("android", UserHandle.USER_CURRENT);
		 mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
                 mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);   
            } catch (RemoteException ignored) {
            }
	    if (anAccenterStyle == 0) {
		rgbLiAccentPicker.setEnabled(true);
		rgbDaAccentPicker.setEnabled(true);
	    } else {
		rgbLiAccentPicker.setEnabled(false);
		rgbDaAccentPicker.setEnabled(false);
	    }  
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
