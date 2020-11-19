package com.example.prayer3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

public class AdhanBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//Builds the notification with all the parameters
        String title = intent.getStringExtra("title");
        Intent intent1 = new Intent(context, AdhanIntentService.class);
        intent1.putExtra("title",title);
        context.startService(intent1);

    }


}
