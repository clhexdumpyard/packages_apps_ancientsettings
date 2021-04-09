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
import com.ancient.settings.display.AnSettingsStylePreferenceController;
import com.ancient.settings.display.SbBrightnStylePreferenceController;
import com.ancient.settings.display.SbQsbgStylePreferenceController;
import com.ancient.settings.display.SbPaddingStylePreferenceController;
//import com.ancient.settings.display.SbMarginStylePreferenceController;
import com.ancient.settings.preferences.SystemSettingListPreference;
import com.ancient.settings.preferences.SystemSettingSwitchPreference;

import com.android.internal.util.ancient.ThemesUtils;
import com.android.internal.util.ancient.AncientUtils;

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

    private IOverlayManager mOverlayService;
    private UiModeManager mUiModeManager;

    private SystemSettingListPreference mAvatarViewVis; 
    private SystemSettingListPreference mAncientCollapsedBaseStyle;
    private SystemSettingSwitchPreference mAncientCollapsedOnoff;  
    private ListPreference mAncientHomepageBackground; 
    private SystemSettingListPreference mAncientCollapseHeader;   
    private SystemSettingSwitchPreference mAncientHomeCollapsedOnoff;   
    private SystemSettingListPreference mAncientCollapseToolBg;  
    private SystemSettingListPreference mSbMarginStyle;  
    private ListPreference mThemeSwitch;

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
            
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new AccentColorPreferenceController(context));
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
        controllers.add(new SbPaddingStylePreferenceController(context));
        //controllers.add(new SbMarginStylePreferenceController(context));    
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
                 mOverlayService.reloadAndroidAssets(UserHandle.USER_CURRENT);
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
