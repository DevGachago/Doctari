package com.example.doctari.dashboardUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctari.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DocActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private TextView textView;
    private EditText etHeight, etWeight, etBloodPressure, etHeartRate, etBmi, etCholesterolLevels,
            etBloodSugarLevels, etBloodTestResults, etUrineTestResults, etImagingResults,
            etOtherDiagnosticTests, etPrescribedMedications, etTherapies, etSurgicalRecommendations,
            etFollowUpAppointments, username;
    private DocumentSnapshot document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

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
        username = findViewById(R.id.usernameA);
        textView = findViewById(R.id.refreshbtn);

        Button saveButton = findViewById(R.id.submit_button);
        saveButton.setOnClickListener(v -> saveTreatmentData());

        // Retrieve the data passed in the Intent
        Intent intent = getIntent();
        String patientUid = intent.getStringExtra("patientUid");

        textView.setOnClickListener(v ->retrieveMedicalFile(patientUid));

        retrievePatientUsername(patientUid);
        retrieveMedicalFile(patientUid);

    }

    private void retrievePatientUsername(String patientUid) {


        // Retrieve the patient's username using the patientUid
        firestore.collection("users").document(patientUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot patientSnapshot) {
                        if (patientSnapshot.exists()) {
                            String patientUsername = patientSnapshot.getString("username");

                            // Populate the TextView with the patient's username

                            username.setText(patientUsername);

                        } else {
                            Toast.makeText(DocActivity.this, "Patient username not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DocActivity.this, "Failed to retrieve patient's username", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void retrieveMedicalFile(String patientUid) {
        // Retrieve the authorization status from the Intent
        Intent intent = getIntent();
        String authorizationStatus = intent.getStringExtra("status");

        // Check if the status is "authorized"
        if ("authorized".equals(authorizationStatus)) {
            // If authorized, fetch and display the treatments
            firestore.collection("treatments").document(patientUid).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Extract data from document and display in EditTexts
                                    displayData(document);
                                } else {
                                    Toast.makeText(DocActivity.this, "No treatment details found.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(DocActivity.this, "Error fetching treatment details.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // If not authorized, display a message
            Toast.makeText(DocActivity.this, "You are not authorized to view this patient's treatments.", Toast.LENGTH_SHORT).show();
        }
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

    private void saveTreatmentData() {

        Intent intent = getIntent();
        String patientUid = intent.getStringExtra("patientUid");
        // Get the current user's ID
        //String userId = auth.getCurrentUser().getUid();

        // Retrieve input values
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String bloodPressure = etBloodPressure.getText().toString().trim();
        String heartRate = etHeartRate.getText().toString().trim();
        String bmi = etBmi.getText().toString().trim();
        String cholesterolLevels = etCholesterolLevels.getText().toString().trim();
        String bloodSugarLevels = etBloodSugarLevels.getText().toString().trim();
        String bloodTestResults = etBloodTestResults.getText().toString().trim();
        String urineTestResults = etUrineTestResults.getText().toString().trim();
        String imagingResults = etImagingResults.getText().toString().trim();
        String otherDiagnosticTests = etOtherDiagnosticTests.getText().toString().trim();
        String prescribedMedications = etPrescribedMedications.getText().toString().trim();
        String therapies = etTherapies.getText().toString().trim();
        String surgicalRecommendations = etSurgicalRecommendations.getText().toString().trim();
        String followUpAppointments = etFollowUpAppointments.getText().toString().trim();


        // Check for empty fields and display a message if any are empty
        if (height.isEmpty() || weight.isEmpty() || bloodPressure.isEmpty() || heartRate.isEmpty() || bmi.isEmpty() ||
                cholesterolLevels.isEmpty() || bloodSugarLevels.isEmpty() || bloodTestResults.isEmpty() || urineTestResults.isEmpty() ||
                imagingResults.isEmpty() || otherDiagnosticTests.isEmpty() || prescribedMedications.isEmpty() || therapies.isEmpty() ||
                surgicalRecommendations.isEmpty() || followUpAppointments.isEmpty()) {
            Toast.makeText(DocActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return; // Exit the method early
        }



        // Create Maps to hold the data
        Map<String, Object> treatmentData = new HashMap<>();

        // Vitals Information
        Map<String, Object> vitalsInformation = new HashMap<>();
        vitalsInformation.put("Height", height);
        vitalsInformation.put("Weight", weight);
        vitalsInformation.put("Blood Pressure", bloodPressure);
        vitalsInformation.put("Heart Rate", heartRate);
        vitalsInformation.put("BMI", bmi);

        // Diagnostic Test Results
        Map<String, Object> diagnosticTestResults = new HashMap<>();
        diagnosticTestResults.put("Cholesterol Levels", cholesterolLevels);
        diagnosticTestResults.put("Blood Sugar Levels", bloodSugarLevels);
        diagnosticTestResults.put("Blood Test Results", bloodTestResults);
        diagnosticTestResults.put("Urine Test Results", urineTestResults);
        diagnosticTestResults.put("Imaging Results", imagingResults);
        diagnosticTestResults.put("Other Diagnostic Tests", otherDiagnosticTests);

        // Treatment Information
        Map<String, Object> treatmentInformation = new HashMap<>();
        treatmentInformation.put("Prescribed Medications", prescribedMedications);
        treatmentInformation.put("Therapies", therapies);
        treatmentInformation.put("Surgical Recommendations", surgicalRecommendations);
        treatmentInformation.put("Follow Up Appointments", followUpAppointments);

        // Combine all maps into the main treatment data map
        treatmentData.put("Vitals Information", vitalsInformation);
        treatmentData.put("Diagnostic Test Results", diagnosticTestResults);
        treatmentData.put("Treatment Information", treatmentInformation);

        // Save registration details to Firestore under the "treatments" collection
        assert patientUid != null;
        firestore.collection("treatments").document(patientUid).set(treatmentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DocActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    // Optionally clear inputs or navigate to another activity
                    clearInputs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DocActivity.this, "Error saving registration details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputs() {
        etHeight.setText("");
        etWeight.setText("");
        etBloodPressure.setText("");
        etHeartRate.setText("");
        etBmi.setText("");
        etCholesterolLevels.setText("");
        etBloodSugarLevels.setText("");
        etBloodTestResults.setText("");
        etUrineTestResults.setText("");
        etImagingResults.setText("");
        etOtherDiagnosticTests.setText("");
        etPrescribedMedications.setText("");
        etTherapies.setText("");
        etSurgicalRecommendations.setText("");
        etFollowUpAppointments.setText("");
    }

}
