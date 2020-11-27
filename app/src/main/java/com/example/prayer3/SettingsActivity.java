package com.example.prayer3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences.Editor prayerEditor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences  prayerPreference = PreferenceManager.getDefaultSharedPreferences(this);
        prayerEditor=   prayerPreference.edit();
       String format= prayerPreference.getString("format","");
       if (format=="24H"){
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
            prayerEditor.putInt("doctrinesvalues",1);
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
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