package com.project22.medikit.AlarmReceiver;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.project22.medikit.HomeActivity.HomeActivityMedicine;
import com.project22.medikit.R;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "MEDIKIT_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {


        int notificationId = intent.getIntExtra("notificationId", 0);
        String message = intent.getStringExtra("medicineName");


        Ringtone ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        ringtone.play();

        // Call MainActivity when notification is tapped.
        Intent mainIntent = new Intent(context, HomeActivityMedicine.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // NotificationManager
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API 26 and above
            CharSequence channelName = "My Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        // Prepare Notification
        @SuppressLint("ResourceAsColor")
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Medicine time")
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setColor(R.color.blue_sky)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true);

        // Notify
        notificationManager.notify(notificationId, builder.build());


        Toast.makeText(context, "Fired!", Toast.LENGTH_LONG).show();




        SystemClock.sleep(10000);
        ringtone.stop();
    }


}
