package com.example.doctari.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctari.R;
import com.example.doctari.dashboardUI.DocActivity;
import com.example.doctari.models.Authorization;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PatientAuthAdapter extends RecyclerView.Adapter<PatientAuthAdapter.AuthorizationViewHolder> {

    private final List<Authorization> authorizationList;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // Constructor
    public PatientAuthAdapter(List<Authorization> authorizationList) {
        this.authorizationList = authorizationList;
    }

    @NonNull
    @Override
    public AuthorizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patientauth, parent, false);
        return new AuthorizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorizationViewHolder holder, int position) {
        Authorization authorization = authorizationList.get(position);

        // Bind data to the views
        holder.textViewPatientName.setText(authorization.getPatientName());
        holder.textViewPatientId.setText(authorization.getAuthorizationStatus());

        // Set onClickListener for the status toggle
        holder.buttonFetchFiles.setOnClickListener(v -> {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            String patientUid = authorization.getPatientUid(); // Use patientUid to query documents

            firestore.collection("authorizations")
                    .whereEqualTo("patientId", patientUid) // Query where patientId matches the current user's ID
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                // Retrieve the current status
                                String currentStatus = documentSnapshot.getString("authorizationStatus");

                                // Toggle the status
                                String newStatus = "authorized".equals(currentStatus) ? "not authorized" : "authorized";

                                // Update the document with the new status
                                firestore.collection("authorizations")
                                        .document(documentSnapshot.getId()) // Use the document ID from the query result
                                        .update("authorizationStatus", newStatus)
                                        .addOnSuccessListener(aVoid -> {
                                            // Update the local list and notify the adapter
                                            authorization.setAuthorizationStatus(newStatus);
                                            notifyItemChanged(position);
                                            Toast.makeText(holder.itemView.getContext(), "Authorization status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(holder.itemView.getContext(), "Failed to update authorization status", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "No document found for the specified patient UID", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(holder.itemView.getContext(), "Failed to retrieve documents", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    @Override
    public int getItemCount() {
        return authorizationList.size();
    }

    public static class AuthorizationViewHolder extends RecyclerView.ViewHolder {

        TextView textViewPatientName, textViewPatientId;
        Button buttonFetchFiles;

        public AuthorizationViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewPatientName = itemView.findViewById(R.id.textViewPatientName);
            textViewPatientId = itemView.findViewById(R.id.textViewPatientId);
            buttonFetchFiles = itemView.findViewById(R.id.buttonFetchFiles);
        }
    }
}


