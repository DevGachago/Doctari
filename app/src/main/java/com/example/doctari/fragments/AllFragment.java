package com.example.doctari.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctari.DoctorDetailActivity;
import com.example.doctari.MainActivity;
import com.example.doctari.R;
import com.example.doctari.adapters.SpecialistAdapter;
import com.example.doctari.models.Specialist;
import com.example.doctari.utils.AndroidUtil;
import com.example.doctari.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AllFragment extends Fragment implements SpecialistAdapter.SpecialistItemClickListener {

    private SpecialistAdapter specialistAdapter;
    private List<Specialist> specList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth mAuth;
    private String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_five, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTreat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        specList = new ArrayList<>();
        specialistAdapter = new SpecialistAdapter(getContext(), specList, this);
        recyclerView.setAdapter(specialistAdapter);

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth

        // Retrieve the data from the arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("fragName");
            Log.d("AllFragment", "Name from Bundle: " + name); // Log the retrieved name

            // Load the specialist items based on the name
            loadSpecialistItems(name);
        } else {
            Log.e("AllFragment", "No arguments received");
        }

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String name = bundle.getString("fragName");
                loadSpecialistItems(name);
            }
        });

        return view;
    }

    private void loadSpecialistItems(String name) {

            DatabaseReference specialistRef = FirebaseDatabase.getInstance().getReference("specialists");

            Query query = specialistRef.orderByChild("category").equalTo(name);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    specList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the specialist data
                        Specialist specItem = snapshot.getValue(Specialist.class);

                        // Add the verification status to the specialist object
                        assert specItem != null;
                        specItem.setVerified((Boolean) snapshot.child("isVerified").getValue());

                        // Add the specialist to the list
                        specList.add(specItem);
                    }

                    // Hide the refresh indicator
                    swipeRefreshLayout.setRefreshing(false);

                    // Notify the adapter that the data has changed
                    specialistAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("TabFiveFragment", "Error retrieving specialist items: " + databaseError.getMessage());

                    // Hide the refresh indicator in case of error
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
    }

    @Override
    public void onSpecialistItemclick(String name, String profilePictureUrl, float rating, String specialization, String certification, String details) {
        // Handle item click: Launch DoctorDetailActivity with specialist data
        Intent intent = new Intent(getActivity(), DoctorDetailActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("profilePictureUrl", profilePictureUrl);
        intent.putExtra("rating", rating);
        intent.putExtra("specialization", specialization);
        intent.putExtra("certification", certification);
        intent.putExtra("details", details);
        startActivity(intent);
    }
}
