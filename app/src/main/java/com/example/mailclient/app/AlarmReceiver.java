package com.example.mailclient.app;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Leo on 12/05/14.
 */
public class AlarmReceiver extends BroadcastReceiver {

    String notification_msg, notification_title;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Show the toast  like in above screen shot
        notification_msg = intent.getStringExtra("EMAIL");
        notification_title = intent.getStringExtra("TITLE");
        int nId = intent.getIntExtra("ID", 0);
                NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(Mailbox.baseContext)
                        .setSmallIcon(R.drawable.pin_list)
                        .setContentTitle(notification_title)
                        .setContentText(notification_msg);

        Intent resultIntent = new Intent(Mailbox.baseContext, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(Mailbox.baseContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) Mailbox.baseContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(nId, mBuilder.build());
    }
}