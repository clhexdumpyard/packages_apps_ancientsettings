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

import static android.os.UserHandle.USER_CURRENT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto; 
import com.android.settings.SettingsPreferenceFragment;

import com.ancient.settings.preferences.SystemSettingSwitchPreference;
import com.android.internal.util.ancient.AncientUtils;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.ancient.settings.preferences.SystemSettingListPreference;

import com.android.settings.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Addon extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    public static final String TAG = "Addon";

    private static final String STATUSBAR_DUAL_STYLE = "STATUSBAR_DUAL_STYLE"; 
    private static final String STATUSBAR_ICON_STYLE = "STATUSBAR_ICON_STYLE"; 
    private static final String STATUSBAR_HEIGHT_STYLE = "STATUSBAR_HEIGHT_STYLE";
    private static final String NABIL_BACKGROUNDCLOCKSB_COLOR = "nabil_backgroundclocksb_color";
    private static final String NABIL_BACKGROUNDCLOCKSB_GRADIENT1 = "nabil_backgroundclocksb_gradient1";
    private static final String NABIL_BACKGROUNDCLOCKSB_GRADIENT2 = "nabil_backgroundclocksb_gradient2";
    private static final String NABIL_BACKGROUNDCLOCKSB_STROKECOLOR = "nabil_backgroundclocksb_strokecolor";
    private static final String STATUSBAR_DATA_STYLE = "STATUSBAR_DATA_STYLE";     
    private static final String BRIGHTNESS_STYLES = "BRIGHTNESS_STYLES";
    private static final String BRIGHTNESS_STYLES = "VOLUMEBAR_STYLES";

    private static final String MEDIUM_OVERLAY_SBHEIGHT = "com.custom.overlay.systemui.hight.medium";
    private static final String BIG_OVERLAY_SBHEIGHT = "com.custom.overlay.systemui.hight.big"; 
    private static final String VERYBIG_OVERLAY_SBHEIGHT = "com.custom.overlay.systemui.hight.verybig";        

    private static final String SIGNAL_OVERLAY_STYLE1 = "com.custom.overlay.systemui.icon1";
    private static final String SIGNAL_OVERLAY_STYLE2 = "com.custom.overlay.systemui.icon2"; 
    private static final String SIGNAL_OVERLAY_STYLE3 = "com.custom.overlay.systemui.icon3";        
    private static final String SIGNAL_OVERLAY_STYLE4 = "com.custom.overlay.systemui.icon4"; 
    private static final String SIGNAL_OVERLAY_STYLE5 = "com.custom.overlay.systemui.icon5"; 
    private static final String SIGNAL_OVERLAY_STYLE6 = "com.custom.overlay.systemui.icon6";
    private static final String SIGNAL_OVERLAY_STYLE7 = "com.custom.overlay.systemui.icon7"; 
    private static final String SIGNAL_OVERLAY_STYLE8 = "com.custom.overlay.systemui.icon8";        
    private static final String SIGNAL_OVERLAY_STYLE9 = "com.custom.overlay.systemui.icon9"; 
    private static final String SIGNAL_OVERLAY_STYLE10 = "com.custom.overlay.systemui.icon10"; 
    private static final String SIGNAL_OVERLAY_STYLE11 = "com.custom.overlay.systemui.icon11";
    private static final String SIGNAL_OVERLAY_STYLE12 = "com.custom.overlay.systemui.icon12"; 
    private static final String SIGNAL_OVERLAY_STYLE13 = "com.custom.overlay.systemui.icon13";        
    private static final String SIGNAL_OVERLAY_STYLE14 = "com.custom.overlay.systemui.icon14"; 
    private static final String SIGNAL_OVERLAY_STYLE15 = "com.custom.overlay.systemui.icon15"; 
    private static final String SIGNAL_OVERLAY_STYLE16 = "com.custom.overlay.systemui.icon16";      
    
    private static final String BRIGHTNESS_OVERLAY_STYLE1 = "com.custom.overlay.systemui.brightness1";
    private static final String BRIGHTNESS_OVERLAY_STYLE2 = "com.custom.overlay.systemui.brightness2"; 
    private static final String BRIGHTNESS_OVERLAY_STYLE3 = "com.custom.overlay.systemui.brightness3";        
    private static final String BRIGHTNESS_OVERLAY_STYLE4 = "com.custom.overlay.systemui.brightness4"; 
    private static final String BRIGHTNESS_OVERLAY_STYLE5 = "com.custom.overlay.systemui.brightness5";
     
    private static final String VOLUMEBAR_OVERLAY_STYLE1 = "com.custom.overlay.systemui.volume1";
    private static final String VOLUMEBAR_OVERLAY_STYLE2 = "com.custom.overlay.systemui.volume2"; 
    private static final String VOLUMEBAR_OVERLAY_STYLE3 = "com.custom.overlay.systemui.volume3";        
    private static final String VOLUMEBAR_OVERLAY_STYLE4 = "com.custom.overlay.systemui.volume4"; 
    private static final String VOLUMEBAR_OVERLAY_STYLE5 = "com.custom.overlay.systemui.volume5";

    private static final String DATA_OVERLAY_STYLE1 = "com.custom.overlay.systemui.data1";
    private static final String DATA_OVERLAY_STYLE2 = "com.custom.overlay.systemui.data2"; 
    private static final String DATA_OVERLAY_STYLE3 = "com.custom.overlay.systemui.data3";        
    private static final String DATA_OVERLAY_STYLE4 = "com.custom.overlay.systemui.data4"; 
    private static final String DATA_OVERLAY_STYLE5 = "com.custom.overlay.systemui.data5";
        
    private static final String TIGALIMA_OVERLAY_SBHEIGHT = "com.custom.overlay.systemui.hight.tigalima";
    private static final String LIMAPULUH_OVERLAY_SBHEIGHT = "com.custom.overlay.systemui.hight.limapuluh"; 
    private static final String ENAMPULUH_OVERLAY_SBHEIGHT = "com.custom.overlay.systemui.hight.enampuluh";  
    private static final String DELAPANPULUH_OVERLAY_SBHEIGHT = "com.custom.overlay.systemui.hight.delapanpuluh";     

    private SystemSettingListPreference idcDualBarStyle;
    private SystemSettingListPreference idcSbIconStyle;   
    private SystemSettingListPreference idcSbHeightStyle;
    private ColorPickerPreference mBacka;
    private ColorPickerPreference mBackb;
    private ColorPickerPreference mBackc;
    private ColorPickerPreference mBackd;
    private SystemSettingListPreference idcSbDataStyle;       
    private SystemSettingListPreference idcSbBrightStyle;
    private SystemSettingListPreference idcSbVolumeStyle;           
  
    private Context mContext;
    private IOverlayManager mOverlayService;    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ancient_settings_addon);

        mContext = getActivity();

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen screen = getPreferenceScreen();

        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
        
        idcDualBarStyle = (SystemSettingListPreference) findPreference("STATUSBAR_DUAL_STYLE");
        int sbDualBarStyle = Settings.System.getIntForUser(getContentResolver(),
                "STATUSBAR_DUAL_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndex = idcDualBarStyle.findIndexOfValue(String.valueOf(sbDualBarStyle));
        idcDualBarStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        idcDualBarStyle.setSummary(idcDualBarStyle.getEntry());
        idcDualBarStyle.setOnPreferenceChangeListener(this);

        idcSbIconStyle = (SystemSettingListPreference) findPreference("STATUSBAR_ICON_STYLE");
        int sbIconStyle = Settings.System.getIntForUser(getContentResolver(),
                "STATUSBAR_ICON_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndexicon = idcSbIconStyle.findIndexOfValue(String.valueOf(sbIconStyle));
        idcSbIconStyle.setValueIndex(valueIndexicon >= 0 ? valueIndexicon : 0);
        idcSbIconStyle.setSummary(idcSbIconStyle.getEntry());
        idcSbIconStyle.setOnPreferenceChangeListener(this);
            
        idcSbDataStyle = (SystemSettingListPreference) findPreference("STATUSBAR_DATA_STYLE");
        int sbDataStyle = Settings.System.getIntForUser(getContentResolver(),
                "STATUSBAR_DATA_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndexdata = idcSbDataStyle.findIndexOfValue(String.valueOf(sbDataStyle));
        idcSbDataStyle.setValueIndex(valueIndexdata >= 0 ? valueIndexdata : 0);
        idcSbDataStyle.setSummary(idcSbDataStyle.getEntry());
        idcSbDataStyle.setOnPreferenceChangeListener(this);
        
        idcSbBrightStyle = (SystemSettingListPreference) findPreference("BRIGHTNESS_STYLES");
        int sbBrightStyle = Settings.System.getIntForUser(getContentResolver(),
                "BRIGHTNESS_STYLES", 0, UserHandle.USER_CURRENT);
        int valueIndexbright = idcSbBrightStyle.findIndexOfValue(String.valueOf(sbBrightStyle));
        idcSbBrightStyle.setValueIndex(valueIndexbright >= 0 ? valueIndexbright : 0);
        idcSbBrightStyle.setSummary(idcSbBrightStyle.getEntry());
        idcSbBrightStyle.setOnPreferenceChangeListener(this);    
            
        idcSbVolumeStyle = (SystemSettingListPreference) findPreference("VOLUMEBAR_STYLES");
        int sbVolumeStyle = Settings.System.getIntForUser(getContentResolver(),
                "VOLUMEBAR_STYLES", 0, UserHandle.USER_CURRENT);
        int valueIndexvol = idcSbVolumeStyle.findIndexOfValue(String.valueOf(sbVolumeStyle));
        idcSbVolumeStyle.setValueIndex(valueIndexvol >= 0 ? valueIndexvol : 0);
        idcSbVolumeStyle.setSummary(idcSbVolumeStyle.getEntry());
        idcSbVolumeStyle.setOnPreferenceChangeListener(this);   
                
    
        idcSbHeightStyle = (SystemSettingListPreference) findPreference("STATUSBAR_HEIGHT_STYLE");
        int sbHeightStyle = Settings.System.getIntForUser(getContentResolver(),
                "STATUSBAR_HEIGHT_STYLE", 0, UserHandle.USER_CURRENT);
        int valueIndexheight = idcSbHeightStyle.findIndexOfValue(String.valueOf(sbHeightStyle));
        idcSbHeightStyle.setValueIndex(valueIndexheight >= 0 ? valueIndexheight : 0);
        idcSbHeightStyle.setSummary(idcSbHeightStyle.getEntry());
        idcSbHeightStyle.setOnPreferenceChangeListener(this);
            
         mBacka = (ColorPickerPreference) findPreference(NABIL_BACKGROUNDCLOCKSB_COLOR);
        int mbacaColor = Settings.System.getInt(getContentResolver(),
                "nabil_backgroundclocksb_color", 0x00000000);
        mBacka.setNewPreviewColor(mbacaColor);
        mBacka.setAlphaSliderEnabled(true);
        String mbacaColorHex = String.format("#%08x", (0x00000000 & mbacaColor));
        if (mbacaColorHex.equals("#00000000")) {
            mBacka.setSummary(R.string.color_default);
        } else {
            mBacka.setSummary(mbacaColorHex);
        }
        mBacka.setOnPreferenceChangeListener(this);

        mBackb = (ColorPickerPreference) findPreference(NABIL_BACKGROUNDCLOCKSB_GRADIENT1);
        int mbacabColor = Settings.System.getInt(getContentResolver(),
                "nabil_backgroundclocksb_gradient1", 0x00000000);
        mBackb.setNewPreviewColor(mbacabColor);
        mBackb.setAlphaSliderEnabled(true);
        String mbacabColorHex = String.format("#%08x", (0x00000000 & mbacabColor));
        if (mbacabColorHex.equals("#00000000")) {
            mBackb.setSummary(R.string.color_default);
        } else {
            mBackb.setSummary(mbacabColorHex);
        }
        mBackb.setOnPreferenceChangeListener(this);

        mBackc = (ColorPickerPreference) findPreference(NABIL_BACKGROUNDCLOCKSB_GRADIENT2);
        int mbacacColor = Settings.System.getInt(getContentResolver(),
                "nabil_backgroundclocksb_gradient2", 0x00000000);
        mBackc.setNewPreviewColor(mbacacColor);
        mBackc.setAlphaSliderEnabled(true);
        String mbacacColorHex = String.format("#%08x", (0x00000000 & mbacacColor));
        if (mbacacColorHex.equals("#00000000")) {
            mBackc.setSummary(R.string.color_default);
        } else {
            mBackc.setSummary(mbacacColorHex);
        }
        mBackc.setOnPreferenceChangeListener(this);

        mBackd = (ColorPickerPreference) findPreference(NABIL_BACKGROUNDCLOCKSB_STROKECOLOR);
        int mbacadColor = Settings.System.getInt(getContentResolver(),
                "nabil_backgroundclocksb_strokecolor", 0x00000000);
        mBackd.setNewPreviewColor(mbacadColor);
        mBackd.setAlphaSliderEnabled(true);
        String mbacadColorHex = String.format("#%08x", (0x00000000 & mbacadColor));
        if (mbacadColorHex.equals("#00000000")) {
            mBackd.setSummary(R.string.color_default);
        } else {
            mBackd.setSummary(mbacadColorHex);
        }
        mBackd.setOnPreferenceChangeListener(this);   
      
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == idcDualBarStyle) {
            int sbDualBarStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "STATUSBAR_DUAL_STYLE", sbDualBarStyle, UserHandle.USER_CURRENT);
            idcDualBarStyle.setSummary(idcDualBarStyle.getEntries()[sbDualBarStyle]);
                if (sbDualBarStyle == 0 || sbDualBarStyle == 1 || sbDualBarStyle == 2) {
                   try {
                      mOverlayService.setEnabled(MEDIUM_OVERLAY_SBHEIGHT, false, USER_CURRENT);
                      mOverlayService.setEnabled(BIG_OVERLAY_SBHEIGHT, false, USER_CURRENT);
                      mOverlayService.setEnabled(VERYBIG_OVERLAY_SBHEIGHT, false, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbDualBarStyle == 3 || sbDualBarStyle == 4 || sbDualBarStyle == 5
                            || sbDualBarStyle == 6 || sbDualBarStyle == 7 || sbDualBarStyle == 8
                            || sbDualBarStyle == 9 || sbDualBarStyle == 10 || sbDualBarStyle == 11) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(MEDIUM_OVERLAY_SBHEIGHT, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbDualBarStyle == 12 || sbDualBarStyle == 13 || sbDualBarStyle == 14) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(BIG_OVERLAY_SBHEIGHT, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbDualBarStyle == 15) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(VERYBIG_OVERLAY_SBHEIGHT, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                }   
            return true;
        } else if (preference == idcSbIconStyle) {
            int sbIconStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "STATUSBAR_ICON_STYLE", sbIconStyle, UserHandle.USER_CURRENT);
            idcSbIconStyle.setSummary(idcSbIconStyle.getEntries()[sbIconStyle]);
                if (sbIconStyle == 0) {
                   try {
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE1, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE2, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE3, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE4, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE5, false, USER_CURRENT); 
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE6, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE7, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE8, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE9, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE10, false, USER_CURRENT); 
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE11, false, USER_CURRENT);  
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE12, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE13, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE14, false, USER_CURRENT);
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE15, false, USER_CURRENT); 
                      mOverlayService.setEnabled(SIGNAL_OVERLAY_STYLE16, false, USER_CURRENT);    
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbIconStyle == 1) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE1, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbIconStyle == 2) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE2, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 3) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE3, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 4) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE4, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 5) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE5, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 6) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE6, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbIconStyle == 7) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE7, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 8) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE8, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 9) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE9, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 10) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE10, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 11) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE11, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 12) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE12, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 13) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE13, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 14) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE14, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 15) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE15, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbIconStyle == 16) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(SIGNAL_OVERLAY_STYLE16, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                }               
            return true;
        } else if (preference == idcSbDataStyle) {
            int sbDataStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "STATUSBAR_DATA_STYLE", sbDataStyle, UserHandle.USER_CURRENT);
            idcSbDataStyle.setSummary(idcSbDataStyle.getEntries()[sbDataStyle]);
                if (sbDataStyle == 0) {
                   try {
                      mOverlayService.setEnabled(DATA_OVERLAY_STYLE1, false, USER_CURRENT);
                      mOverlayService.setEnabled(DATA_OVERLAY_STYLE2, false, USER_CURRENT);
                      mOverlayService.setEnabled(DATA_OVERLAY_STYLE3, false, USER_CURRENT);
                      mOverlayService.setEnabled(DATA_OVERLAY_STYLE4, false, USER_CURRENT);
                      mOverlayService.setEnabled(DATA_OVERLAY_STYLE5, false, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbDataStyle == 1) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(DATA_OVERLAY_STYLE1, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbDataStyle == 2) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(DATA_OVERLAY_STYLE2, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbDataStyle == 3) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(DATA_OVERLAY_STYLE3, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbDataStyle == 4) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(DATA_OVERLAY_STYLE4, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbDataStyle == 5) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(DATA_OVERLAY_STYLE5, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                }    
            return true;
        } else if (preference == idcSbVolumeStyle) {
            int sbSbVolumeStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "VOLUMEBAR_STYLES", sbSbVolumeStyle, UserHandle.USER_CURRENT);
            idcSbVolumeStyle.setSummary(idcSbVolumeStyle.getEntries()[sbSbVolumeStyle]);
                if (sbSbVolumeStyle == 0) {
                   try {
                      mOverlayService.setEnabled(VOLUMEBAR_OVERLAY_STYLE1, false, USER_CURRENT);
                      mOverlayService.setEnabled(VOLUMEBAR_OVERLAY_STYLE2, false, USER_CURRENT);
                      mOverlayService.setEnabled(VOLUMEBAR_OVERLAY_STYLE3, false, USER_CURRENT);
                      mOverlayService.setEnabled(VOLUMEBAR_OVERLAY_STYLE4, false, USER_CURRENT);
                      mOverlayService.setEnabled(VOLUMEBAR_OVERLAY_STYLE5, false, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbSbVolumeStyle == 1) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(VOLUMEBAR_OVERLAY_STYLE1, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbSbVolumeStyle == 2) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(VOLUMEBAR_OVERLAY_STYLE2, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbSbVolumeStyle == 3) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(VOLUMEBAR_OVERLAY_STYLE3, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbSbVolumeStyle == 4) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(VOLUMEBAR_OVERLAY_STYLE4, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbSbVolumeStyle == 5) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(VOLUMEBAR_OVERLAY_STYLE5, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                }    
            return true;
        } else if (preference == idcSbHeightStyle) {
            int sbHeightStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "STATUSBAR_HEIGHT_STYLE", sbHeightStyle, UserHandle.USER_CURRENT);
            idcSbHeightStyle.setSummary(idcSbHeightStyle.getEntries()[sbHeightStyle]);
                if (sbHeightStyle == 0) {
                   try {
                      mOverlayService.setEnabled(TIGALIMA_OVERLAY_SBHEIGHT, false, USER_CURRENT);
                      mOverlayService.setEnabled(LIMAPULUH_OVERLAY_SBHEIGHT, false, USER_CURRENT);
                      mOverlayService.setEnabled(ENAMPULUH_OVERLAY_SBHEIGHT, false, USER_CURRENT);
                      mOverlayService.setEnabled(DELAPANPULUH_OVERLAY_SBHEIGHT, false, USER_CURRENT);    
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbHeightStyle == 1) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(TIGALIMA_OVERLAY_SBHEIGHT, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbHeightStyle == 2) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(LIMAPULUH_OVERLAY_SBHEIGHT, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbHeightStyle == 3) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(ENAMPULUH_OVERLAY_SBHEIGHT, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbHeightStyle == 4) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(DELAPANPULUH_OVERLAY_SBHEIGHT, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                }  
            return true;
        } else if (preference == mBacka) {
            String hexa = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hexa.equals("#00000000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexa);
            }
            int intHexa = ColorPickerPreference.convertToColorInt(hexa);
            Settings.System.putInt(getContentResolver(),
                    "nabil_backgroundclocksb_color", intHexa);
            return true;  
        } else if (preference == mBackb) {
            String hexb = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hexb.equals("#00000000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexb);
            }
            int intHexb = ColorPickerPreference.convertToColorInt(hexb);
            Settings.System.putInt(getContentResolver(),
                    "nabil_backgroundclocksb_gradient1", intHexb);
            return true;   
        } else if (preference == mBackc) {
            String hexc = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hexc.equals("#00000000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexc);
            }
            int intHexc = ColorPickerPreference.convertToColorInt(hexc);
            Settings.System.putInt(getContentResolver(),
                    "nabil_backgroundclocksb_gradient2", intHexc);
            return true;
        } else if (preference == mBackd) {
            String hexd = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hexd.equals("#00000000")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexd);
            }
            int intHexd = ColorPickerPreference.convertToColorInt(hexd);
            Settings.System.putInt(getContentResolver(),
                    "nabil_backgroundclocksb_strokecolor", intHexd);
            return true;
        } else if (preference == idcSbBrightStyle) {
            int sbBrightStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(),
                    "BRIGHTNESS_STYLES", sbBrightStyle, UserHandle.USER_CURRENT);
            idcSbBrightStyle.setSummary(idcSbBrightStyle.getEntries()[sbBrightStyle]);
                if (sbBrightStyle == 0) {
                   try {
                      mOverlayService.setEnabled(BRIGHTNESS_OVERLAY_STYLE1, false, USER_CURRENT);
                      mOverlayService.setEnabled(BRIGHTNESS_OVERLAY_STYLE2, false, USER_CURRENT);
                      mOverlayService.setEnabled(BRIGHTNESS_OVERLAY_STYLE3, false, USER_CURRENT);
                      mOverlayService.setEnabled(BRIGHTNESS_OVERLAY_STYLE4, false, USER_CURRENT);
                      mOverlayService.setEnabled(BRIGHTNESS_OVERLAY_STYLE5, false, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbBrightStyle == 1) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(BRIGHTNESS_OVERLAY_STYLE1, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
               } else if (sbBrightStyle == 2) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(BRIGHTNESS_OVERLAY_STYLE2, USER_CURRENT);   
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbBrightStyle == 3) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(BRIGHTNESS_OVERLAY_STYLE3, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbBrightStyle == 4) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(BRIGHTNESS_OVERLAY_STYLE4, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                } else if (sbBrightStyle == 5) {
                   try {
                      mOverlayService.setEnabledExclusiveInCategory(BRIGHTNESS_OVERLAY_STYLE5, USER_CURRENT);     
                   } catch (RemoteException re) {
                      throw re.rethrowFromSystemServer();
                   }
                }    
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }
}
