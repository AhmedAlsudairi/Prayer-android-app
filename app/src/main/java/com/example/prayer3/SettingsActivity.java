package com.example.prayer3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static String prefsName = "MY_PREF";
    SharedPreferences  defaultPreference ;
    SharedPreferences prayerPreference;
    private SharedPreferences.Editor prayerEditor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        defaultPreference = PreferenceManager.getDefaultSharedPreferences(this);
        defaultPreference.registerOnSharedPreferenceChangeListener(this);
        prayerPreference = getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        prayerEditor =   prayerPreference.edit();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("format")){
            if (sharedPreferences.getString(key,"12H").equals("24H"))
                prayerEditor.putInt("formatvalue",0);

            else
                prayerEditor.putInt("formatvalue",1);

        }
        else if(key.equals("reference")){
           String reference = sharedPreferences.getString(key,"Umm al-Qura, Makkah(Default)");
            if (reference.equals("Ithna Ashari"))
                prayerEditor.putInt("referencevalue",0);

            else if (reference.equals("University of Islamic Sciences, Karachi"))
                prayerEditor.putInt("referencevalue",1);

            else if (reference.equals("Islamic Society of North America (ISNA)"))
                prayerEditor.putInt("referencevalue",2);

            else if (reference.equals("Muslim World League (MWL)"))
                prayerEditor.putInt("referencevalue",3);

            else if (reference.equals("Umm al-Qura, Makkah(Default)"))
                prayerEditor.putInt("referencevalue",4);

            else if (reference.equals("Egyptian General Authority of Survey"))
                prayerEditor.putInt("referencevalue",5);

            else if (reference.equals("Institute of Geophysics, University of Tehran"))
                prayerEditor.putInt("referencevalue",6);


        }
        else if(key.equals("doctrines")){
            if (sharedPreferences.getString(key,"").equals("Shafii"))
                prayerEditor.putInt("doctrinesvalues",0);
            else
                prayerEditor.putInt("doctrinesvalues",1);

        }
        else if(key.equals("minutes")){
            String minutes = sharedPreferences.getString(key,"30 minutes");
            if (minutes.equals("5 minutes"))
                prayerEditor.putLong("minutesvalue",5);

            else  if (minutes.equals("10 minutes"))
                prayerEditor.putLong("minutesvalue",10);

            else  if (minutes.equals("15 minutes"))
                prayerEditor.putLong("minutesvalue",15);

            else  if (minutes.equals("20 minutes"))
                prayerEditor.putLong("minutesvalue",20);

            else  if (minutes.equals("25 minutes"))
                prayerEditor.putLong("minutesvalue",25);

            else  if (minutes.equals("30 minutes"))
                prayerEditor.putLong("minutesvalue",30);


        }

        prayerEditor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        defaultPreference.unregisterOnSharedPreferenceChangeListener(this);
    }

    public  static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

        }
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    //    int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.) {
////            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
////            startActivity(intent);
//
//
//
//  //      }
//
//    }
}