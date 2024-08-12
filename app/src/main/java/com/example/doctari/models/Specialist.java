package com.example.doctari.models;

public class Specialist {
    private String id;
    private String name;
    private String category;
    private String specialization;
    private String location;
    private String contact;
    private String details;
    private boolean isVerified;
    private String certification;

    private float rating;
    private String profilePictureUrl;

    // Default constructor required for calls to DataSnapshot.getValue(Specialist.class)
    public Specialist() {
    }

    // Constructor
    public Specialist(String id, String name, String category, String specialization, String location, String contact, String details, boolean isVerified, float rating, String profilePictureUrl, String certification) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.specialization = specialization;
        this.location = location;
        this.contact = contact;
        this.details = details;
        this.isVerified = isVerified;
        this.rating = rating;
        this.profilePictureUrl = profilePictureUrl;
        this.certification = certification;

    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }
}

