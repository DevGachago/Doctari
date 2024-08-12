package com.example.doctari.models;

public class Authorization {
    private String patientUid;
    private String patientName;
    private String authorizationStatus;

    public Authorization() {
        // Default constructor required for calls to DataSnapshot.getValue(Authorization.class)
    }

    public Authorization(String patientUid, String patientName, String authorizationStatus) {
        this.patientUid = patientUid;
        this.patientName = patientName;
        this.authorizationStatus = authorizationStatus;
    }

    public String getPatientUid() {
        return patientUid;
    }

    public void setPatientUid(String patientUid) {
        this.patientUid = patientUid;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAuthorizationStatus() {
        return authorizationStatus;
    }

    public void setAuthorizationStatus(String authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }
}

