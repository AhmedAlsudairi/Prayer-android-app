package com.example.prayer3;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import androidx.annotation.NonNull;
public class LocationService extends Service {
    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Since we already granted the permission in main activity, so ignore errors which ask for it.
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Intent i = new Intent("location_updates");
                float lang = (float)location.getLongitude();
                float lat = (float)location.getLatitude();
                i.putExtra("long", lang);
                i.putExtra("lat",lat);
                sendBroadcast(i);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                //This will redirect the user to settings for location to enable it
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //Todo enhance updating process using a button to update the location
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,0,locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager !=null){
            locationManager.removeUpdates(locationListener);
        }
    }


}
