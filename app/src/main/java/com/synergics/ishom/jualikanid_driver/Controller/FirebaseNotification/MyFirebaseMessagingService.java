package com.synergics.ishom.jualikanid_driver.Controller.FirebaseNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.synergics.ishom.jualikanid_driver.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "asd";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        String tittle = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        String action = remoteMessage.getNotification().getClickAction();

        String delivery_id = remoteMessage.getData().get("delivery_id");
        Bundle bundle = new Bundle();
        bundle.putString("delivery_id", delivery_id);

        Intent intent = new Intent(action);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        long array[] = {300l, 300l, 300l, 0l, 0l, 0l, 300l, 300l, 300l, 0l, 0l, 0l, 300l, 300l , 300l};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(tittle);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.icon_car);
        BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_car);
        Bitmap largeIconBitmap = bitmapDrawable.getBitmap();
        builder.setLargeIcon(largeIconBitmap);
        builder.setAutoCancel(true);
        builder.setVibrate( array);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }
}
