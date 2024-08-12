package com.example.doctari.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctari.R;
import com.example.doctari.models.Appointments;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

    private final List<Appointments> appointmentList;

    public AppointmentsAdapter(List<Appointments> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointments appointment = appointmentList.get(position);
        holder.textViewPatientName.setText(appointment.getPatientName());
        holder.textViewAppointmentDate.setText(appointment.getAppointmentDate());
        holder.textViewAppointmentTime.setText(appointment.getAppointmentTime());
        holder.textViewDoctorName.setText(appointment.getDoctorName());
        holder.textViewPurpose.setText(appointment.getPurpose());
        holder.textViewStatus.setText(appointment.getStatus().toString());
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPatientName;
        TextView textViewAppointmentDate;
        TextView textViewAppointmentTime;
        TextView textViewDoctorName;
        TextView textViewPurpose;
        TextView textViewStatus;

        AppointmentViewHolder(View itemView) {
            super(itemView);
            textViewPatientName = itemView.findViewById(R.id.textViewPatientName);
            textViewAppointmentDate = itemView.findViewById(R.id.textViewAppointmentDate);
            textViewAppointmentTime = itemView.findViewById(R.id.textViewAppointmentTime);
            textViewDoctorName = itemView.findViewById(R.id.textViewDoctorName);
            textViewPurpose = itemView.findViewById(R.id.textViewPurpose);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}

