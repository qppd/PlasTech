package com.qppd.plastech.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.qppd.plastech.Classes.ActivityLog;
import com.qppd.plastech.Classes.User;
import com.qppd.plastech.Globals.UserGlobal;
import com.qppd.plastech.Libs.DateTimez.DateTimeClass;
import com.qppd.plastech.Libs.Firebasez.FirebaseAuthHelper;
import com.qppd.plastech.Libs.Firebasez.FirebaseRTDBHelper;
import com.qppd.plastech.Libs.Firebasez.FirebaseStorageHelper;
import com.qppd.plastech.Libs.Functionz.FunctionClass;
import com.qppd.plastech.Libs.Validatorz.ValidatorClass;
import com.qppd.plastech.LoginActivity;
import com.qppd.plastech.R;
import com.qppd.plastech.databinding.FragmentProfileBinding;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import com.qppd.plastech.HelpActivity;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private final String TAG = ProfileFragment.class.getSimpleName();

    private FragmentProfileBinding binding;
    private View root;
    private Context context;

    private ProfileViewModel profileViewModel;
    private FirebaseStorageHelper firebaseStorageHelper = new FirebaseStorageHelper();
    private FirebaseRTDBHelper<User> userFirebaseRTDBHelper = new FirebaseRTDBHelper<>("plastech");
    private FirebaseRTDBHelper<ActivityLog> activityLogHelper = new FirebaseRTDBHelper<>("plastech");
    private FirebaseAuthHelper firebaseAuthHelper = new FirebaseAuthHelper();
    private FunctionClass function;
    private DateTimeClass dateTime = new DateTimeClass("MM/dd/yyyy HH:mm:ss");

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> uCropLauncher;
    private Uri imageUri;

    // UI Components
    private CircleImageView civProfilePhoto;
    private ImageView imbUploadProfilePhoto;

    private TextView txtName;
    private ImageView imbEditName;
    private TextView txtEmail;
    private ImageView imbEditEmail;
    private TextView txtPhone;
    private ImageView imbEditPhone;
    private TextView txtRegistrationDate;
    private TextView txtStatus;

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
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        initializeComponents();
        setupObservers();
        loadProfilePhoto();
        
        // Load data
        profileViewModel.loadUserProfile();
        profileViewModel.loadActivityLogs();

        return root;
    }

    private void initializeComponents() {

        civProfilePhoto = root.findViewById(R.id.civProfilePhoto);
        imbUploadProfilePhoto = root.findViewById(R.id.imbUploadProfilePhoto);
        imbUploadProfilePhoto.setOnClickListener(this);

        txtName = root.findViewById(R.id.txtName);
        imbEditName = root.findViewById(R.id.imbEditName);
        imbEditName.setOnClickListener(this);

        txtEmail = root.findViewById(R.id.txtEmail);
        txtPhone = root.findViewById(R.id.txtPhone);
        imbEditPhone = root.findViewById(R.id.imbEditPhone);
        imbEditPhone.setOnClickListener(this);

        txtStatus = root.findViewById(R.id.txtStatus); // Initialize txtStatus

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

        // Add click listener for profile photo animation
        civProfilePhoto.setOnClickListener(this);

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
                    logActivity("Profile Picture Update", "Updated profile picture", "Completed");
                    uploadImage(croppedImageUri, "images/" + UserGlobal.getUser_id(), civProfilePhoto);
                }
            } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                Throwable cropError = UCrop.getError(result.getData());
                function.showMessage("Cropping failed: " + (cropError != null ? cropError.getMessage() : "Unknown error"));
            }
        });
    }

    private void setupObservers() {
        profileViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUserUI(user);
            }
        });

        profileViewModel.getActivityLogsLiveData().observe(getViewLifecycleOwner(), activityLogs -> {
            // Activity logs loaded but not displayed in RecyclerView anymore
        });

        profileViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                function.showMessage(errorMessage);
            }
        });
    }

    private void updateUserUI(User user) {
        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        txtPhone.setText(user.getContact());

        // Set status
        String statusText = user.getStatus() == 1 ? "Active" : "Inactive";
        txtStatus.setText(statusText);
        
        // Load profile picture using Glide
        Glide.with(this)
                .load(R.drawable.profile) // This would be the user's actual profile photo URL
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(civProfilePhoto);
        
        // Animate the UI elements similar to HomeFragment
        civProfilePhoto.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_in));
        txtName.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_in));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.civProfilePhoto:
                // Profile picture animation
                civProfilePhoto.startAnimation(
                    AnimationUtils.loadAnimation(context, R.anim.profile_photo_animation));
                break;
            case R.id.imbUploadProfilePhoto:
                openGallery();
                break;
            case R.id.txtName:
                break;
            case R.id.imbEditName:
                showEditNameDialog();
                break;

            case R.id.imbEditPhone:
                showEditPhoneDialog();
                break;
            case R.id.cardPickUpTrash:
                // Add animation before navigation like HomeFragment
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.card_press_down));
                NavHostFragment.findNavController(this).navigate(R.id.navigation_update);
                break;
            case R.id.cardHelpCenter:
                // Add animation before navigation
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.card_press_down));
                Intent intent = new Intent(context, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.cardMonitoring:
                // Add animation before navigation
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.card_press_down));
                NavHostFragment.findNavController(this).navigate(R.id.navigation_monitor);
                break;
            case R.id.cardRecords:
                // Add animation before navigation
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.card_press_down));
                NavHostFragment.findNavController(this).navigate(R.id.navigation_update);
                break;
            case R.id.btnLogout:
                showLogoutDialog();
                break;
        }
    }

    private void showEditEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_email, null);
        final EditText etEmail = dialogView.findViewById(R.id.etEmail);
        etEmail.setText(txtEmail.getText().toString());

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, id) -> {
                    String newEmail = etEmail.getText().toString().trim();
                    if (!newEmail.isEmpty() && ValidatorClass.validateEmailOnly(newEmail)) {
                        updateUserEmail(newEmail);
                    } else {
                        Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showEditPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_phone, null);
        final EditText etPhone = dialogView.findViewById(R.id.etPhone);
        etPhone.setText(txtPhone.getText().toString());

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, id) -> {
                    String newPhone = etPhone.getText().toString().trim();
                    if (!newPhone.isEmpty() && ValidatorClass.validatePhoneOnly(newPhone)) {
                        updateUserPhone(newPhone);
                    } else {
                        Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateUserEmail(String newEmail) {
        User currentUser = UserGlobal.getUser();
        if (currentUser != null) {
            currentUser.setEmail(newEmail);
            profileViewModel.updateUserProfile(currentUser, new ProfileViewModel.ProfileUpdateCallback() {
                @Override
                public void onSuccess() {
                    txtEmail.setText(newEmail);
                    Toast.makeText(context, "Email updated successfully", Toast.LENGTH_SHORT).show();
                    logActivity("Profile Update", "Updated email address", "Completed");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Failed to update email", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update email", e);
                }
            });
        }
    }

    private void updateUserPhone(String newPhone) {
        User currentUser = UserGlobal.getUser();
        if (currentUser != null) {
            currentUser.setContact(newPhone);
            profileViewModel.updateUserProfile(currentUser, new ProfileViewModel.ProfileUpdateCallback() {
                @Override
                public void onSuccess() {
                    txtPhone.setText(newPhone);
                    Toast.makeText(context, "Phone number updated successfully", Toast.LENGTH_SHORT).show();
                    logActivity("Profile Update", "Updated phone number", "Completed");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Failed to update phone number", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update phone number", e);
                }
            });
        }
    }

    private void updateUserName(String newName) {
        User currentUser = UserGlobal.getUser();
        if (currentUser != null) {
            currentUser.setName(newName);
            profileViewModel.updateUserProfile(currentUser, new ProfileViewModel.ProfileUpdateCallback() {
                @Override
                public void onSuccess() {
                    txtName.setText(newName);
                    Toast.makeText(context, "Name updated successfully", Toast.LENGTH_SHORT).show();
                    logActivity("Profile Update", "Updated name", "Completed");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Failed to update name", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update name", e);
                }
            });
        }
    }

    private void logActivity(String action, String description, String status) {
        String activityId = "activity_" + System.currentTimeMillis();
        ActivityLog activityLog = new ActivityLog(
            activityId,
            UserGlobal.getUser_id(),
            action,
            description,
            dateTime.getFormattedTime(),
            status
        );

        activityLogHelper.save("activity_logs/" + UserGlobal.getUser_id() + "/" + activityId, 
            activityLog, new FirebaseRTDBHelper.DatabaseCallback() {
                @Override
                public void onSuccess() {
                    // Refresh activity logs
                    profileViewModel.loadActivityLogs();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to log activity", e);
                }
            });
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    logout();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_name, null);
        final EditText etName = dialogView.findViewById(R.id.etName);
        etName.setText(txtName.getText().toString());

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, id) -> {
                    String newName = etName.getText().toString().trim();
                    if (!newName.isEmpty() && ValidatorClass.validateLetterOnly(newName)) {
                        updateUserName(newName);
                    } else {
                        Toast.makeText(context, "Please enter a valid name", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
    private void logout() {
        // Log the logout activity
        logActivity("Logout", "User logged out from the application", "Completed");
        
        // Clear user session data
        UserGlobal.setUser(null);
        UserGlobal.setUser_id(null);
        
        // Sign out from Firebase
        firebaseAuthHelper.logout();
        
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}