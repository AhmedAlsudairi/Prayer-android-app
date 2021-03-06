package com.example.prayer3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
   // private TextView text1;
    private TextView textView6;
    private static PrayTime prayer;
    private static String prefsName = "MY_PREF";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//
//        calx();



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // ---------------------
        prayer = new PrayTime();

        textView6=(TextView) findViewById(R.id.textView6);

        Thread myThread = new Thread(){
            public void run(){
                try {
                    while (!isInterrupted()){
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Calendar calendar=Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm:ss a");
                                String curr=simpleDateFormat.format(calendar.getTime());
                                textView6.setText(curr);
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        myThread.start();



        //---------------

        //initialize sharedPrefrence and editor
        prayerPreference = getSharedPreferences(prefsName,Context.MODE_PRIVATE);

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


        int TimeFormat = prayerPreference.getInt("formatvalue",1);
        int CaluclationMethod = prayerPreference.getInt("referencevalue", 4);
        int JuristicMethod = prayerPreference.getInt("doctrinesvalues",0);

        setPrayerSettings(prayer,TimeFormat,CaluclationMethod,JuristicMethod,3);


        // saving prayer times in millisecond format
        try {
            CalculatePrayerTimesOnly(prayer);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Notification channel set up
        createNotificationChannel();
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


                    ArrayList<String> prayerTimeWithName = CalculatePrayerTime(prayer);
                    prayerEditor.putString("fajr", prayerTimeWithName.get(0));
                    prayerEditor.putString("sunrise", prayerTimeWithName.get(1));
                    prayerEditor.putString("dohur", prayerTimeWithName.get(2));
                    prayerEditor.putString("asr", prayerTimeWithName.get(3));
                    prayerEditor.putString("sunset", prayerTimeWithName.get(4));
                    prayerEditor.putString("magreb", prayerTimeWithName.get(5));
                    prayerEditor.putString("isha", prayerTimeWithName.get(6));
               //     double s=Double.parseDouble(prayerTimeWithName.get(0));

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

        //building the notification when the app is resume
        buldingTheNotitfications();
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
            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
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


    private void setSingleExactAlarm(long time, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        boolean alreadyDone = System.currentTimeMillis() > time;
        if (alreadyDone){
            System.out.println(true);
            return;
        }
            System.out.println(false);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }else {
            //silence mode
            Intent silenceIntent = new Intent(MainActivity.this, silenceBroadcastReceiver.class);
            Random silence = new Random();
            int silenceInt = silence.nextInt();
            PendingIntent silencePendingIntent = PendingIntent.getBroadcast(MainActivity.this, silenceInt, silenceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            long silenceInterval = prayerPreference.getLong("minutesvalue",30);
            //setup the notification
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pIntent);
                //TODO
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time + 1000 * 60 * silenceInterval, silencePendingIntent);
            } else if (android.os.Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pIntent);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, time + 1000 * 60 * silenceInterval, silencePendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, time + 1000 * 60 * silenceInterval, silencePendingIntent);
            }
        }
    }
    // Bulding the notitfications for all prayers

    private void buldingTheNotitfications() {
        //fajr notification
        long fajrTime = prayerPreference.getLong("FajrTime",0);
        System.out.println(fajrTime);
        Intent fajrIntent = new Intent(MainActivity.this,AdhanBroadcastReceiver.class);
        fajrIntent.putExtra("title","Fajr");
        Random rFajr = new Random();
        int fajr = rFajr.nextInt();
        PendingIntent fajrPendingIntent = PendingIntent.getBroadcast(MainActivity.this,fajr,fajrIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        setSingleExactAlarm(fajrTime,fajrPendingIntent);

        //dohur notification
        long dohurTime = prayerPreference.getLong("DhuhrTime",0);
        System.out.println(dohurTime);
        Intent dohurIntent = new Intent(MainActivity.this,AdhanBroadcastReceiver.class);
        dohurIntent.putExtra("title","Dohur");
        Random rDohur = new Random();
        int dohur = rDohur.nextInt();
        PendingIntent dohurPendingIntent = PendingIntent.getBroadcast(MainActivity.this,dohur,dohurIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        setSingleExactAlarm(dohurTime,dohurPendingIntent);

        //asr notification
        long asrTime = prayerPreference.getLong("AsrTime",0);
        System.out.println(asrTime);
        Intent asrIntent = new Intent(MainActivity.this,AdhanBroadcastReceiver.class);
        asrIntent.putExtra("title","Asr");
        Random rAsr = new Random();
        int asr = rAsr.nextInt();
        PendingIntent asrPendingIntent = PendingIntent.getBroadcast(MainActivity.this,asr,asrIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        setSingleExactAlarm(asrTime,asrPendingIntent);

        //magreb notification
        long magrebTime = prayerPreference.getLong("MaghribTime",0);
        System.out.println(magrebTime);
        Intent magrebIntent = new Intent(MainActivity.this,AdhanBroadcastReceiver.class);
        magrebIntent.putExtra("title","Magreb");
        Random rMagreb = new Random();
        int magreb = rMagreb.nextInt();
        PendingIntent magrebPendingIntent = PendingIntent.getBroadcast(MainActivity.this,magreb,magrebIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        setSingleExactAlarm(magrebTime,magrebPendingIntent);

        //isha notification
        long ishaTime = prayerPreference.getLong("IshaTime",0);
        System.out.println(ishaTime);
        Intent ishaIntent = new Intent(MainActivity.this,AdhanBroadcastReceiver.class);
        ishaIntent.putExtra("title","Isha");
        Random rIsha = new Random();
        int isha = rIsha.nextInt();
        PendingIntent ishaPendingIntent = PendingIntent.getBroadcast(MainActivity.this,isha,ishaIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        setSingleExactAlarm(ishaTime,ishaPendingIntent);
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

    private boolean runtime_permission_notifications(){
        if (Build.VERSION.SDK_INT >= 23
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_NOTIFICATION_POLICY },
                    500);
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
        if(requestCode == 200){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
                runtime_permission_notifications();
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


    public void CalculatePrayerTimesOnly(PrayTime prayer) throws ParseException {
        /*
        This method will calculate prayerTimes in given prayer object, then convert times from String to long in millisecond.
        Finally it save times (millisecond) of prayers in SharedPreferences with its key. for example: prayerPreference.getLong("FajrTime",0) will give you fajr time in millisecond (47940000)
         */

        prayerPreference = PreferenceManager.getDefaultSharedPreferences(this);
        prayerEditor = prayerPreference.edit();

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

        String [] names = {"FajrTime","SunriseTime","DhuhrTime","AsrTime","SunsetTime","MaghribTime","IshaTime"};
        for (int i = 0; i < prayerTimes.size(); i++) {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String strDate= formatter.format(date);
            System.out.println(strDate);
            String dateString = strDate+" "+prayerTimes.get(i);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date time = format.parse(dateString);
            long millisecond = time.getTime();
            prayerEditor.putLong(names[i],millisecond);
            System.out.println(names[i]+" "+prayerPreference.getLong(names[i],0));
        }
        return;
    }




}
