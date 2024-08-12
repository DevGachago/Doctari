package com.example.doctari;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doctari.models.UserModel;
import com.example.doctari.utils.AndroidUtil;
import com.example.doctari.utils.FirebaseUtil;
import com.example.doctari.utils.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    UserModel currentUserModel;
    Uri selectedImageUri;

    private static final String TAG = "MainActivity";
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this, "FCM can't post notifications without POST_NOTIFICATIONS permission",
                            Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        phoneInput = findViewById(R.id.profile_phone);
        updateProfileBtn = findViewById(R.id.profle_update_btn);
        progressBar = findViewById(R.id.profile_progress_bar);
        logoutBtn = findViewById(R.id.logout_btn);

        getUserData();

        updateProfileBtn.setOnClickListener((v -> {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic("weather")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "profile updated successfully";
                            if (!task.isSuccessful()) {
                                msg = "failed to update profile";
                            }
                            Log.d(TAG, msg);
                            Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();

                            // Show a notification
                            MyFirebaseMessagingService.sendNotification(ProfileActivity.this, msg);
                            updateBtnClick();
                        }
                    });
        }));

        logoutBtn.setOnClickListener((v) -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseUtil.logout();
                        Intent intent = new Intent(ProfileActivity.this, Splash.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);

        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl().addOnCompleteListener(uriTask -> {
                                if (uriTask.isSuccessful()) {
                                    Uri downloadUri = uriTask.getResult();
                                    currentUserModel.setProfilePictureUrl(downloadUri.toString());
                                    saveProfilePictureUrl(downloadUri.toString());
                                } else {
                                    // Handle failure to get download URL
                                    setInProgress(false);
                                    AndroidUtil.showToast(ProfileActivity.this, "Failed to get profile picture URL");
                                }
                            });
                        } else {
                            // Handle failure to upload file
                            setInProgress(false);
                            AndroidUtil.showToast(ProfileActivity.this, "Failed to upload profile picture");
                        }
                    });
        } else {
            updateToFirestore();
        }
    }

    private void saveProfilePictureUrl(String profilePictureUrl) {
        String uid = FirebaseUtil.getCurrentUser().getUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Save the URI in Firestore under the user's profilePictureUrl field
        firestore.collection("users").document(uid)
                .update("profilePictureUrl", profilePictureUrl)
                .addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        // Also save the URI in Firestore under the specialists' profilePictureUrl field
                        firestore.collection("specialists").document(uid)
                                .update("profilePictureUrl", profilePictureUrl)
                                .addOnCompleteListener(specialistSaveTask -> {
                                    if (specialistSaveTask.isSuccessful()) {
                                        // Successfully saved profile picture URL in both places
                                        updateToFirestore();
                                    } else {
                                        // Handle failure to save the profile picture URL in specialists collection
                                        setInProgress(false);
                                        AndroidUtil.showToast(ProfileActivity.this, "Failed to save profile picture URL in specialists collection");
                                    }
                                });
                    } else {
                        // Handle failure to save the profile picture URL in users collection
                        setInProgress(false);
                        AndroidUtil.showToast(ProfileActivity.this, "Failed to save profile picture URL in users collection");
                    }
                });
    }

    void updateToFirestore() {
        // Update the user details in Firestore
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(ProfileActivity.this, "Updated successfully");
                    } else {
                        AndroidUtil.showToast(ProfileActivity.this, "Update failed");
                    }
                });
    }

    void getUserData() {
        setInProgress(true);

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(ProfileActivity.this, uri, profilePic);

                        // Save the URI in Firestore under the user's profilePictureUrl field
                        String uid = FirebaseUtil.getCurrentUser().getUid();
                        FirebaseFirestore.getInstance().collection("users").document(uid)
                                .update("profilePictureUrl", uri.toString())
                                .addOnCompleteListener(saveTask -> {
                                    if (saveTask.isSuccessful()) {
                                        // Also save the URI in Firestore under the specialists' profilePictureUrl field
                                        FirebaseFirestore.getInstance().collection("specialists").document(uid)
                                                .update("profilePictureUrl", uri.toString())
                                                .addOnCompleteListener(specialistSaveTask -> {
                                                    if (specialistSaveTask.isSuccessful()) {
                                                        // Successfully saved profile picture URL in both places
                                                        setInProgress(false);
                                                    } else {
                                                        // Handle failure to save the profile picture URL in specialists collection
                                                        setInProgress(false);
                                                        AndroidUtil.showToast(ProfileActivity.this, "Failed to save profile picture URL in specialists collection");
                                                    }
                                                });
                                    } else {
                                        // Handle failure to save the profile picture URL in users collection
                                        setInProgress(false);
                                        AndroidUtil.showToast(ProfileActivity.this, "Failed to save profile picture URL in users collection");
                                    }
                                });
                    } else {
                        // Handle failure to get profile picture URL
                        setInProgress(false);
                        AndroidUtil.showToast(ProfileActivity.this, "Failed to get profile picture URL");
                    }
                });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                currentUserModel = task.getResult().toObject(UserModel.class);
                if (currentUserModel != null) {
                    usernameInput.setText(currentUserModel.getUsername());
                    phoneInput.setText(currentUserModel.getPhone());
                }
            } else {
                // Handle failure to retrieve user details
                AndroidUtil.showToast(ProfileActivity.this, "Failed to retrieve user details");
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 45 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profilePic.setImageURI(selectedImageUri);
        }
    }
}
