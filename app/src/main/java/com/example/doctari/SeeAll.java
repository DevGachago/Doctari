package com.example.doctari;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.doctari.fragments.AllFragment;

import java.util.Objects;

public class SeeAll extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all);

        textView = findViewById(R.id.pageTitle);

        // Retrieve the data from the Intent
        String name = getIntent().getStringExtra("fragName");

        if (!Objects.equals(name, "Pharmacy")){

            textView.setText(name);
        }else {
            textView.setText(R.string.pharmacies);
        }


        // Create a new instance of AllFragment and pass the data using a Bundle
        AllFragment allFragment = new AllFragment();
        Bundle bundle = new Bundle();
        bundle.putString("fragName",name); // Make sure the key matches
        allFragment.setArguments(bundle);

        // Load the AllFragment when the activity is created
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, allFragment)
                    .commit();
        }
    }
}
