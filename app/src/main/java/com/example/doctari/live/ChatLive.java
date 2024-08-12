package com.example.doctari.live;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctari.ProfileActivity;
import com.example.doctari.R;
import com.example.doctari.utils.AndroidUtil;
import com.example.doctari.utils.FirebaseUtil;
import com.example.doctari.utils.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.StorageReference;

import io.antmedia.webrtcandroidframework.api.IWebRTCClient;

public class ChatLive extends AppCompatActivity {

    private IWebRTCClient webRTCClient;
    private ImageView button, toggleLive, avatar, back;
    private String streamId;
    TextView otherUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_live);

        avatar = findViewById(R.id.userProfileImage);
        otherUsername = findViewById(R.id.userName);
        back = findViewById(R.id.back);

        back.setOnClickListener((v)-> onBackPressed());


        String otherUserId = getIntent().getStringExtra("otherUser");
        String otherUserName = getIntent().getStringExtra("otherUsername");

        StorageReference profilePicRef = FirebaseUtil.getOtherProfilePicStorageRef(otherUserId);
        if (profilePicRef != null) {
            profilePicRef.getDownloadUrl().addOnCompleteListener(t -> {
                if (t.isSuccessful()) {
                    Uri uri = t.getResult();
                    AndroidUtil.setProfilePic(this, uri, avatar);
                }  // Handle the case where getting the download URL is not successful
                // For example, show an error message or use a placeholder image

            });
        }

        otherUsername.setText(otherUserName);

        // Retrieve the data from the Intent
        streamId = getIntent().getStringExtra("streamId");

        button = findViewById(R.id.btnStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if webRTCClient is already initialized
                if (webRTCClient == null) {
                    initializeWebRTCClient(); // Initialize WebRTCClient
                }
            }
        });

        toggleLive = findViewById(R.id.btnStop);
        toggleLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopStreaming();
            }
        });

    }

    private void stopStreaming() {
        if (webRTCClient != null) {
            webRTCClient.stop(streamId);
            webRTCClient = null; // Set to null after stopping

            // Delete the stream from the Realtime Database
            deleteStreamFromDatabase(streamId);
        }
    }

    private void deleteStreamFromDatabase(String streamId) {
        // Reference to your Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("videochatrooms");

        // Remove the stream with the given streamId
        databaseReference.child(streamId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Stream successfully deleted from the database
                    Log.d("Firebase", "Stream deleted successfully: " + streamId);
                } else {
                    // Failed to delete the stream from the database
                    Log.e("Firebase", "Failed to delete stream: " + streamId, task.getException());
                }
            }
        });
    }

    private void initializeWebRTCClient() {
        webRTCClient = IWebRTCClient.builder()
                .setActivity(this)
                .setLocalVideoRenderer(findViewById(R.id.localVideoView))
                .setServerUrl("wss://test.antmedia.io:5443/WebRTCAppEE/websocket")
                .build();
        webRTCClient.publish(streamId);

        // Get the other participant's ID
        String otherUserId = getIntent().getStringExtra("otherUser");

        // Send a notification to the other user
        sendVideoCallNotification(streamId, otherUserId);
    }

    private void sendVideoCallNotification(String streamId, String otherUserId) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = firestore.collection("users").document(otherUserId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String fcmToken = document.getString("fcmToken");
                        if (fcmToken != null) {
                            String notificationMessage = "You have an active video call with stream ID: " + streamId;
                            sendNotificationToUser(fcmToken, notificationMessage);
                        }
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });

    }


    private void sendNotificationToUser(String fcmToken, String message) {
        FirebaseMessaging.getInstance().subscribeToTopic("video_call_notifications")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "Subscribed to video call notifications topic");
                        RemoteMessage remoteMessage = new RemoteMessage.Builder(fcmToken)
                                .setMessageId(Integer.toString(message.hashCode()))
                                .addData("message", message)
                                .build();
                        FirebaseMessaging.getInstance().send(remoteMessage);
                    } else {
                        Log.e("FCM", "Subscription to topic failed");
                    }
                });
    }



}