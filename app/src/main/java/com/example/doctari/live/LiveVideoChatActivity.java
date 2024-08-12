package com.example.doctari.live;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doctari.R;

import org.webrtc.SurfaceViewRenderer;

import io.antmedia.webrtcandroidframework.api.DefaultDataChannelObserver;
import io.antmedia.webrtcandroidframework.api.DefaultWebRTCListener;
import io.antmedia.webrtcandroidframework.api.IDataChannelObserver;
import io.antmedia.webrtcandroidframework.api.IWebRTCClient;
import io.antmedia.webrtcandroidframework.api.IWebRTCListener;

public class LiveVideoChatActivity extends AppCompatActivity {
    private View broadcastingView;
    private View startStreamingButton;
    private String streamId;
    private IWebRTCClient webRTCClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video_chat);

        SurfaceViewRenderer localVideoView = findViewById(R.id.localVideoView);
        SurfaceViewRenderer remoteVideoView = findViewById(R.id.remoteVideoView);

        startStreamingButton = findViewById(R.id.btnStart);

        String serverUrl = "wss://your-server-url.com";

        webRTCClient = IWebRTCClient.builder()
                .setLocalVideoRenderer(localVideoView)
                .addRemoteVideoRenderer(remoteVideoView)
                .setServerUrl(serverUrl)
                .setActivity(this)
                .setWebRTCListener(createWebRTCListener())
                .setDataChannelObserver(createDatachannelObserver())
                .build();

        String streamId = getIntent().getStringExtra("streamId");
        if (streamId != null) {
            webRTCClient.join(streamId);
        }

        startStreamingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startStopStream(v);
                //startStreaming();
            }
        });


    }

    public void startStopStream(View v) {
        streamId = "streamId"; // Replace with your actual stream ID logic
        if (!webRTCClient.isStreaming(streamId)) {
            ((Button) v).setText("Stop");
            Log.i(getClass().getSimpleName(), "Calling play start");
            webRTCClient.join(streamId);
        } else {
            ((Button) v).setText("Start");
            Log.i(getClass().getSimpleName(), "Calling play stop");
            webRTCClient.stop(streamId);
        }
    }

    private IDataChannelObserver createDatachannelObserver() {
        return new DefaultDataChannelObserver() {
            @Override
            public void textMessageReceived(String messageText) {
                super.textMessageReceived(messageText);
                Toast.makeText(LiveVideoChatActivity.this, "Message received: " + messageText, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private IWebRTCListener createWebRTCListener() {
        return new DefaultWebRTCListener() {
            @Override
            public void onPlayStarted(String streamId) {
                super.onPlayStarted(streamId);
                broadcastingView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPlayFinished(String streamId) {
                super.onPlayFinished(streamId);
                broadcastingView.setVisibility(View.GONE);
            }
        };
    }
}
