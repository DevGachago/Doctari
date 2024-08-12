package com.example.doctari;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doctari.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;


public class PhoneNumberActivity extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(PhoneNumberActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //Objects.requireNonNull(getSupportActionBar()).hide();

        binding.phoneBox.requestFocus();

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.phoneBox.getText().toString().trim();
                if (!phone.isEmpty()) {
                    Intent intent = new Intent(PhoneNumberActivity.this, OtpActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                } else {
                    // Show an error message or toast indicating that the phone number is empty
                    Toast.makeText(PhoneNumberActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}