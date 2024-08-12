package com.example.doctari;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctari.adapters.AppointmentsAdapter;
import com.example.doctari.models.Appointments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAppointments;
    private AppointmentsAdapter adapter;
    private List<Appointments> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments);
        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(this));

        appointmentList = new ArrayList<>();
        adapter = new AppointmentsAdapter(appointmentList);
        recyclerViewAppointments.setAdapter(adapter);

        fetchAppointments();
    }

    private void fetchAppointments() {
        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to Firestore collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference appointmentsRef = db.collection("appointments");

        // Query for appointments where ownerId matches the current userId
        Query query = appointmentsRef.whereEqualTo("ownerId", userId);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            appointmentList.clear();
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Appointments appointment = documentSnapshot.toObject(Appointments.class);
                    appointmentList.add(appointment);
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Handle errors
            Log.e("FirestoreError", "Error fetching appointments", e);
        });
    }
}

