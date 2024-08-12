package com.example.doctari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doctari.adapters.ViewPagerAdapter;
import com.example.doctari.dashboardUI.AuthorizationActivity;
import com.example.doctari.dashboardUI.DocActivity;
import com.example.doctari.dashboardUI.MedicalFile;
import com.example.doctari.dashboardUI.MyDetails;
import com.example.doctari.dashboardUI.Preference;
import com.example.doctari.dashboardUI.RegistrationActivity;
import com.example.doctari.dashboardUI.Treatments;
import com.example.doctari.dashboardUI.TreatmentsP;
import com.example.doctari.fragments.TabFiveFragment;
import com.example.doctari.fragments.TabFourFragment;
import com.example.doctari.fragments.TabOneFragment;
import com.example.doctari.fragments.TabThreeFragment;
import com.example.doctari.fragments.TabTwoFragment;
import com.example.doctari.models.NotificationModel;
import com.example.doctari.models.Specialist;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager2;
    private CircleImageView profilePicture;
    private TextView textView, counter;
    private TextView seeAllButton;
    private ImageView imageView, imageViewTwo;
    private DatabaseReference databaseReference;
    private ValueEventListener notificationListener;
    private String profilePictureUrl;
    //private int selectedItemId = R.id.bottomHome; // Default to the home item

    private LinearLayout linearLayout, linearLayoutR, linearLayoutM, linearLayoutP, linearLayoutMD, linearLayoutT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.bottomHome);
        profilePicture = findViewById(R.id.profpic);
        textView =findViewById(R.id.category);
        seeAllButton = findViewById(R.id.all);
        counter = findViewById(R.id.notification_counter);
        imageView = findViewById(R.id.notification);
        imageViewTwo = findViewById(R.id.imageViewNlC);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        linearLayout = findViewById(R.id.appointmentsLayout);
        linearLayoutR = findViewById(R.id.regLayout);
        linearLayoutM = findViewById(R.id.mydetails);
        linearLayoutP = findViewById(R.id.prefLayout);
        linearLayoutMD = findViewById(R.id.medicLayout);
        linearLayoutT = findViewById(R.id.treatLayout);


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserRoleAndNavigate();
            }
        });

        linearLayoutR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyDetails.class);
                startActivity(intent);
            }
        });

        linearLayoutP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Preference.class);
                startActivity(intent);
            }
        });

        linearLayoutMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MedicalFile.class);
                startActivity(intent);
            }
        });

        linearLayoutT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TreatmentsP.class);
                startActivity(intent);
            }
        });


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                //selectedItemId = id;

                // Handle item clicks based on their IDs
                if (id == R.id.bottomHome) {
                    // Handle clicks on the "Chats" item
                    // Example: Start a new activity for chats
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                    return true;
                } else if (id == R.id.bottomWatchlist) {
                    // Handle clicks on the "Status" item
                    // Example: Start a new activity for status
                    startActivity(new Intent(MainActivity.this, PatientAssesment.class));
                    return true;
                } else if (id == R.id.bottomChats) {
                    // Handle clicks on the "Calls" item
                    // Example: Start a new activity for calls
                    startActivity(new Intent(MainActivity.this, MainChats.class));
                    return true;
                }
                return false;
            }
        });



        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the user is signed in
        if (currentUser != null) {
            // Get the user ID
            String userId = currentUser.getUid();
            setupNotificationListener(userId);

        } else {
            // No user is signed in
        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(intent);

            }
        });

        seeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textView.getText().toString();
                Intent intent = new Intent(MainActivity.this,SeeAll.class);
                intent.putExtra("fragName",name);
                startActivity(intent);
            }
        });

        // Add custom tabs
        for (int i = 0; i < viewPagerAdapter.getItemCount(); i++) {
            // Inflate custom tab item layout
            View customTabView = getLayoutInflater().inflate(R.layout.custom_tab_item, null);

            // Find TextView in custom tab item layout
            TextView tabTextView = customTabView.findViewById(R.id.tab_text);

            // Set text for the TextView based on the names of the tabs
            switch (i) {
                case 0:
                    tabTextView.setText(getString(R.string.doctor));
                    break;
                case 1:
                    tabTextView.setText(getString(R.string.hospital));
                    break;
                case 2:
                    tabTextView.setText(getString(R.string.consultant));
                    break;
                case 3:
                    tabTextView.setText(getString(R.string.chemist));
                    break;
                case 4:
                    tabTextView.setText(getString(R.string.treatments));
                    break;
                default:
                    tabTextView.setText(""); // Handle default case if needed
            }

            // Create a new Tab and set its custom view
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setCustomView(customTabView);

            // Add the tab to the TabLayout
            tabLayout.addTab(tab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Fragment currentFragment = viewPagerAdapter.getFragment(position);
                String fragmentName = getFragmentName(currentFragment);

                if (!Objects.equals(fragmentName, "Pharmacy")){
                    textView.setText(fragmentName);
                }else {
                    textView.setText(R.string.pharmacies);
                }

                tabLayout.getTabAt(position).select();
            }
        });

        profilePicture = findViewById(R.id.profpic);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePicOptions();
            }
        });

        // Assuming you have a reference to the root view of your activity layout
        View rootView = getWindow().getDecorView().getRootView();

        // Call the hideBottomNavigation() method and pass the root view
        hideBottomNavigation(rootView);

        // Set a touch listener to reapply immersive mode when touched
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Reapply immersive mode when touched
                hideBottomNavigation(rootView);
                return false;
            }
        });

        // Load and display the profile picture
        fetchUserProfile();

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        // Set the selected item in the BottomNavigationView when the user returns to this activity
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }

     */

    private void checkUserRoleAndNavigate() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = firestore.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String role = document.getString("role");
                    if ("specialist".equals(role)) {
                        Intent intent = new Intent(MainActivity.this, DoctorsAppointmentActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, AppointmentsActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Log.d("FetchError", "No such user");
                }
            } else {
                Log.d("FetchError", "Failed to fetch user role", task.getException());
            }
        });
    }

    private void fetchUserProfile() {
        // Get the current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the "users" collection in Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = firestore.collection("users").document(userId);

        // Fetch user data from Firestore
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Retrieve profilePictureUrl from the document
                    String profilePictureUrl = document.getString("profilePictureUrl");
                    loadProfilePicture(profilePictureUrl);
                } else {
                    Log.d("FetchError", "No such user");
                }
            } else {
                Log.d("FetchError", "Failed to fetch user profile", task.getException());
            }
        });
    }

    private void loadProfilePicture(String profilePictureUrl) {
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Glide.with(this)
                    .load(profilePictureUrl)
                    .placeholder(R.drawable.avatar) // Placeholder image
                    .into(profilePicture);
        } else {
            profilePicture.setImageResource(R.drawable.avatar); // Default image if no URL is found
        }
    }


    private void setupNotificationListener(String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");

        // Query to get notifications for the current user
        Query query = databaseReference.orderByChild("userId").equalTo(userId);
        notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int notificationCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NotificationModel notification = snapshot.getValue(NotificationModel.class);
                    if (notification != null && !notification.isRead()) {
                        notificationCount++;
                    }
                }
                counter.setText(String.valueOf(notificationCount));

                if ( notificationCount == 0){
                    imageViewTwo.setVisibility(View.INVISIBLE);
                    counter.setVisibility(View.INVISIBLE);
                }else {
                    imageViewTwo.setVisibility(View.VISIBLE);
                    counter.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        };

        query.addValueEventListener(notificationListener);
    }



    private void showProfilePicOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Profile Picture Options")
                .setItems(new CharSequence[]{"View Profile Picture", "Update Profile Picture"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // View Profile Picture
                                viewProfilePicture();
                                break;
                            case 1:
                                // Update Profile Picture
                                updateProfilePicture();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private String getFragmentName(Fragment fragment) {
        if (fragment instanceof TabOneFragment) {
            return "Doctor";
        } else if (fragment instanceof TabTwoFragment) {
            return "Hospital";
        } else if (fragment instanceof TabThreeFragment) {
            return "Consultant";
        } else if (fragment instanceof TabFourFragment) {
            return "Pharmacy";
        } else if (fragment instanceof TabFiveFragment) {
            return "Treatment Blog";
        } else {
            return "Unknown Fragment";
        }
    }

    private void viewProfilePicture() {
        // Logic to view the profile picture
        // You can implement an activity or a dialog to show the profile picture
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void updateProfilePicture() {
        // Logic to update the profile picture
        // You can implement an activity to allow the user to choose and upload a new profile picture
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void hideBottomNavigation(View view) {
        // Hide the navigation bar
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        view.setSystemUiVisibility(uiOptions);
    }


}






