package com.example.prayer3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {
    private static String prefsName = "MY_PREF";
    private SharedPreferences.Editor prayerEditor;
    private SharedPreferences prayerPreference;
    public String format;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prayerPreference = getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        prayerEditor =  prayerPreference.edit();
        format= prayerPreference.getString("format","");
        Log.i("Format Value is : ", format+"");

       if (format.equals("24H")){
         prayerEditor.putInt("formatvalue",0);
       }
       else if (format=="12H"){
           prayerEditor.putInt("formatvalue",1);
       }
        String reference= prayerPreference.getString("reference","");
        if (reference=="Ithna Ashari"){
            prayerEditor.putInt("referencevalue",0);
        }
        else if (reference=="University of Islamic Sciences, Karachi"){
            prayerEditor.putInt("referencevalue",1);
        }
        else if (reference=="Islamic Society of North America (ISNA)"){
            prayerEditor.putInt("referencevalue",2);
        }
        else if (reference=="Muslim World League (MWL)"){
            prayerEditor.putInt("referencevalue",3);
        }
        else if (reference=="Umm al-Qura, Makkah(Default)"){
            prayerEditor.putInt("referencevalue",4);
        }
        else if (reference=="Egyptian General Authority of Survey"){
            prayerEditor.putInt("referencevalue",5);
        }
        else if (reference=="Institute of Geophysics, University of Tehran"){
            prayerEditor.putInt("referencevalue",6);
        }

        String doctrines= prayerPreference.getString("doctrines","");
        if (doctrines=="Shafii"){
            prayerEditor.putInt("doctrinesvalues",0);
        }
        else{
            prayerEditor.putInt("doctrinesvalues",88);
        }
        String minutes=prayerPreference.getString("minutes","");
        if (minutes=="5 minutes"){
            prayerEditor.putLong("minutesvalue",5);
        }
        else  if (minutes=="10 minutes"){
            prayerEditor.putLong("minutesvalue",10);
        }
        else  if (minutes=="15 minutes"){
            prayerEditor.putLong("minutesvalue",15);
        }
        else  if (minutes=="20 minutes"){
            prayerEditor.putLong("minutesvalue",20);
        }
        else  if (minutes=="25 minutes"){
            prayerEditor.putLong("minutesvalue",25);
        }
        else  if (minutes=="30 minutes"){
            prayerEditor.putLong("minutesvalue",30);
        }
        prayerEditor.commit();
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