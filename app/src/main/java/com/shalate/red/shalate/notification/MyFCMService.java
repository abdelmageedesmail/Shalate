package com.shalate.red.shalate.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shalate.red.shalate.Activity.HomeActivity;
import com.shalate.red.shalate.Activity.HomePage;
import com.shalate.red.shalate.Activity.PostDetails;
import com.shalate.red.shalate.Activity.ProfessionActivity;
import com.shalate.red.shalate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by abdelmageed on 30/03/17.
 */

public class MyFCMService extends FirebaseMessagingService {

    Intent intent;
    public static boolean NotificationFrom;
    public static String id;
    String mesg;
    private String targetId;
    private Intent i;
    private String order_id;
    private String body;
    private String titleNotification;
    private String subject_id, foriegn_id;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String message = remoteMessage.getData().get("message");
        String title = remoteMessage.getData().get("title");
        subject_id = remoteMessage.getData().get("subject_id");
        foriegn_id = remoteMessage.getData().get("foriegn_id");
        String type = remoteMessage.getData().get("type");
        try {
            JSONArray messages = new JSONArray(message);
            JSONArray titles = new JSONArray(title);
            for (int i = 0; i < messages.length(); i++) {
                JSONObject jsonObject = messages.getJSONObject(i);
                if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
                    body = jsonObject.getString("ar");
                } else {
                    body = jsonObject.getString("en");
                }
            }

            for (int i = 0; i < titles.length(); i++) {
                JSONObject jsonObject = titles.getJSONObject(i);
                if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
                    titleNotification = jsonObject.getString("ar");
                } else {
                    titleNotification = jsonObject.getString("en");
                }
            }
            sendNotification(body, titleNotification, type);
        } catch (JSONException e) {
            e.printStackTrace();
            sendNotification(message, title, "0");
        }
        Log.e("messageNoti", "" + body + "..." + titleNotification);


    }

    private void sendNotification(String messageBody, String title, String type) {
        if (type.equals("1")) {
            intent = new Intent(MyFCMService.this, PostDetails.class);
            intent.putExtra("postId", "" + subject_id);
            intent.putExtra("from", "notification");
        } else if (type.equals("3")) {
            intent = new Intent(MyFCMService.this, ProfessionActivity.class);
            intent.putExtra("id", "" + subject_id);
        } else {
            intent = new Intent(MyFCMService.this, HomeActivity.class);
            intent.putExtra("from", "notification");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
