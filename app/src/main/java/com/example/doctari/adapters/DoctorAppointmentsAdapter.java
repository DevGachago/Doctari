package com.example.doctari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctari.R;
import com.example.doctari.models.Appointments;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DoctorAppointmentsAdapter extends RecyclerView.Adapter<DoctorAppointmentsAdapter.DoctorAppointmentViewHolder> {

    private final List<Appointments> appointmentList;
    private final Context context;

    public DoctorAppointmentsAdapter(Context context, List<Appointments> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public DoctorAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_doctor, parent, false);
        return new DoctorAppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorAppointmentViewHolder holder, int position) {
        Appointments appointment = appointmentList.get(position);

        // Bind data to the ViewHolder's TextViews
        holder.textViewAppointmentDate.setText(appointment.getAppointmentDate());
        holder.textViewAppointmentTime.setText(appointment.getAppointmentTime());
        holder.textViewPatientName.setText(appointment.getPatientName());
        holder.textViewPurpose.setText(appointment.getPurpose());
        holder.textViewStatus.setText(appointment.getStatus().toString());

        // Setup the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.appointment_status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerStatus.setAdapter(adapter);

        // Set the spinner to the current status
        holder.spinnerStatus.setSelection(appointment.getStatus().ordinal());

        // Set the spinner listener
        holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update the appointment status based on spinner selection
                Appointments.Status newStatus = Appointments.Status.values()[position];
                if (newStatus != appointment.getStatus()) {
                    appointment.setStatus(newStatus);
                    updateAppointmentStatus(appointment);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    private void updateAppointmentStatus(Appointments appointment) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments").document(appointment.getId())
                .update("status", appointment.getStatus().toString())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Status updated to " + appointment.getStatus().toString(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }

    static class DoctorAppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAppointmentDate;
        TextView textViewAppointmentTime;
        TextView textViewPatientName;
        TextView textViewPurpose;
        TextView textViewStatus;
        Spinner spinnerStatus;

        DoctorAppointmentViewHolder(View itemView) {
            super(itemView);

            // Initialize the TextViews and Spinner with their respective IDs from item_appointment_doctor.xml
            textViewAppointmentDate = itemView.findViewById(R.id.textViewAppointmentDate);
            textViewAppointmentTime = itemView.findViewById(R.id.textViewAppointmentTime);
            textViewPatientName = itemView.findViewById(R.id.textViewPatientName);
            textViewPurpose = itemView.findViewById(R.id.textViewPurpose);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
        }
    }
}

