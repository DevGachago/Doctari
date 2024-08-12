package com.example.doctari;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterServices extends AppCompatActivity {
    private TextView specialistName;

    private EditText  specialistSpecialization, specialistLocation, specialistContact, specialistDetails, specialistCertification;
    private Spinner specialistCategory;
    private Button btnSubmit;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_services);

        // Initialize UI components
        specialistName = findViewById(R.id.specialist_name);
        specialistCategory = findViewById(R.id.specialist_category);
        specialistSpecialization = findViewById(R.id.specialist_specialization);
        specialistLocation = findViewById(R.id.specialist_location);
        specialistContact = findViewById(R.id.specialist_contact);
        specialistDetails = findViewById(R.id.specialist_details);
        specialistCertification = findViewById(R.id.specialist_certifications);
        btnSubmit = findViewById(R.id.btn_submit);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Set button click listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSpecialistDetails();
            }
        });

        fetchUserDataAndSaveDetails();
    }

    private void fetchUserDataAndSaveDetails() {
        // Get the current authenticated user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the user ID
        String userId = currentUser.getUid();

        // Fetch the username from Firestore
        firestore.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Fetch the username from Firestore
                            String username = document.getString("username");
                            specialistName.setText(username);

                        } else {
                            Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveSpecialistDetails() {
        // Get data from input fields
        String name = specialistName.getText().toString().trim();
        String category = specialistCategory.getSelectedItem().toString();
        String specialization = specialistSpecialization.getText().toString().trim();
        String location = specialistLocation.getText().toString().trim();
        String contact = specialistContact.getText().toString().trim();
        String details = specialistDetails.getText().toString().trim();
        String certification = specialistCertification.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(name)) {
            specialistName.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(specialization)) {
            specialistSpecialization.setError("Specialization is required");
            return;
        }
        if (TextUtils.isEmpty(location)) {
            specialistLocation.setError("Location is required");
            return;
        }
        if (TextUtils.isEmpty(contact)) {
            specialistContact.setError("Contact information is required");
            return;
        }

        // Get the current authenticated user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the user ID
        String userId = currentUser.getUid();

        // Set isVerified to false initially
        boolean isVerified = false;

        // Create specialist data map
        Map<String, Object> specialistData = new HashMap<>();
        specialistData.put("name", name);
        specialistData.put("category", category);
        specialistData.put("specialization", specialization);
        specialistData.put("location", location);
        specialistData.put("contact", contact);
        specialistData.put("details", details);
        specialistData.put("certification", certification);
        specialistData.put("isVerified", isVerified);
        specialistData.put("role", "specialist"); // Add role to specialist data


        // Save specialist details to Firestore
        firestore.collection("specialists").document(userId)
                .set(specialistData)
                .addOnSuccessListener(aVoid -> {
                    // Save the role to the users database
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("role", "specialist");

                    firestore.collection("users").document(userId)
                            .update(userData) // Use update to add the new field
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(RegisterServices.this, "Specialist added successfully", Toast.LENGTH_SHORT).show();
                                clearInputs();
                                navigateToMainActivity();
                            })
                            .addOnFailureListener(e -> Toast.makeText(RegisterServices.this, "Failed to save user role", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterServices.this, "Failed to add specialist", Toast.LENGTH_SHORT).show());

    }

    private void clearInputs() {
        specialistName.setText("");
        specialistCategory.setSelection(0); // Assuming the first item is the default one
        specialistSpecialization.setText("");
        specialistLocation.setText("");
        specialistContact.setText("");
        specialistDetails.setText("");
        specialistCertification.setText("");
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterServices.this, MainActivity.class);
        startActivity(intent);
        finish(); // Optionally call finish() if you want to close this activity
    }
}
