package com.example.doctari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.doctari.adapters.NotificationAdapter;
import com.example.doctari.models.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private ListView notificationListView;
    private ProgressBar progressBar;
    private NotificationAdapter notificationAdapter;
    private List<NotificationModel> notificationList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference notificationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationListView = findViewById(R.id.notification_list_view);
        progressBar = findViewById(R.id.notification_progress_bar);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList, this);

        notificationListView.setAdapter(notificationAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

        fetchNotifications();
    }

    private void fetchNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = firebaseAuth.getCurrentUser().getUid();
        notificationsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NotificationModel notification = snapshot.getValue(NotificationModel.class);
                    if (notification != null) {
                        notificationList.add(notification);
                        if (!notification.isRead()) {
                            //markNotificationAsRead(notification, snapshot.getKey());
                        }
                    }
                }
                notificationAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                // Handle database error
            }
        });
    }

    private void markNotificationAsRead(NotificationModel notification, String notificationId) {
        // Ensure the notification ID is valid
        if (notificationId == null || notificationId.isEmpty()) {
            System.err.println("Notification ID is null or empty.");
            return;
        }

        // Check if the notification is already marked as read
        if (!notification.isRead()) {
            // Update the local notification object
            notification.setRead(true);

            // Update the database to mark it as read
            notificationsRef.child(notificationId).child("read").setValue(true);

            // Refresh the list view to update the UI
            notificationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNotificationClick(NotificationModel notification) {
        // Print out the notification ID when it is clicked
        System.out.println("Notification clicked: " + notification.getNotificationId());

        // Marks the notification as read when clicked
        markNotificationAsRead(notification, notification.getNotificationId());
    }
}
