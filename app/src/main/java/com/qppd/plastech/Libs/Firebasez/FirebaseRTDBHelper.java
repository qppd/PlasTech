package com.qppd.plastech.Libs.Firebasez;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class FirebaseRTDBHelper<T> {
    private final FirebaseDatabase firebaseDatabase;
    private final DatabaseReference databaseReference;

    public FirebaseRTDBHelper(String path) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(path);
    }

    public FirebaseDatabase getDb() {
        return firebaseDatabase;
    }

    public DatabaseReference getRef() {
        return databaseReference;
    }

    public interface DatabaseCallback {
        void onSuccess();

        void onFailure(Exception e);
    }

    public interface PushCallback extends DatabaseCallback {
        void onKeyReceived(String key);
    }

    public void save(String key, T object, DatabaseCallback callback) {
        databaseReference.child(key).setValue(object)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void push(String key, T object, PushCallback callback) {
        DatabaseReference newRef = databaseReference.child(key).push();
        String pushedKey = newRef.getKey(); // Get the generated key
        newRef.setValue(object)
                .addOnSuccessListener(aVoid -> {
                    if (pushedKey != null) {
                        callback.onKeyReceived(pushedKey);
                    }
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void edit(String key, T object, DatabaseCallback callback) {
        save(key, object, callback);
    }

    public void delete(String key, DatabaseCallback callback) {
        databaseReference.child(key).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void update(String key, Map<String, Object> updates, DatabaseCallback callback) {
        databaseReference.child(key).updateChildren(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
