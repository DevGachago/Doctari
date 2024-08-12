package com.example.doctari;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.doctari.adapters.DoctorAppointmentsAdapter;
import com.example.doctari.models.Appointments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorsAppointmentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DoctorAppointmentsAdapter adapter;
    private final List<Appointments> appointmentList = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_appointment);

        recyclerView = findViewById(R.id.recyclerViewAppointments);


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorAppointmentsAdapter(this, appointmentList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAppointments();
            }
        });

        fetchAppointments();

    }

    private void fetchAppointments() {
        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to Firestore collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference appointmentsRef = db.collection("appointments");

        // Query for appointments where docId matches the current userId
        Query query = appointmentsRef.whereEqualTo("docId", userId);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            appointmentList.clear();
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Appointments appointment = documentSnapshot.toObject(Appointments.class);
                    appointmentList.add(appointment);
                }
            }

            // Hide the refresh indicator
            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();

        }).addOnFailureListener(e -> {
            // Handle errors
            Log.e("FirestoreError", "Error fetching appointments", e);
            // Hide the refresh indicator in case of error
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}
