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

public class MyDetails extends AppCompatActivity {

    private EditText fullName, dateOfBirth, gender, phoneNumber, emailAddress, emergencyContact;
    private EditText homeAddress, city, stateProvince, country, postalCode;
    private EditText nationalId, insuranceProvider, policyNumber;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_details);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        fullName = findViewById(R.id.full_name_edittext);
        dateOfBirth = findViewById(R.id.date_of_birth_edittext);
        gender = findViewById(R.id.gender_edittext);
        phoneNumber = findViewById(R.id.phone_number_edittext);
        emailAddress = findViewById(R.id.email_address_edittext);
        emergencyContact = findViewById(R.id.emergency_contact_edittext);

        homeAddress = findViewById(R.id.home_address_edittext);
        city = findViewById(R.id.city_edittext);
        stateProvince = findViewById(R.id.state_province_edittext);
        country = findViewById(R.id.country_edittext);
        postalCode = findViewById(R.id.postal_code_edittext);

        nationalId = findViewById(R.id.national_id_edittext);
        insuranceProvider = findViewById(R.id.insurance_provider_edittext);
        policyNumber = findViewById(R.id.policy_number_edittext);

        // Fetch and display registration details
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
                                Toast.makeText(MyDetails.this, "No registration details found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("FirestoreError", "Error fetching registration details: ", task.getException());
                            Toast.makeText(MyDetails.this, "Error fetching registration details.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displayData(DocumentSnapshot document) {
        // Personal Information
        fullName.setText(document.getString("Personal Information.Full Name"));
        dateOfBirth.setText(document.getString("Personal Information.Date of Birth"));
        gender.setText(document.getString("Personal Information.Gender"));
        phoneNumber.setText(document.getString("Personal Information.Phone Number"));
        emailAddress.setText(document.getString("Personal Information.Email Address"));
        emergencyContact.setText(document.getString("Personal Information.Emergency Contact"));

        // Address Information
        homeAddress.setText(document.getString("Address Information.Home Address"));
        city.setText(document.getString("Address Information.City"));
        stateProvince.setText(document.getString("Address Information.Province"));
        country.setText(document.getString("Address Information.Country"));
        postalCode.setText(document.getString("Address Information.Postal Code"));

        // Identification
        nationalId.setText(document.getString("Identification.National ID"));
        insuranceProvider.setText(document.getString("Identification.Insurance Provider"));
        policyNumber.setText(document.getString("Identification.Policy Number"));
    }
}