package com.example.gihan.chatapp.ui;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.example.gihan.chatapp.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Gihan on 8/5/2017.
 */

public class FirebaseMessagingServices extends com.google.firebase.messaging.FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New Friend Request")
                        .setContentText("you receive new friend request");


        int mNotificationId =(int)System.currentTimeMillis();
        NotificationManager mNotifyMgr =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
