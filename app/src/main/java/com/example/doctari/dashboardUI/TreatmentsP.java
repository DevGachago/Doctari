package com.example.doctari.dashboardUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doctari.MainActivity;
import com.example.doctari.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class TreatmentsP extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private EditText etUsername;
    private TextView btnAuthorize, checkAuth;
    private EditText etHeight, etWeight, etBloodPressure, etHeartRate, etBmi, etCholesterolLevels,
            etBloodSugarLevels, etBloodTestResults, etUrineTestResults, etImagingResults,
            etOtherDiagnosticTests, etPrescribedMedications, etTherapies, etSurgicalRecommendations,
            etFollowUpAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatments_p);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        etUsername = findViewById(R.id.usernameA);
        btnAuthorize = findViewById(R.id.authoriz);
        checkAuth = findViewById(R.id.checkAuth);

        // Initialize EditText fields
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etBloodPressure = findViewById(R.id.etBloodPressure);
        etHeartRate = findViewById(R.id.etHeartRate);
        etBmi = findViewById(R.id.etBmi);
        etCholesterolLevels = findViewById(R.id.etCholesterolLevels);
        etBloodSugarLevels = findViewById(R.id.etBloodSugarLevels);
        etBloodTestResults = findViewById(R.id.etBloodTestResults);
        etUrineTestResults = findViewById(R.id.etUrineTestResults);
        etImagingResults = findViewById(R.id.etImagingResults);
        etOtherDiagnosticTests = findViewById(R.id.etOtherDiagnosticTests);
        etPrescribedMedications = findViewById(R.id.etPrescribedMedications);
        etTherapies = findViewById(R.id.etTherapies);
        etSurgicalRecommendations = findViewById(R.id.etSurgicalRecommendations);
        etFollowUpAppointments = findViewById(R.id.etFollowUpAppointments);

        btnAuthorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorizeDoctor();
            }
        });

        checkAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TreatmentsP.this,Treatments.class);
                startActivity(intent);
            }
        });

        // Fetch and display the treatment data
        fetchAndDisplayTreatmentDetails();
        retrievePatientUsername();
    }

    private void retrievePatientUsername() {

        String currentUserId = auth.getCurrentUser().getUid();

        // Retrieve the patient's username using the patientUid
        firestore.collection("users").document(currentUserId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot patientSnapshot) {
                        if (patientSnapshot.exists()) {
                            String patientUsername = patientSnapshot.getString("username");

                            // Populate the TextView with the patient's username
                        } else {
                            Toast.makeText(TreatmentsP.this, "Patient username not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TreatmentsP.this, "Failed to retrieve patient's username", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void authorizeDoctor() {
        String currentUserId = auth.getCurrentUser().getUid();  // Get the current patient's UID
        String docname = etUsername.getText().toString().trim();  // Get the doctor's username

        if (docname.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        // First, get the doctor's UID from the "users" collection using the provided username
        firestore.collection("users")
                .whereEqualTo("username", docname)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Assuming usernames are unique, there should be only one document
                            DocumentSnapshot doctorSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String doctorUid = doctorSnapshot.getId();  // Get the UID of the doctor

                            // Retrieve the patient's username using the patientUid
                            firestore.collection("users").document(currentUserId).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot patientSnapshot) {
                                            if (patientSnapshot.exists()) {
                                                String patientUsername = patientSnapshot.getString("username");

                                                // Create a new authorization document
                                                Map<String, Object> authorization = new HashMap<>();
                                                authorization.put("patientId", currentUserId);
                                                authorization.put("doctorname", docname);
                                                authorization.put("patientUsername", patientUsername); // Optional: Add patient's username if needed
                                                authorization.put("authorizationStatus", "authorized");

                                                // Save the document using the doctor's UID as the document ID
                                                firestore.collection("authorizations")
                                                        .document(doctorUid)  // Use the doctor's UID as the document ID
                                                        .set(authorization)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(TreatmentsP.this, "Authorization successful", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(TreatmentsP.this, "Authorization failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(TreatmentsP.this, "Patient username not found", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(TreatmentsP.this, "Failed to retrieve patient username", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(TreatmentsP.this, "Doctor not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TreatmentsP.this, "Failed to retrieve doctor information", Toast.LENGTH_SHORT).show();
                    }
                });
    }





    private void fetchAndDisplayTreatmentDetails() {
        // Get the current user's ID
        String userId = auth.getCurrentUser().getUid();

        // Fetch treatment details from Firestore
        firestore.collection("treatments").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Extract data from document and display in EditTexts
                                displayData(document);
                            } else {
                                Toast.makeText(TreatmentsP.this, "No treatment details found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("FirestoreError", "Error fetching treatment details: ", task.getException());
                            Toast.makeText(TreatmentsP.this, "Error fetching treatment details.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displayData(DocumentSnapshot document) {
        // Vitals Information
        etHeight.setText(document.getString("Vitals Information.Height"));
        etWeight.setText(document.getString("Vitals Information.Weight"));
        etBloodPressure.setText(document.getString("Vitals Information.Blood Pressure"));
        etHeartRate.setText(document.getString("Vitals Information.Heart Rate"));
        etBmi.setText(document.getString("Vitals Information.BMI"));

        // Diagnostic Test Results
        etCholesterolLevels.setText(document.getString("Diagnostic Test Results.Cholesterol Levels"));
        etBloodSugarLevels.setText(document.getString("Diagnostic Test Results.Blood Sugar Levels"));
        etBloodTestResults.setText(document.getString("Diagnostic Test Results.Blood Test Results"));
        etUrineTestResults.setText(document.getString("Diagnostic Test Results.Urine Test Results"));
        etImagingResults.setText(document.getString("Diagnostic Test Results.Imaging Results"));
        etOtherDiagnosticTests.setText(document.getString("Diagnostic Test Results.Other Diagnostic Tests"));

        // Treatment Information
        etPrescribedMedications.setText(document.getString("Treatment Information.Prescribed Medications"));
        etTherapies.setText(document.getString("Treatment Information.Therapies"));
        etSurgicalRecommendations.setText(document.getString("Treatment Information.Surgical Recommendations"));
        etFollowUpAppointments.setText(document.getString("Treatment Information.Follow Up Appointments"));
    }
}
