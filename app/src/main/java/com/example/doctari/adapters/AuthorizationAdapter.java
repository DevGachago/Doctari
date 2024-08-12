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

import java.util.List;

public class AuthorizationAdapter extends RecyclerView.Adapter<AuthorizationAdapter.AuthorizationViewHolder> {

    private final List<Authorization> authorizationList;

    // Constructor
    public AuthorizationAdapter(List<Authorization> authorizationList) {
        this.authorizationList = authorizationList;
    }

    @NonNull
    @Override
    public AuthorizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_authorization, parent, false);
        return new AuthorizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorizationViewHolder holder, int position) {
        Authorization authorization = authorizationList.get(position);

        // Bind data to the views
        holder.textViewPatientName.setText(authorization.getPatientName());
        holder.textViewPatientId.setText(authorization.getAuthorizationStatus());

        // Set onClickListener for the "Get" button
        holder.buttonFetchFiles.setOnClickListener(v -> {
            // Create an Intent to start the next activity
            Intent intent = new Intent(holder.itemView.getContext(), DocActivity.class);

            // Pass the patientUid and patientName as extras
            intent.putExtra("patientUid", authorization.getPatientUid());
            intent.putExtra("patientName", authorization.getPatientName());
            intent.putExtra("status", authorization.getAuthorizationStatus());

            // Start the activity
            holder.itemView.getContext().startActivity(intent);
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


