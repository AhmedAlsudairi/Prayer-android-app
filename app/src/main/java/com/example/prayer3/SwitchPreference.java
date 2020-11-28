//package com.example.prayer3;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.Preference;
//import android.preference.PreferenceFragment;
//
//import androidx.annotation.Nullable;
//import androidx.preference.PreferenceManager;
//
//public class SwitchPreference extends PreferenceFragment {
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.root_preferences);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        android.preference.SwitchPreference pre=(android.preference.SwitchPreference)findPreference("able");
//        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
//        pre.setOnPreferenceChangeListener(new  android.preference.Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o) {
//                return false;
//            }
//        });
//    }
//}
