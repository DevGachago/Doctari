package com.example.doctari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.doctari.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::checkUserRole, 1000);
    }

    private void checkUserRole() {
        if (FirebaseUtil.isLoggedIn()) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String role = document.getString("role");
                        navigateBasedOnRole(role);
                    } else {
                        redirectToRegistrationChoice();
                    }
                } else {
                    Toast.makeText(Splash.this, "Failed to retrieve user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            redirectToPhoneNumberActivity();
        }
    }

    private void navigateBasedOnRole(String role) {
        Intent intent;
        if ("patient".equals(role)) {
            Toast.makeText(this, "You are logged in as a patient", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, MainActivity.class);
        } else if ("specialist".equals(role)) {
            Toast.makeText(this, "You are logged in as a specialist", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, MainActivity.class);
        } else {
            Toast.makeText(this, "Invalid role: " + role, Toast.LENGTH_SHORT).show();
            redirectToRegistrationChoice();
            return;
        }
        startActivity(intent);
        finish();
    }

    private void redirectToRegistrationChoice() {
        startActivity(new Intent(this, RegistrationChoice.class));
        finish();
    }

    private void redirectToPhoneNumberActivity() {
        startActivity(new Intent(this, PhoneNumberActivity.class));
        finish();
    }
}
