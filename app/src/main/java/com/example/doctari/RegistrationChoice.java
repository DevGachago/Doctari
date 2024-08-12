package com.example.doctari;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doctari.models.UserModel;
import com.example.doctari.utils.AndroidUtil;
import com.example.doctari.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationChoice extends AppCompatActivity {

    private Button btnRegisterPatient;
    private Button btnRegisterServiceProvider;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_choice);


        btnRegisterPatient = findViewById(R.id.btn_register_patient);
        btnRegisterServiceProvider = findViewById(R.id.btn_register_service_provider);


        btnRegisterPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(RegistrationChoice.this, PatientRegistrationFom.class);
                //startActivity(intent);
                updateUserRole();

            }
        });

        btnRegisterServiceProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(RegistrationChoice.this, RegisterServices.class);
                updateSpecRole();
            }
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


    }

    private void updateSpecRole() {
        // Get the current authenticated user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the user ID
        String userId = currentUser.getUid();

        // Save the role to the users database
        Map<String, Object> userData = new HashMap<>();
        userData.put("role", "specialist");

        firestore.collection("users").document(userId)
                .update(userData) // Use update to add the new field
                .addOnSuccessListener(aVoid1 -> {
                    Intent intent = new Intent(RegistrationChoice.this, MainActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Toast.makeText(RegistrationChoice.this, "Failed to save user role", Toast.LENGTH_SHORT).show());

    }

    private void updateUserRole() {

        // Get the current authenticated user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the user ID
        String userId = currentUser.getUid();

        // Save the role to the users database
        Map<String, Object> userData = new HashMap<>();
        userData.put("role", "patient");

        firestore.collection("users").document(userId)
                .update(userData) // Use update to add the new field
                .addOnSuccessListener(aVoid1 -> {
                    Intent intent = new Intent(RegistrationChoice.this, MainActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Toast.makeText(RegistrationChoice.this, "Failed to save user role", Toast.LENGTH_SHORT).show());
    }
}