package com.example.doctari.live;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doctari.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HostActivity extends AppCompatActivity {

    private EditText editTextStreamId;
    private Button buttonJoinStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        editTextStreamId = findViewById(R.id.editText_stream_id);
        buttonJoinStream = findViewById(R.id.button_join_stream);


        buttonJoinStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStream();
            }
        });
    }

    private void startStream() {
        String streamId = editTextStreamId.getText().toString().trim();

        if (TextUtils.isEmpty(streamId)) {
            Toast.makeText(this, "Please enter a Stream ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference to the new stream in Firebase
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("videochatrooms").child(streamId);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String createdAt = String.valueOf(System.currentTimeMillis()); // Or use a formatted timestamp

        // Retrieve the data from the Intent
        String otherUserId = getIntent().getStringExtra("participant");
        String otherUsername = getIntent().getStringExtra("otherUsername");


        // Create a new VideoChatroom object
        LiveChatModel chatroom = new LiveChatModel(userId, otherUserId, createdAt);

        // Save the new chatroom to Firebase
        roomRef.setValue(chatroom).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Move to the chatroom activity
                Intent intent = new Intent(HostActivity.this, ChatLive.class);
                intent.putExtra("streamId", streamId);
                intent.putExtra("otherUser", otherUserId);
                intent.putExtra("otherUsername", otherUsername);
                startActivity(intent);
                finish(); // End the current activity
            } else {
                Toast.makeText(this, "Failed to start the stream. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}