package com.qppd.plastech.Libs.Firebasez;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
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

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }

    public interface ListCallback<T> {
        void onSuccess(List<T> dataList);
        void onFailure(Exception e);
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

    public void get(String key, Class<T> clazz, DataCallback<T> callback) {
        databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    T object = snapshot.getValue(clazz);
                    callback.onSuccess(object);
                } else {
                    callback.onFailure(new Exception("Data not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public void getList(String key, Class<T> clazz, ListCallback<T> callback) {
        databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<T> dataList = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    T object = childSnapshot.getValue(clazz);
                    if (object != null) {
                        dataList.add(object);
                    }
                }
                callback.onSuccess(dataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }
}
