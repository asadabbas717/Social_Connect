package com.example.socialconnect.data;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.Map;

/** Atomic profile edits preserve metadata across concurrent clients. */
public final class ProfileStore {
    private ProfileStore() {}
    public static Task<Void> save(String uid, String name, String bio, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("users").document(uid);
        return db.runTransaction(transaction -> {
            boolean exists = transaction.get(ref).exists();
            Map<String, Object> fields = ProfileUpdate.fields(name, bio, imageUrl);
            if (!exists) fields.put("createdAt", FieldValue.serverTimestamp());
            transaction.set(ref, fields, SetOptions.merge());
            return null;
        });
    }
}
