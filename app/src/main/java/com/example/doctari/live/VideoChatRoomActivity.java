package com.example.doctari.live;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doctari.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class VideoChatRoomActivity extends AppCompatActivity {

    private FrameLayout hostVideoContainer;
    private LinearLayout participantsContainer;
    private String streamId;
    private String userId;
    private DatabaseReference roomRef;
    private Button buttonAddParticipant, buttonRemoveParticipant, buttonMuteParticipant, buttonAssignCoHost, buttonEndStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_room);

        // Initialize UI components
        hostVideoContainer = findViewById(R.id.host_video_container);
        participantsContainer = findViewById(R.id.participants_container);

        buttonAddParticipant = findViewById(R.id.button_add_participant);
        buttonRemoveParticipant = findViewById(R.id.button_remove_participant);
        buttonMuteParticipant = findViewById(R.id.button_mute_participant);
        buttonAssignCoHost = findViewById(R.id.button_assign_cohost);
        buttonEndStream = findViewById(R.id.button_end_stream);

        // Get user and stream information
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        streamId = getIntent().getStringExtra("streamId");

        // Set up Firebase reference
        roomRef = FirebaseDatabase.getInstance().getReference("videochatrooms").child(streamId);

        // Show host controls if the current user is the host
        if (isUserHost()) {
            buttonAddParticipant.setVisibility(View.VISIBLE);
            buttonRemoveParticipant.setVisibility(View.VISIBLE);
            buttonMuteParticipant.setVisibility(View.VISIBLE);
            buttonAssignCoHost.setVisibility(View.VISIBLE);
            buttonEndStream.setVisibility(View.VISIBLE);
        }

        // Load chatroom data from Firebase
        loadChatroomData();

        // Set up button click listeners
        buttonAddParticipant.setOnClickListener(v -> {
            // Example usage, you could add more UI to select a participant to add
            addParticipant("newUserId");
        });

        buttonRemoveParticipant.setOnClickListener(v -> {
            // Example usage, you could add more UI to select a participant to remove
            removeParticipant("participantId");
        });

        buttonMuteParticipant.setOnClickListener(v -> {
            // Example usage, you could add more UI to select a participant to mute/unmute
            setParticipantMute("participantId", true);
        });

        buttonAssignCoHost.setOnClickListener(v -> {
            // Example usage, you could add more UI to select a participant to assign as co-host
            assignCoHost("participantId");
        });

        buttonEndStream.setOnClickListener(v -> endLiveStream());
    }

    private void loadChatroomData() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VideoChatroom chatroom = dataSnapshot.getValue(VideoChatroom.class);
                if (chatroom != null) {
                    displayHostView(chatroom.host);
                    displayParticipants(chatroom.participants);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void displayHostView(String hostUserId) {
        // In a real app, you'd add the host's video stream view here
        TextView hostView = new TextView(this);
        hostView.setText("Host: " + hostUserId);
        hostView.setTextColor(getResources().getColor(android.R.color.white));
        hostView.setTextSize(20f);
        hostView.setGravity(View.TEXT_ALIGNMENT_CENTER);

        hostVideoContainer.removeAllViews();
        hostVideoContainer.addView(hostView);
    }

    private void displayParticipants(Map<String, Boolean> participants) {
        participantsContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Map.Entry<String, Boolean> entry : participants.entrySet()) {
            View participantView = inflater.inflate(R.layout.item_participant, participantsContainer, false);

            FrameLayout videoContainer = participantView.findViewById(R.id.participant_video_container);
            TextView textViewRole = participantView.findViewById(R.id.textView_role);
            TextView textViewUserId = participantView.findViewById(R.id.textView_user_id);

            textViewUserId.setText(entry.getKey());

            // Set role (assuming the host is the first entry)
            if (entry.getKey().equals(streamId)) {
                textViewRole.setText("Host");
            } else {
                textViewRole.setText("Participant");
            }

            // In a real app, you'd add the participant's video stream view here

            participantsContainer.addView(participantView);
        }
    }

    // Method to add a participant (host only)
    private void addParticipant(String newUserId) {
        if (isUserHost()) {
            roomRef.child("participants").child(newUserId).setValue(false); // false means not muted
            updateParticipantCount(1);
        } else {
            showToast("Only the host can add participants.");
        }
    }

    // Method to remove a participant (host only)
    private void removeParticipant(String participantId) {
        if (isUserHost()) {
            roomRef.child("participants").child(participantId).removeValue();
            updateParticipantCount(-1);
        } else {
            showToast("Only the host can remove participants.");
        }
    }

    // Method to mute or unmute a participant (host only)
    private void setParticipantMute(String participantId, boolean mute) {
        if (isUserHost()) {
            roomRef.child("participants").child(participantId).setValue(mute);
        } else {
            showToast("Only the host can mute/unmute participants.");
        }
    }

    // Method to assign co-host role (host only)
    private void assignCoHost(String participantId) {
        if (isUserHost()) {
            roomRef.child("coHost").setValue(participantId);
        } else {
            showToast("Only the host can assign a co-host.");
        }
    }

    // Method to end the live session (host only)
    private void endLiveStream() {
        if (isUserHost()) {
            roomRef.removeValue(); // This ends the stream by removing the room from Firebase
            finish(); // Close the activity
        } else {
            showToast("Only the host can end the live stream.");
        }
    }

    // Helper method to update participant count
    private void updateParticipantCount(int delta) {
        roomRef.child("participantCount").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Integer count = task.getResult().getValue(Integer.class);
                if (count != null) {
                    roomRef.child("participantCount").setValue(count + delta);
                }
            }
        });
    }

    // Helper method to check if the current user is the host
    private boolean isUserHost() {
        return userId.equals(roomRef.child("host").getKey());
    }

    // Helper method to show toast messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
