package com.example.doctari.models;


import java.io.Serializable;

public class Appointments implements Serializable {




    public enum Status {
        WAITING,
        ACCEPTED,
        REJECTED,
        MISSED,
        RESCHEDULED,
        COMPLETED
    }
    private String id;
    private String patientName;
    private String appointmentDate;
    private String appointmentTime;
    private String doctorName;
    private String purpose;
    private Status status;
    private String ownerId; // New field for ownerId
    private String docId;

    // Default constructor
    public Appointments() {}

    // Parameterized constructor
    public Appointments(String id, String patientName, String appointmentDate, String appointmentTime, String doctorName, String purpose, Status status, String ownerId, String docId) {
        this.id = id;
        this.patientName = patientName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.doctorName = doctorName;
        this.purpose = purpose;
        this.status = status;
        this.ownerId = ownerId;
        this.docId = docId;
    }

    // Getters and setters

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
