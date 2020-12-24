/*
 * Copyright (C) 2020 The Pure Nexus Project
 * used for Project ancient
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

package com.ancient.settings;

import com.android.internal.logging.nano.MetricsProto;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Surface;
import android.preference.Preference;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.Toast;
import android.view.View;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.ComponentName;
import androidx.cardview.widget.CardView;;

import com.android.settings.R;
import com.ancient.settings.fragments.Interface;
import com.ancient.settings.fragments.StatusBar;
import com.ancient.settings.fragments.QuickSettings;
import com.ancient.settings.fragments.LockScreen;
import com.ancient.settings.fragments.PowerMenuSettings;
import com.ancient.settings.fragments.Gestures;
import com.ancient.settings.fragments.Notifications;
import com.ancient.settings.fragments.Buttons;
import com.ancient.settings.fragments.Navigation;
import com.ancient.settings.fragments.Animations;
import com.ancient.settings.fragments.Battery;
import com.ancient.settings.fragments.Misc;
import com.ancient.settings.fragments.MegalithAbout;

import com.android.settings.SettingsPreferenceFragment;

public class AncientSettings extends SettingsPreferenceFragment implements View.OnClickListener {

    CardView mInterfaceCard;
    CardView mStatusBarCard;
    CardView mQuickSettingsCard;
    CardView mLockScreenCard;
    CardView mPowerMenuSettingsCard;
    CardView mGesturesCard;
    CardView mNotificationsCard;
    CardView mButtonCard;
    CardView mNavigationCard;
    CardView mAnimationsCard;
    CardView mBatteryCard;
    CardView mMiscCard;
    CardView mMegalithAboutCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.ancient_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            getActivity();

        mInterfaceCard = (CardView) view.findViewById(R.id.interface_card);
        mInterfaceCard.setOnClickListener(this);

        mStatusBarCard = (CardView) view.findViewById(R.id.statusbar_card);
        mStatusBarCard.setOnClickListener(this);

        mQuickSettingsCard = (CardView) view.findViewById(R.id.quicksettings_card);
        mQuickSettingsCard.setOnClickListener(this);

        mLockScreenCard = (CardView) view.findViewById(R.id.lockscreen_card);
        mLockScreenCard.setOnClickListener(this);

        mPowerMenuSettingsCard = (CardView) view.findViewById(R.id.powermenu_card);
        mPowerMenuSettingsCard.setOnClickListener(this);

        mGesturesCard = (CardView) view.findViewById(R.id.gesture_card);
        mGesturesCard.setOnClickListener(this);

        mNotificationsCard = (CardView) view.findViewById(R.id.notification_card);
        mNotificationsCard.setOnClickListener(this);

        mButtonCard = (CardView) view.findViewById(R.id.button_card);
        mButtonCard.setOnClickListener(this);

        mNavigationCard = (CardView) view.findViewById(R.id.navigation_card);
        mNavigationCard.setOnClickListener(this);

        mAnimationsCard = (CardView) view.findViewById(R.id.animations_card);
        mAnimationsCard.setOnClickListener(this);

        mBatteryCard = (CardView) view.findViewById(R.id.battery_card);
        mBatteryCard.setOnClickListener(this);

        mMiscCard = (CardView) view.findViewById(R.id.misc_card);
        mMiscCard.setOnClickListener(this);

        mMegalithAboutCard = (CardView) view.findViewById(R.id.megalith_about_card);
        mMegalithAboutCard.setOnClickListener(this);

        }

    @Override
    public void onClick(View view) {
        int id = view.getId();
            if (id == R.id.interface_card)
              {
                Interface interfacefragment = new Interface();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.replace(this.getId(), interfacefragment);
                transaction.addToBackStack(null);
                transaction.commit();
               }
            if (id == R.id.statusbar_card)
              {
                StatusBar statusbarfragment = new StatusBar();
                FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
                transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction1.replace(this.getId(), statusbarfragment);
                transaction1.addToBackStack(null);
                transaction1.commit();
              }
            if (id == R.id.lockscreen_card)
              {
               LockScreen lockscreenfragment = new LockScreen();
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                transaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction2.replace(this.getId(), lockscreenfragment);
                transaction2.addToBackStack(null);
                transaction2.commit();
               }
            if (id == R.id.powermenu_card)
              {
                PowerMenuSettings powermenusettingsfragment = new PowerMenuSettings();
                FragmentTransaction transaction3 = getFragmentManager().beginTransaction();
                transaction3.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction3.replace(this.getId(), powermenusettingsfragment);
                transaction3.addToBackStack(null);
                transaction3.commit();
               }
            if (id == R.id.gesture_card)
              {
                Gestures gesturesfragment = new Gestures();
                FragmentTransaction transaction4 = getFragmentManager().beginTransaction();
                transaction4.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction4.replace(this.getId(), gesturesfragment);
                transaction4.addToBackStack(null);
                transaction4.commit();
              }
            if (id == R.id.notification_card)
              {
                Notifications notificationsfragment = new Notifications();
                FragmentTransaction transaction5 = getFragmentManager().beginTransaction();
                transaction5.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction5.replace(this.getId(), notificationsfragment);
                transaction5.addToBackStack(null);
                transaction5.commit();
               }
            if (id == R.id.button_card)
              {
                Buttons buttonfragment = new Buttons();
                FragmentTransaction transaction6 = getFragmentManager().beginTransaction();
                transaction6.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction6.replace(this.getId(), buttonfragment);
                transaction6.addToBackStack(null);
                transaction6.commit();
               }
            if (id == R.id.megalith_about_card)
              {
                MegalithAbout megalithaboutfragment = new MegalithAbout();
                FragmentTransaction transaction7 = getFragmentManager().beginTransaction();
                transaction7.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction7.replace(this.getId(), megalithaboutfragment);
                transaction7.addToBackStack(null);
                transaction7.commit();
               }
            if (id == R.id.quicksettings_card)
              {
                QuickSettings quicksettingsfragment = new QuickSettings();
                FragmentTransaction transaction8 = getFragmentManager().beginTransaction();
                transaction8.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction8.replace(this.getId(), quicksettingsfragment);
                transaction8.addToBackStack(null);
                transaction8.commit();
               }
            if (id == R.id.navigation_card)
              {
                Navigation navigationfragment = new Navigation();
                FragmentTransaction transaction9 = getFragmentManager().beginTransaction();
                transaction9.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction9.replace(this.getId(), navigationfragment);
                transaction9.addToBackStack(null);
                transaction9.commit();
               }
            if (id == R.id.animations_card)
              {
                Animations animationsfragment = new Animations();
                FragmentTransaction transaction10 = getFragmentManager().beginTransaction();
                transaction10.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction10.replace(this.getId(), animationsfragment);
                transaction10.addToBackStack(null);
                transaction10.commit();
               }
            if (id == R.id.battery_card)
              {
                Battery batteryfragment = new Battery();
                FragmentTransaction transaction11 = getFragmentManager().beginTransaction();
                transaction11.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction11.replace(this.getId(), batteryfragment);
                transaction11.addToBackStack(null);
                transaction11.commit();
               }
            if (id == R.id.misc_card)
              {
                Misc miscfragment = new Misc();
                FragmentTransaction transaction12 = getFragmentManager().beginTransaction();
                transaction12.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction12.replace(this.getId(), miscfragment);
                transaction12.addToBackStack(null);
                transaction12.commit();
               }
        }
        
    @Override           
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ANCIENT_SETTINGS;
    }

    public static void lockCurrentOrientation(Activity activity) {
        int currentRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = activity.getResources().getConfiguration().orientation;
        int frozenRotation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        switch (currentRotation) {
            case Surface.ROTATION_0:
                frozenRotation = orientation == Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                frozenRotation = orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                frozenRotation = orientation == Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            case Surface.ROTATION_270:
                frozenRotation = orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }
        activity.setRequestedOrientation(frozenRotation);
    }
}