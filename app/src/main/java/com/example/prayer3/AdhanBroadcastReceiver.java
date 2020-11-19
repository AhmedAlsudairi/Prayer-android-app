package com.example.prayer3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AdhanBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//Builds the notification with all the parameters
        String title = intent.getStringExtra("title");
        Intent intent1 = new Intent(context, AdhanIntentService.class);
        intent1.putExtra("title",title);
        context.startService(intent1);
    }

//    public void playSound() {
//        MediaPlayer sound = MediaPlayer.create(this, R.raw.mySound);
//        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mp.release();
//            }
//
//        });
//
//        quadrantChangeSound.start();
//    }
}
