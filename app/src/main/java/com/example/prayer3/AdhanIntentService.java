package com.example.prayer3;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AdhanIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 3;

    public AdhanIntentService() {
        super("AdhanIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String title = intent.getStringExtra("title");
        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri path = Uri.parse("android.resource://raw/" + R.raw.adhan);
        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(this, "notifyChannel")
                        .setContentTitle("Prayer app")
                        .setContentText(title+" adhan Time")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .setNotificationSilent();
        //try
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
        playSound(getApplicationContext());
    }

    public void playSound(Context context) {
        MediaPlayer sound = MediaPlayer.create(context, R.raw.adhan);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });

        sound.start();
    }
}
