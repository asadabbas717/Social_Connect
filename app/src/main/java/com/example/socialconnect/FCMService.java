package com.example.socialconnect;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import java.util.Collections;
public class FCMService extends FirebaseMessagingService {
    @Override public void onNewToken(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                .set(Collections.singletonMap("fcmToken", token), SetOptions.merge())
                .addOnFailureListener(e -> Log.w("FCM", "Token registration failed"));
    }
}
