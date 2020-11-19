package com.example.prayer3;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.example.libpraytime.PrayTime;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver locationReceiver;
    private SharedPreferences prayerPreference;
    private SharedPreferences.Editor prayerEditor;
    private TextView fajrTextView;
    private TextView dohurTextView;
    private TextView asrTextView;
    private TextView magrebTextView;
    private TextView ishaTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        //initialize sharedPrefrence and editor
        prayerPreference = PreferenceManager.getDefaultSharedPreferences(this);
        prayerEditor = prayerPreference.edit();

        //initialize prayer time text views
        fajrTextView = findViewById(R.id.fajrView);
        dohurTextView = findViewById(R.id.dohurView);
        asrTextView = findViewById(R.id.asrView);
        magrebTextView = findViewById(R.id.magrebView);
        ishaTextView = findViewById(R.id.ishaView);
        // if location permissions already granted, start the work.
        if(!runtime_permission()){
            //Start location service then update the UI, Note: the service will stop when we receive location info
                Intent i = new Intent(getApplicationContext(), LocationService.class);
                startService(i);
        }
        setSupportActionBar(toolbar);

        // Notification set up
        createNotificationChannel();
        buldingTheNotitfications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if the receiver not created yet, create it. This to avoid recreating receiver each time
        if(locationReceiver == null){
            locationReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    /*
                    Receive incoming long,lat from location service, provide it to prayerPreference
                    for later use in calculating,checking of prayer times and stop the service, then
                    update the UI
                     */
                    Intent i = new Intent(getApplicationContext(), LocationService.class);
                    stopService(i);
                    float lang = (float) intent.getExtras().getFloat("long",0);
                    float lat = (float) intent.getExtras().getFloat("lat",0);
                    prayerEditor.putFloat("long", lang);
                    prayerEditor.putFloat("lat", lat);
                    prayerEditor.commit();
                    PrayTime prayer = new PrayTime();

                    //TODO make the settings comes from shared prefrence so it's becoming dynamic
                    setPrayerSettings(prayer,1,4,0,3);

                    ArrayList<String> prayerTimeWithName = CalculatePrayerTime(prayer);
                    prayerEditor.putString("fajr", prayerTimeWithName.get(0));
                    prayerEditor.putString("sunrise", prayerTimeWithName.get(1));
                    prayerEditor.putString("dohur", prayerTimeWithName.get(2));
                    prayerEditor.putString("asr", prayerTimeWithName.get(3));
                    prayerEditor.putString("sunset", prayerTimeWithName.get(4));
                    prayerEditor.putString("magreb", prayerTimeWithName.get(5));
                    prayerEditor.putString("isha", prayerTimeWithName.get(6));
                    prayerEditor.commit();

                    fajrTextView.setText(prayerPreference.getString("fajr",""));
                    //TODO Implement text views for sunrise, sunset which keys are sunrise and sunset
                    dohurTextView.setText(prayerPreference.getString("dohur",""));
                    asrTextView.setText(prayerPreference.getString("asr",""));
                    magrebTextView.setText(prayerPreference.getString("magreb",""));
                    ishaTextView.setText(prayerPreference.getString("isha",""));
                }

            };
        }
        registerReceiver(locationReceiver, new IntentFilter("location_updates"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //to avoid consuming the battery and memory, stop the receiver when we finish.
        if(locationReceiver !=null){
            unregisterReceiver(locationReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // create notification channel to (API 26 and higher)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("notifyChannel", "Prayer app Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Prayer app");
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }

    // Bulding the notitfications for all prayers
    private void buldingTheNotitfications() {
        //fajr notification
        Intent fajrIntent = new Intent(MainActivity.this,AdhanBroadcastReceiver.class);
        fajrIntent.putExtra("title","Fajr");
        Random rFajr = new Random();
        int fajr = rFajr.nextInt();
        PendingIntent fajrPendingIntent = PendingIntent.getBroadcast(MainActivity.this,fajr,fajrIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,10000,fajrPendingIntent); // Require current pray time to calculate the remaining time for notification **

        //dohur notification
        Intent dohurIntent = new Intent(MainActivity.this,AdhanBroadcastReceiver.class);
        dohurIntent.putExtra("title","Dohur");
        Random rDohur = new Random();
        int dohur = rDohur.nextInt();
        PendingIntent dohurPendingIntent = PendingIntent.getBroadcast(MainActivity.this,dohur,dohurIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,10000,dohurPendingIntent);

        //asr notification
        Intent asrIntent = new Intent(MainActivity.this,AdhanBroadcastReceiver.class);
        asrIntent.putExtra("title","Asr");
        Random rAsr = new Random();
        int asr = rAsr.nextInt();
        PendingIntent asrPendingIntent = PendingIntent.getBroadcast(MainActivity.this,asr,asrIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,10000,asrPendingIntent);

        //magreb notification
        Intent magrebIntent = new Intent(MainActivity.this,AdhanBroadcastReceiver.class);
        magrebIntent.putExtra("title","Magreb");
        Random rMagreb = new Random();
        int magreb = rMagreb.nextInt();
        PendingIntent magrebPendingIntent = PendingIntent.getBroadcast(MainActivity.this,magreb,magrebIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,10000,magrebPendingIntent);

        //isha notification
        Intent ishaIntent = new Intent(MainActivity.this,AdhanBroadcastReceiver.class);
        ishaIntent.putExtra("title","Isha");
        Random rIsha = new Random();
        int isha = rIsha.nextInt();
        PendingIntent ishaPendingIntent = PendingIntent.getBroadcast(MainActivity.this,isha,ishaIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,10000,ishaPendingIntent);
    }
    //Check if location permissions already granted or not.
    private boolean runtime_permission(){
        if( Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return true;
        }
        return false;
    }


    //Required to check if we get the location permissions correctly or not, if not ask for it again.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED)
                runtime_permission();
        }
    }

    public void setPrayerSettings(PrayTime prayer, int TimeFormat, int CalculationMethod, int JuristicMethod , int AdjustingMethod){
        /*
        The following method will set all prayer time settings on prayerTime object before calculation.
        *Note: the user can change any of those parameter value, because those settings are public to the user
        also each one of them will effect the calculation process => leading to different prayer times.
        Here is explanation for each parameter and it's possible values:

         1-TimeFormat, Possible values for Time format are:
         0: For Time24(Default)
         1: For Time12
         2: 12-hour format with no suffix
         3: floating point number

         2-CalculationMethod for prayer time, Possible values for calculation method:
         0:  Ithna Ashari
         1:  University of Islamic Sciences, Karachi
         2:  Islamic Society of North America (ISNA)
         3:  Muslim World League (MWL)
         4:  Umm al-Qura, Makkah(Default)
         5:  Egyptian General Authority of Survey
         6:  Institute of Geophysics, University of Tehran
         7:  Custom Setting

         3-Juristic Methods for asr pray time , Possible values for Juristic Methods:
         0:  Shafii (Default)
         1:  Hanafi

         4- Adjusting Methods for Higher Latitudes, Possible values for Adjusting Methods:
         0:  No adjustment
         1:  middle of night
         2:  1/7th of night
         3:  angle/60th of night (Default)
         */

        prayer.setTimeFormat(TimeFormat);
        prayer.setCalcMethod(CalculationMethod);
        prayer.setAsrJuristic(JuristicMethod);
        prayer.setAdjustHighLats(AdjustingMethod);
    }

    public ArrayList<String> CalculatePrayerTime(PrayTime prayer){
        /*
        This method will calculate prayerTimes in given prayer object, and return ArrayList<String>
        containing strings of each prayerTime, ex: fajr 4:47pm in index 0 and so on.
         */


        //Get lang,lat of our shared prefrence
        double lang =(double) prayerPreference.getFloat("long",-1);
        double lat = (double) prayerPreference.getFloat("lat",-1);

        double timezone = prayer.getBaseTimeZone();


        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayer.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        ArrayList<String> prayerTimes = prayer.getPrayerTimes(cal, lat, lang, timezone);
        ArrayList<String> prayerNames = prayer.getTimeNames();
        ArrayList<String> prayerNameWithTime= new ArrayList<>();

        for (int i = 0; i < prayerTimes.size(); i++) {
            System.out.println(prayerNames.get(i) + " - " + prayerTimes.get(i));
            prayerNameWithTime.add(prayerTimes.get(i));
        }
        return prayerNameWithTime;
    }
}