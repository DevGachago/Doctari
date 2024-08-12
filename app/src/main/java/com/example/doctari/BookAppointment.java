package com.example.doctari;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctari.models.Appointments;
import com.example.doctari.utils.MyFirebaseMessagingService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.UUID;

public class BookAppointment extends AppCompatActivity {

    private EditText time, date, title;
    private TextView patientName,doctorName;
    private Button book;
    private FirebaseFirestore db;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        db = FirebaseFirestore.getInstance();
        // Retrieve the username from the intent
        String username = getIntent().getStringExtra("username");

        patientName = findViewById(R.id.patient_name);
        doctorName = findViewById(R.id.doctor_name);
        time = findViewById(R.id.appointment_time);
        date = findViewById(R.id.appointment_date);
        book = findViewById(R.id.book_appointment_button);
        title = findViewById(R.id.reason);

        doctorName.setText(username);
        book.setOnClickListener(v -> saveAppointment());

        date.setOnClickListener(v -> showDatePickerDialog());
        time.setOnClickListener(v -> showTimePickerDialog());

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String currentUser = document.getString("username");
                    patientName.setText(currentUser);

                }
            }
        });

        findDocUserIdByUsername(username);

    }

    private void findDocUserIdByUsername(String username) {
        // Query the Firestore to find the document with the specified username
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Assuming username is unique, get the first matching document
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String userId = document.getId();
                            doctorId = userId;
                            Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No user found with this username", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to retrieve user ID", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Note: Month is 0-based, so add 1 to it
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    date.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String selectedTime = selectedHour + ":" + selectedMinute;
                    time.setText(selectedTime);
                }, hour, minute, true); // true for 24-hour time format
        timePickerDialog.show();
    }


    private void saveAppointment() {
        // Retrieve the text from the input fields
        String patientNameText = patientName.getText().toString().trim();
        String dateText = date.getText().toString().trim();
        String timeText = time.getText().toString().trim();
        String doctorNameText = doctorName.getText().toString().trim();
        String titleText = title.getText().toString().trim();
        String doctorsId = doctorId;



        // Check if any field is blank
        if (patientNameText.isEmpty() || dateText.isEmpty() || timeText.isEmpty() || doctorNameText.isEmpty() || titleText.isEmpty()) {
            Toast.makeText(BookAppointment.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return; // Stop execution if any field is blank
        }

        // Get the current user's ID as the ownerId
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Generate a unique appointment ID using UUID
        String appointmentId = UUID.randomUUID().toString();

        // Retrieve the username from the intent
        String username = getIntent().getStringExtra("username");
        // Query the Firestore to find the document with the specified username
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Assuming username is unique, get the first matching document
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                             String docsId = document.getId();

                            Toast.makeText(this, "User ID: " + docsId, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No user found with this username", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to retrieve user ID", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });



        // Create an Appointments object with the provided details
        Appointments appointments = new Appointments(
                appointmentId,
                patientNameText,
                dateText,
                timeText,
                doctorNameText,
                titleText,
                Appointments.Status.WAITING,
                ownerId, // Set the ownerId field
                doctorsId
        );

        // Save the appointment object to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments").document(appointmentId).set(appointments)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Show success message
                        Toast.makeText(BookAppointment.this, "Appointment Booked!", Toast.LENGTH_SHORT).show();

                        // Send a notification
                        String msg = "Appointment Booked with Doctor " + doctorNameText;
                        MyFirebaseMessagingService.sendNotification(BookAppointment.this, msg);

                        // Clear input fields after successful booking
                        clearFields();
                    } else {
                        // Show error message
                        Toast.makeText(BookAppointment.this, "Error booking appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void clearFields() {
        patientName.setText("");
        date.setText("");
        time.setText("");
        doctorName.setText("");
        title.setText("");
    }



}