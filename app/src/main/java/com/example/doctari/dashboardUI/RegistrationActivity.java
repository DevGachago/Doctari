package com.example.doctari.dashboardUI;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doctari.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText fullName, dateOfBirth, phoneNumber, emailAddress, emergencyContact;
    private RadioGroup genderGroup;
    private EditText homeAddress, city, stateProvince, country, postalCode;
    private EditText nationalId, insuranceProvider, policyNumber;
    private EditText currentMedications, allergies, pastConditions, familyHistory, surgeries, immunizationRecords;
    private EditText smokingStatus, alcoholConsumption, exerciseFrequency, dietaryPreferences;
    private EditText preferredLanguage, communicationMethod, accessibilityNeeds;
    private CheckBox consentTelehealth, consentDataSharing;
    private Button submitButton;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        fullName = findViewById(R.id.full_name);
        dateOfBirth = findViewById(R.id.date_of_birth);
        genderGroup = findViewById(R.id.gender);
        phoneNumber = findViewById(R.id.phone_number);
        emailAddress = findViewById(R.id.email_address);
        emergencyContact = findViewById(R.id.emergency_contact);

        homeAddress = findViewById(R.id.home_address);
        city = findViewById(R.id.city);
        stateProvince = findViewById(R.id.state_province);
        country = findViewById(R.id.country);
        postalCode = findViewById(R.id.postal_code);

        nationalId = findViewById(R.id.national_id);
        insuranceProvider = findViewById(R.id.insurance_provider);
        policyNumber = findViewById(R.id.policy_number);

        currentMedications = findViewById(R.id.current_medications);
        allergies = findViewById(R.id.allergies);
        pastConditions = findViewById(R.id.past_conditions);
        familyHistory = findViewById(R.id.family_history);
        surgeries = findViewById(R.id.surgeries);
        immunizationRecords = findViewById(R.id.immunization_records);

        smokingStatus = findViewById(R.id.smoking_status);
        alcoholConsumption = findViewById(R.id.alcohol_consumption);
        exerciseFrequency = findViewById(R.id.exercise_frequency);
        dietaryPreferences = findViewById(R.id.dietary_preferences);

        preferredLanguage = findViewById(R.id.preferred_language);
        communicationMethod = findViewById(R.id.communication_method);
        accessibilityNeeds = findViewById(R.id.accessibility_needs);

        consentTelehealth = findViewById(R.id.consent_telehealth);
        consentDataSharing = findViewById(R.id.consent_data_sharing);

        submitButton = findViewById(R.id.submit_button);

        // Set submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRegistrationDetails();
            }
        });
    }

    private void saveRegistrationDetails() {
        // Get data from input fields
        String fullNameText = fullName.getText().toString().trim();
        String dateOfBirthText = dateOfBirth.getText().toString().trim();
        String gender = getSelectedGender();
        String phoneNumberText = phoneNumber.getText().toString().trim();
        String emailAddressText = emailAddress.getText().toString().trim();
        String emergencyContactText = emergencyContact.getText().toString().trim();

        String homeAddressText = homeAddress.getText().toString().trim();
        String cityText = city.getText().toString().trim();
        String stateProvinceText = stateProvince.getText().toString().trim();
        String countryText = country.getText().toString().trim();
        String postalCodeText = postalCode.getText().toString().trim();

        String nationalIdText = nationalId.getText().toString().trim();
        String insuranceProviderText = insuranceProvider.getText().toString().trim();
        String policyNumberText = policyNumber.getText().toString().trim();

        String currentMedicationsText = currentMedications.getText().toString().trim();
        String allergiesText = allergies.getText().toString().trim();
        String pastConditionsText = pastConditions.getText().toString().trim();
        String familyHistoryText = familyHistory.getText().toString().trim();
        String surgeriesText = surgeries.getText().toString().trim();
        String immunizationRecordsText = immunizationRecords.getText().toString().trim();

        String smokingStatusText = smokingStatus.getText().toString().trim();
        String alcoholConsumptionText = alcoholConsumption.getText().toString().trim();
        String exerciseFrequencyText = exerciseFrequency.getText().toString().trim();
        String dietaryPreferencesText = dietaryPreferences.getText().toString().trim();

        String preferredLanguageText = preferredLanguage.getText().toString().trim();
        String communicationMethodText = communicationMethod.getText().toString().trim();
        String accessibilityNeedsText = accessibilityNeeds.getText().toString().trim();

        boolean consentTelehealthChecked = consentTelehealth.isChecked();
        boolean consentDataSharingChecked = consentDataSharing.isChecked();

        // Validate inputs (you can add more validation as needed)
        if (TextUtils.isEmpty(fullNameText) || TextUtils.isEmpty(dateOfBirthText) || TextUtils.isEmpty(gender) ||
                TextUtils.isEmpty(phoneNumberText) || TextUtils.isEmpty(emailAddressText)) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to organize data under subheadings
        Map<String, Object> registrationData = new HashMap<>();

        // Personal Information
        Map<String, Object> personalInformation = new HashMap<>();
        personalInformation.put("Full Name", fullNameText);
        personalInformation.put("Date of Birth", dateOfBirthText);
        personalInformation.put("Gender", gender);
        personalInformation.put("Phone Number", phoneNumberText);
        personalInformation.put("Email Address", emailAddressText);
        personalInformation.put("Emergency Contact", emergencyContactText);

        // Address Information
        Map<String, Object> addressInformation = new HashMap<>();
        addressInformation.put("Home Address", homeAddressText);
        addressInformation.put("City", cityText);
        addressInformation.put("Province", stateProvinceText);
        addressInformation.put("Country", countryText);
        addressInformation.put("Postal Code", postalCodeText);

        // Identification
        Map<String, Object> identification = new HashMap<>();
        identification.put("National ID", nationalIdText);
        identification.put("Insurance Provider", insuranceProviderText);
        identification.put("Policy Number", policyNumberText);

        // Medical History
        Map<String, Object> medicalHistory = new HashMap<>();
        medicalHistory.put("Current Medications", currentMedicationsText);
        medicalHistory.put("Allergies", allergiesText);
        medicalHistory.put("Past Medical Conditions", pastConditionsText);
        medicalHistory.put("Family Medical History", familyHistoryText);
        medicalHistory.put("Surgeries and Hospitalizations", surgeriesText);
        medicalHistory.put("Immunization Records", immunizationRecordsText);

        // Lifestyle Information
        Map<String, Object> lifestyleInformation = new HashMap<>();
        lifestyleInformation.put("Smoking Status", smokingStatusText);
        lifestyleInformation.put("Alcohol Consumption", alcoholConsumptionText);
        lifestyleInformation.put("Exercise Frequency", exerciseFrequencyText);
        lifestyleInformation.put("Dietary Preferences", dietaryPreferencesText);

        // Preferences and Accessibility
        Map<String, Object> preferencesAccessibility = new HashMap<>();
        preferencesAccessibility.put("Preferred Language", preferredLanguageText);
        preferencesAccessibility.put("Preferred Communication Method", communicationMethodText);
        preferencesAccessibility.put("Accessibility Needs", accessibilityNeedsText);

        // Consent and Legal
        Map<String, Object> consentLegal = new HashMap<>();
        consentLegal.put("Consent for Telehealth Services", consentTelehealthChecked);
        consentLegal.put("Consent for Data Sharing", consentDataSharingChecked);

        // Add each subheading map to the main registrationData map
        registrationData.put("Personal Information", personalInformation);
        registrationData.put("Address Information", addressInformation);
        registrationData.put("Identification", identification);
        registrationData.put("Medical History", medicalHistory);
        registrationData.put("Lifestyle Information", lifestyleInformation);
        registrationData.put("Preferences and Accessibility", preferencesAccessibility);
        registrationData.put("Consent and Legal", consentLegal);

        // Get the current user's ID
        String userId = auth.getCurrentUser().getUid();

        // Save registration details to Firestore under the "registration_details" collection
        firestore.collection("registration_details").document(userId).set(registrationData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    // Optionally clear inputs or navigate to another activity
                    clearInputs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistrationActivity.this, "Error saving registration details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getSelectedGender() {
        int selectedId = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedGenderButton = findViewById(selectedId);
        return selectedGenderButton != null ? selectedGenderButton.getText().toString() : "";
    }

    private void clearInputs() {
        fullName.setText("");
        dateOfBirth.setText("");
        genderGroup.clearCheck();
        phoneNumber.setText("");
        emailAddress.setText("");
        emergencyContact.setText("");

        homeAddress.setText("");
        city.setText("");
        stateProvince.setText("");
        country.setText("");
        postalCode.setText("");

        nationalId.setText("");
        insuranceProvider.setText("");
        policyNumber.setText("");

        currentMedications.setText("");
        allergies.setText("");
        pastConditions.setText("");
        familyHistory.setText("");
        surgeries.setText("");
        immunizationRecords.setText("");

        smokingStatus.setText("");
        alcoholConsumption.setText("");
        exerciseFrequency.setText("");
        dietaryPreferences.setText("");

        preferredLanguage.setText("");
        communicationMethod.setText("");
        accessibilityNeeds.setText("");

        consentTelehealth.setChecked(false);
        consentDataSharing.setChecked(false);
    }
}
