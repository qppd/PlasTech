package com.qppd.plastech.Libs.Firebasez;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class FirebaseDatabaseHelper<T> {
    private final FirebaseDatabase firebaseDatabase;
    private final DatabaseReference databaseReference;
    boolean result = false;

    public FirebaseDatabaseHelper(String path) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(path);
    }

    public FirebaseDatabase getDb(){
        return firebaseDatabase;
    }

    public DatabaseReference getRef(){
        return databaseReference;
    }

    public boolean save(String key, T object) {
        databaseReference.child(key).setValue(object)
                .addOnSuccessListener(aVoid -> {
                    result = true;
                })
                .addOnFailureListener(e -> {
                    result = false;

                });
        return result;
    }

    public String push(String key, T object) {
        DatabaseReference newRef = databaseReference.child(key).push();
        newRef.setValue(object)
                .addOnSuccessListener(aVoid -> {
                    result = true;
                })
                .addOnFailureListener(e -> {
                    result = false;
                });

        return newRef.getKey();
    }

    public boolean edit(String key, T object) {
        return save(key, object);
    }

    public boolean delete(String key) {
        databaseReference.child(key).removeValue()
                .addOnSuccessListener(aVoid -> {
                    result = true;
                })
                .addOnFailureListener(e -> {
                    result = false;
                });

        return result;
    }

    public boolean update(String key, Map<String, Object> updates) {
        databaseReference.child(key).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    result = true;
                })
                .addOnFailureListener(e -> {
                    result = false;
                });

        return result;
    }
}

