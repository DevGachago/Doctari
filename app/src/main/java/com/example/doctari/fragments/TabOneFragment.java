package com.example.doctari.fragments;

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

import com.example.doctari.DoctorDetailActivity;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class TabOneFragment extends Fragment implements SpecialistAdapter.SpecialistItemClickListener {

    private SpecialistAdapter specialistAdapter;
    private List<Specialist> specList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore firestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_five, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTreat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSpecialistItems();
            }
        });

        specList = new ArrayList<>();
        specialistAdapter = new SpecialistAdapter(getContext(), specList, this);
        recyclerView.setAdapter(specialistAdapter);

        firestore = FirebaseFirestore.getInstance();
        loadSpecialistItems();

        return view;
    }

    public List<String> getDataItems() {
        // Return the list of data items for this fragment
        List<String> dataItems = new ArrayList<>();
        // Add data items to the list
        return dataItems;
    }

    private void loadSpecialistItems() {
        Query query = firestore.collection("specialists").whereEqualTo("category", "Doctor");

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                specList.clear();
                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Get the specialist data
                        Specialist specItem = document.toObject(Specialist.class);

                        // Add the verification status to the specialist object
                        if (specItem != null) {
                            specItem.setVerified(document.getBoolean("isVerified"));
                            // Add the specialist to the list
                            specList.add(specItem);
                        }
                    }

                    // Hide the refresh indicator
                    swipeRefreshLayout.setRefreshing(false);

                    // Notify the adapter that the data has changed
                    specialistAdapter.notifyDataSetChanged();
                }
            } else {
                Log.e("TabFiveFragment", "Error retrieving specialist items: ", task.getException());

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
