package com.qppd.plastech.Libs.Firebasez;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageHelper {

    private static final String TAG = "FirebaseStorageHelper";
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public FirebaseStorageHelper() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public StorageReference getStorageRef() {
        return storageRef;
    }

    public void uploadImage(Uri imageUri, String filename, final UploadCallback callback) {
        if (imageUri == null) {
            Log.e(TAG, "Image URI is null");
            return;
        }

        StorageReference imageRef = storageRef.child("images/" + filename + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                Log.d(TAG, "Image uploaded successfully: " + downloadUri.toString());
                                callback.onSuccess(downloadUri);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to upload image", e);
                        callback.onFailure(e);
                    }
                });
    }

    public interface UploadCallback {
        void onSuccess(Uri downloadUri);
        void onFailure(Exception e);
    }
}
