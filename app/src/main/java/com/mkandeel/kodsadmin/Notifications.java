package com.mkandeel.kodsadmin;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class Notifications extends FirebaseMessagingService {
    private String title, msg;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage != null) {
            /*Log.d("Notification", Objects.requireNonNull(remoteMessage.getFrom()));
            Log.d("Notification", Objects.requireNonNull(remoteMessage.getNotification().getTitle()));
            Log.d("Notification", Objects.requireNonNull(remoteMessage.getNotification().getBody()));

            title = remoteMessage.getNotification().getTitle();
            msg = remoteMessage.getNotification().getBody();

            Notify(title, msg);*/
            if (remoteMessage.getData().size() > 0) {
                title = remoteMessage.getData().get("title");
                msg = remoteMessage.getData().get("message");
                Notify(title, msg);
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    private void Notify(String title, String msg) {
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelID = "com.mkandeel.KodsFreet";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelID, "Dalel el eman",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setSmallIcon(R.drawable.notif)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setSound(sound)
                .setAutoCancel(true);

        Intent intent = new Intent(this, ViewCert.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pi = PendingIntent.getActivity(this, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);

        NotificationManagerCompat mcompact = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            mcompact.notify(1, builder.build());
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

}
