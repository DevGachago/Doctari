package com.example.doctari.dashboardUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doctari.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MedicalFile extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    // EditText fields
    private EditText currentMedicationsEditText;
    private EditText allergiesEditText;
    private EditText pastMedicalConditionsEditText;
    private EditText familyMedicalHistoryEditText;
    private EditText surgeriesHospitalizationsEditText;
    private EditText immunizationRecordsEditText;
    private EditText smokingStatusEditText;
    private EditText alcoholConsumptionEditText;
    private EditText exerciseFrequencyEditText;
    private EditText dietaryPreferencesEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_file);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize EditText fields
        currentMedicationsEditText = findViewById(R.id.current_medications_edittext);
        allergiesEditText = findViewById(R.id.allergies_edittext);
        pastMedicalConditionsEditText = findViewById(R.id.past_medical_conditions_edittext);
        familyMedicalHistoryEditText = findViewById(R.id.family_medical_history_edittext);
        surgeriesHospitalizationsEditText = findViewById(R.id.surgeries_hospitalizations_edittext);
        immunizationRecordsEditText = findViewById(R.id.immunization_records_edittext);
        smokingStatusEditText = findViewById(R.id.smoking_status_edittext);
        alcoholConsumptionEditText = findViewById(R.id.alcohol_consumption_edittext);
        exerciseFrequencyEditText = findViewById(R.id.exercise_frequency_edittext);
        dietaryPreferencesEditText = findViewById(R.id.dietary_preferences_edittext);


        fetchAndDisplayRegistrationDetails();

    }

    private void fetchAndDisplayRegistrationDetails() {
        // Get the current user's ID
        String userId = auth.getCurrentUser().getUid();

        // Fetch registration details from Firestore
        firestore.collection("registration_details").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Extract data from document and display in EditTexts
                                displayData(document);
                            } else {
                                Toast.makeText(MedicalFile.this, "No registration details found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("FirestoreError", "Error fetching registration details: ", task.getException());
                            Toast.makeText(MedicalFile.this, "Error fetching registration details.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displayData(DocumentSnapshot document) {
        // Medical History
        currentMedicationsEditText.setText(document.getString("Medical History.Current Medications"));
        allergiesEditText.setText(document.getString("Medical History.Allergies"));
        pastMedicalConditionsEditText.setText(document.getString("Medical History.Past Medical Conditions"));
        familyMedicalHistoryEditText.setText(document.getString("Medical History.Family Medical History"));
        surgeriesHospitalizationsEditText.setText(document.getString("Medical History.Surgeries and Hospitalizations"));
        immunizationRecordsEditText.setText(document.getString("Medical History.Immunization Records"));

        // Lifestyle Information
        smokingStatusEditText.setText(document.getString("Lifestyle Information.Smoking Status"));
        alcoholConsumptionEditText.setText(document.getString("Lifestyle Information.Alcohol Consumption"));
        exerciseFrequencyEditText.setText(document.getString("Lifestyle Information.Exercise Frequency"));
        dietaryPreferencesEditText.setText(document.getString("Lifestyle Information.Dietary Preferences"));
    }
}