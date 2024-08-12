package com.example.doctari.dashboardUI;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.doctari.R;
import com.example.doctari.adapters.AuthorizationAdapter;
import com.example.doctari.models.Authorization;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AuthorizationAdapter adapter;
    private final List<Authorization> authorizationList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        recyclerView = findViewById(R.id.recyclerViewAuthorizations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AuthorizationAdapter(authorizationList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        /*swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updatePatientData(); // Your method to refresh data
            }
        });

         */

        updatePatientData();
    }

    private void updatePatientData() {
        String doctorUid = auth.getCurrentUser().getUid(); // Get the current UID of the doctor

        // Query the 'authorizations' collection using the doctor's UID as the document ID
        firestore.collection("authorizations")
                .document(doctorUid)  // Use doctorUid as the document ID (which is also the authorizationId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve the patientId and doctorname from the document
                            String patientUid = documentSnapshot.getString("patientId");
                            String patientName = documentSnapshot.getString("patientUsername");
                            String status = documentSnapshot.getString("authorizationStatus");

                            // Create an Authorization object and add it to the list
                            Authorization authorization = new Authorization(patientUid, patientName, status);
                            authorizationList.add(authorization);

                            // Notify the adapter to update the RecyclerView
                            //swipeRefreshLayout.setRefreshing(false);
                            adapter.notifyDataSetChanged();
                        } else {
                            // If no document is found for the given doctorUid
                            Toast.makeText(AuthorizationActivity.this, "No authorizations found for doctor with UID: " + doctorUid, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occur during the retrieval process
                        Toast.makeText(AuthorizationActivity.this, "Failed to retrieve authorizations", Toast.LENGTH_SHORT).show();
                        //swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation
                    }
                });
    }


}
