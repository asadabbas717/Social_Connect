package com.example.socialconnect.utils;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;

public class NotificationSender {

    private static final String SERVER_KEY = "AIzaSyBC3CU8Z1U9s7TI3_YOIB9OMcTFckS64KM"; // Replace with your FCM key
    private static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";

    public static void sendNotification(String token, String title, String body) {
        try {
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);

            JSONObject data = new JSONObject();
            data.put("to", token);
            data.put("notification", notification);

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = RequestBody.create(
                    data.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(FCM_API_URL)
                    .addHeader("Authorization", "key=" + SERVER_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("NOTIFY", "Failed to send", e);
                }

                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("NOTIFY", "Notification sent. Code: " + response.code());
                }
            });

        } catch (Exception e) {
            Log.e("NOTIFY", "Exception while sending notification", e);
        }
    }
}
