package com.example.doctari;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doctari.models.ChatroomModel;
import com.example.doctari.models.Specialist;
import com.example.doctari.models.UserModel;
import com.example.doctari.utils.AndroidUtil;
import com.example.doctari.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorDetailActivity extends AppCompatActivity {

    private ImageView doctorImage;
    private TextView doctorName;
    private RatingBar doctorRating;
    private TextView doctorSpecialty;
    private TextView doctorQualifications;
    private TextView aboutTherapistDetails;
    private Button buttonBookNow, onlineChat, videoCall;

    private DatabaseReference databaseReference;
    private String doctorId;
    private FirebaseAuth mAuth;
    private ChatroomModel chatroomModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("specialists");

        doctorId = getIntent().getStringExtra("doctorId");
        String profilepic = getIntent().getStringExtra("profilePictureUrl");
        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        String specialization = getIntent().getStringExtra("specialization");
        String location = getIntent().getStringExtra("location");
        String contact = getIntent().getStringExtra("contact");
        String details = getIntent().getStringExtra("details");
        String certification = getIntent().getStringExtra("certification");


        // Initialize views
        doctorImage = findViewById(R.id.imageView);
        doctorName = findViewById(R.id.doctor_name);
        doctorRating = findViewById(R.id.doctor_rating);
        doctorSpecialty = findViewById(R.id.doctor_specialty);
        doctorQualifications = findViewById(R.id.doctor_qualifications);
        aboutTherapistDetails = findViewById(R.id.about_therapist_details);
        buttonBookNow = findViewById(R.id.button_book_now);
        onlineChat = findViewById(R.id.button_online_chat);
        videoCall = findViewById(R.id.button_video_call);

        onlineChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorDetailActivity.this, SearchUserActivity.class);
                intent.putExtra("username", name);
                startActivity(intent);
            }
        });

        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorDetailActivity.this, SearchUserActivity.class);
                intent.putExtra("username", name);
                startActivity(intent);
            }
        });




        buttonBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorDetailActivity.this, BookAppointment.class);
            intent.putExtra("username", name);
            startActivity(intent);
        });

        // Populate data
        doctorName.setText("Dr. " + name);
        doctorSpecialty.setText(specialization);
        doctorQualifications.setText(certification);
        aboutTherapistDetails.setText(details);

        // Load profile picture using Glide
        if (profilepic != null && !profilepic.isEmpty()) {
            Glide.with(this)
                    .load(profilepic)
                    .placeholder(R.drawable.avatar) // Placeholder image
                    .into(doctorImage);
        } else {
            // Set a default placeholder if no profile picture URL is provided
            doctorImage.setImageResource(R.drawable.avatar);
        }

        // Fetch and populate data
        //fetchDoctorData(doctorId);
    }



}
