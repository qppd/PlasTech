package com.qppd.plastech.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.qppd.plastech.Classes.User;
import com.qppd.plastech.Globals.UserGlobal;
import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;
import com.qppd.plastech.Libs.Firebasez.FirebaseStorageHelper;
import com.qppd.plastech.Libs.Functionz.FunctionClass;
import com.qppd.plastech.R;
import com.qppd.plastech.databinding.FragmentProfileBinding;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private final String TAG = ProfileFragment.class.getSimpleName();

    private FragmentProfileBinding binding;
    private View root;
    private Context context;

    private FirebaseStorageHelper firebaseStorageHelper = new FirebaseStorageHelper();
    private FirebaseRTDBHelper<User> userFirebaseRTDBHelper = new FirebaseRTDBHelper<>("plastech");
    private FunctionClass function;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> uCropLauncher;
    private Uri imageUri;

    private CircleImageView civProfilePhoto;
    private ImageView imbUploadProfilePhoto;

    private TextView txtName;
    private ImageView imbEditName;

    private CardView cardPickUpTrash;
    private CardView cardHelpCenter;
    private CardView cardMonitoring;
    private CardView cardRecords;

    private Button btnLogout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = root.getContext();

        function = new FunctionClass(context);

        initializeComponents();
        loadProfilePhoto();
        loadUser();

        return root;
    }

    private void initializeComponents() {

        civProfilePhoto = root.findViewById(R.id.civProfilePhoto);
        imbUploadProfilePhoto = root.findViewById(R.id.imbUploadProfilePhoto);
        imbUploadProfilePhoto.setOnClickListener(this);

        txtName = root.findViewById(R.id.txtName);
        imbEditName = root.findViewById(R.id.imbEditName);
        imbEditName.setOnClickListener(this);

        cardPickUpTrash = root.findViewById(R.id.cardPickUpTrash);
        cardPickUpTrash.setOnClickListener(this);
        cardHelpCenter = root.findViewById(R.id.cardHelpCenter);
        cardHelpCenter.setOnClickListener(this);
        cardMonitoring = root.findViewById(R.id.cardMonitoring);
        cardMonitoring.setOnClickListener(this);
        cardRecords = root.findViewById(R.id.cardRecords);
        cardRecords.setOnClickListener(this);

        btnLogout = root.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                if (imageUri != null) {
                    Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image.jpg"));
                    UCrop.Options options = new UCrop.Options();
                    options.setCompressionQuality(80);
                    options.setAspectRatioOptions(0, new AspectRatio("", 3f, 2f));
                    UCrop uCrop = UCrop.of(imageUri, destinationUri);
                    uCrop.withOptions(options);
                    Intent uCropIntent = uCrop.getIntent(requireContext());
                    uCropLauncher.launch(uCropIntent);
                }
            }
        });

        // Register the launcher for UCrop result
        uCropLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri croppedImageUri = UCrop.getOutput(result.getData());
                if (croppedImageUri != null) {
                    function.showMessage("Uploading");

                    uploadImage(croppedImageUri, "images/" + UserGlobal.getUser_id(), civProfilePhoto);
                }
            } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                Throwable cropError = UCrop.getError(result.getData());
                function.showMessage("Cropping failed: " + (cropError != null ? cropError.getMessage() : "Unknown error"));
            }
        });


    }

    private void loadProfilePhoto(){
        StorageReference profilePictureRef = firebaseStorageHelper.getStorageRef().child("images/" + UserGlobal.getUser_id() + "/profile.jpg");
        profilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Load the image using Glide
                Glide.with(context)
                        .load(uri)
                        .into(civProfilePhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to load profile picture", e);
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void uploadImage(Uri imageUri, String filename, final ImageView imageView) {
        //progressBar.setVisibility(View.VISIBLE);

        firebaseStorageHelper.uploadImage(imageUri, filename, new FirebaseStorageHelper.UploadCallback() {
            @Override
            public void onSuccess(Uri downloadUri) {
                //progressBar.setVisibility(View.GONE);
                Glide.with(context)
                        .load(downloadUri)
                        .into(imageView);
                function.showMessage("Success uploading image!");
                //Toast.makeText(MyStoreActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                //progressBar.setVisibility(View.GONE);
                function.showMessage("Failed to upload image!");
                //Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUser() {
        userFirebaseRTDBHelper.getRef().child("users/" + UserGlobal.getUser_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user =  snapshot.getValue(User.class);
                    txtName.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.civProfilePhoto:
                 break;
            case R.id.imbUploadProfilePhoto:
                openGallery();
                break;
            case R.id.txtName:
                break;
            case R.id.imbEditName:
                break;
            case R.id.cardPickUpTrash:
                break;
            case R.id.cardHelpCenter:
                break;
            case R.id.cardMonitoring:
                break;
            case R.id.cardRecords:
                break;

        }
    }
}