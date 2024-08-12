package com.example.doctari.dashboardUI;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doctari.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

public class Preference extends AppCompatActivity {

    private static final String TAG = "PreferencesActivity";
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    // EditText fields
    private EditText preferredLanguageEditText;
    private EditText preferredCommunicationEditText;
    private EditText accessibilityNeedsEditText;
    private EditText legalGuardianEditText;
    private EditText mentalHealthStatusEditText;
    private EditText psychologicalAssessmentsEditText;
    private EditText primaryCarePhysicianEditText;
    private EditText specialistPhysiciansEditText;

    // CheckBox fields
    private CheckBox consentTelehealthCheckBox;
    private CheckBox consentDataSharingCheckBox;
    private CheckBox photographCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        // Initialize Firestore
        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize EditTexts and CheckBoxes
        preferredLanguageEditText = findViewById(R.id.preferred_language_edittext);
        preferredCommunicationEditText = findViewById(R.id.preferred_communication_edittext);
        accessibilityNeedsEditText = findViewById(R.id.accessibility_needs_edittext);
        legalGuardianEditText = findViewById(R.id.legal_guardian_edittext);
        mentalHealthStatusEditText = findViewById(R.id.mental_health_status_edittext);
        psychologicalAssessmentsEditText = findViewById(R.id.psychological_assessments_edittext);
        primaryCarePhysicianEditText = findViewById(R.id.primary_care_physician_edittext);
        specialistPhysiciansEditText = findViewById(R.id.specialist_physicians_edittext);

        consentTelehealthCheckBox = findViewById(R.id.consent_telehealth_checkbox);
        consentDataSharingCheckBox = findViewById(R.id.consent_data_sharing_checkbox);
        photographCheckBox = findViewById(R.id.photograph_checkbox);

        // Fetch and display preferences data
        fetchPreferencesData();
    }

    private void fetchPreferencesData() {
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
                                displayPreferencesData(document);
                            } else {
                                Toast.makeText(Preference.this, "No registration details found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("FirestoreError", "Error fetching registration details: ", task.getException());
                            Toast.makeText(Preference.this, "Error fetching registration details.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displayPreferencesData(DocumentSnapshot document) {
        // Use FieldPath.of() for fields with special characters
        // Use dot notation for regular fields

        // Preferences and Accessibility
        String preferredLanguage = document.getString("Preferences and Accessibility.Preferred Language");
        String preferredCommunicationMethod = document.getString("Preferences and Accessibility.Preferred Communication Method");
        String accessibilityNeeds = document.getString("Preferences and Accessibility.Accessibility Needs");

        // Consent and Legal
        Boolean consentTelehealth = document.getBoolean("Consent and Legal.Consent for Telehealth Services");
        Boolean consentDataSharing = document.getBoolean("Consent and Legal.Consent for Data Sharing");
        String legalGuardian = document.getString("Consent and Legal.Legal Guardian");

        // Psychological Health
        String mentalHealthStatus = document.getString("Psychological Health.Mental Health Status");
        String psychologicalAssessments = document.getString("Psychological Health.Psychological Assessments");

        // Additional Information
        Boolean photographAttached = document.getBoolean("Additional Information.Photograph Attached");
        String primaryCarePhysician = document.getString("Additional Information.Primary Care Physician");
        String specialistPhysicians = document.getString("Additional Information.Specialist Physicians");

        // Set values to EditTexts and CheckBoxes
        preferredLanguageEditText.setText(preferredLanguage != null ? preferredLanguage : "");
        preferredCommunicationEditText.setText(preferredCommunicationMethod != null ? preferredCommunicationMethod : "");
        accessibilityNeedsEditText.setText(accessibilityNeeds != null ? accessibilityNeeds : "");
        legalGuardianEditText.setText(legalGuardian != null ? legalGuardian : "");
        mentalHealthStatusEditText.setText(mentalHealthStatus != null ? mentalHealthStatus : "");
        psychologicalAssessmentsEditText.setText(psychologicalAssessments != null ? psychologicalAssessments : "");
        primaryCarePhysicianEditText.setText(primaryCarePhysician != null ? primaryCarePhysician : "");
        specialistPhysiciansEditText.setText(specialistPhysicians != null ? specialistPhysicians : "");

        // Set CheckBox values
        consentTelehealthCheckBox.setChecked(consentTelehealth != null ? consentTelehealth : false);
        consentDataSharingCheckBox.setChecked(consentDataSharing != null ? consentDataSharing : false);
        photographCheckBox.setChecked(photographAttached != null ? photographAttached : false);
    }
}
