/**
 * @Author Peter Smaal 24-03-2020
 */

package com.locifierapp.locifier.notification;

import android.app.PendingIntent;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.locifierapp.locifier.MainActivity;
import com.locifierapp.locifier.R;

public class ArrivalNotification {
    public ArrivalNotification(PendingIntent pendingIntent, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationChannelBuilder.ARRIVAL_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle("Arrived!")
                .setContentText("You have arrived at your destination")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
}
