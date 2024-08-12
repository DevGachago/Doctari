package com.example.doctari.dashboardUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doctari.R;
import com.example.doctari.adapters.AuthorizationAdapter;
import com.example.doctari.adapters.PatientAuthAdapter;
import com.example.doctari.models.Authorization;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Treatments extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PatientAuthAdapter adapter;
    private final List<Authorization> authorizationList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatments);

        recyclerView = findViewById(R.id.recyclerViewAuthorizations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PatientAuthAdapter(authorizationList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        updatePatientData();
    }

    private void updatePatientData() {
        String patientUid = auth.getCurrentUser().getUid(); // Get the current UID of the patient

        // Query the 'authorizations' collection to check if there's an authorization for this patient
        firestore.collection("authorizations")
                .whereEqualTo("patientId", patientUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                // Retrieve the patientId, patientUsername, and authorizationStatus from the document
                                String patientId = documentSnapshot.getString("patientId");
                                String patientName = documentSnapshot.getString("doctorname");
                                String status = documentSnapshot.getString("authorizationStatus");

                                // Create an Authorization object and add it to the list
                                Authorization authorization = new Authorization(patientId, patientName, status);
                                authorizationList.add(authorization);
                            }

                            // Notify the adapter to update the RecyclerView
                            adapter.notifyDataSetChanged();
                        } else {
                            // If no documents are found for the given patientUid
                            Toast.makeText(Treatments.this, "No authorizations found for patient with UID: " + patientUid, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occur during the retrieval process
                        Toast.makeText(Treatments.this, "Failed to retrieve authorizations", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}