//package com.example.prayer3;
//
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//public class AdhanBroadcastReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
////        NotificationManager mNotifyManager =  (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
////Builds the notification with all the parameters
//        NotificationCompat.Builder notifyBuilder =
//                new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setContentTitle("Prayer app")
//                        .setContentText("Adhan Time")
//                        .setSmallIcon(R.drawable.ic_launcher_background)
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                        .setDefaults(NotificationCompat.DEFAULT_ALL);
//        //try
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(200,notifyBuilder.build());
//    }
//}
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