package com.example.doctari;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doctari.models.UserModel;
import com.example.doctari.utils.AndroidUtil;
import com.example.doctari.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserNameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button letMeInBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        usernameInput = findViewById(R.id.login_username);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        circleImageView = findViewById(R.id.imageView);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();

        letMeInBtn.setOnClickListener(v -> setUsername());

        circleImageView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 45);
        });
    }

    void setUsername() {
        String username = usernameInput.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        setInProgress(true);
        if (userModel != null) {
            userModel.setUsername(username);
        } else {
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId(), "default_role", null);
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                checkUserRoleAndNavigate();
            } else {
                AndroidUtil.showToast(UserNameActivity.this, "Failed to update user data");
            }
        });
    }

    void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                userModel = task.getResult().toObject(UserModel.class);
                if (userModel != null) {
                    usernameInput.setText(userModel.getUsername());
                }
            } else {
                AndroidUtil.showToast(UserNameActivity.this, "Failed to retrieve user data");
            }
        });
    }

    void checkUserRoleAndNavigate() {
        String userId = FirebaseUtil.currentUserId();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String role = document.getString("role");
                    if ("patient".equals(role)) {
                        navigateToMainActivity("You are logged in as a patient");
                    } else if ("specialist".equals(role)) {
                        navigateToMainActivity("You are logged in as a specialist");
                    } else {
                        redirectToRegistrationChoice();
                    }
                } else {
                    redirectToRegistrationChoice();
                }
            } else {
                AndroidUtil.showToast(UserNameActivity.this, "Failed to retrieve user role");
            }
        });
    }

    void navigateToMainActivity(String message) {
        AndroidUtil.showToast(this, message);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void redirectToRegistrationChoice() {
        Intent intent = new Intent(this, RegistrationChoice.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 45 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            circleImageView.setImageURI(selectedImageUri);
            // Additional logic to upload the selected image to Firebase Storage and update the user's profile picture URL can be added here.
        }
    }
}
