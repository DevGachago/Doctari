package com.example.doctari.live;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doctari.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JoinStream extends AppCompatActivity {

    private EditText editTextStreamId;
    private Button buttonJoinStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_stream);

        editTextStreamId = findViewById(R.id.editText_stream_id);
        buttonJoinStream = findViewById(R.id.button_join_stream);

        buttonJoinStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinStream();
            }
        });
    }

    private void joinStream() {
        String streamId = editTextStreamId.getText().toString().trim();

        if (TextUtils.isEmpty(streamId)) {
            Toast.makeText(this, "Please enter a Stream ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the stream exists and join
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("videochatrooms").child(streamId);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Add the user as a participant
        roomRef.child("participants").child(userId).setValue(true);

        // Increment the participant count
        roomRef.child("participantCount").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Integer count = task.getResult().getValue(Integer.class);
                if (count != null) {
                    roomRef.child("participantCount").setValue(count + 1);
                }
            }
        });

        // Move to the chatroom activity (assuming you have one)
        Intent intent = new Intent(JoinStream.this, VideoChatRoomActivity.class);
        intent.putExtra("streamId", streamId);
        startActivity(intent);
        finish(); // End the current activity
    }
}
