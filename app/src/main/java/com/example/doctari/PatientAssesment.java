package com.example.doctari;

import static android.content.ContentValues.TAG;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.doctari.geminipro.GeminiPro;
import com.example.doctari.geminipro.GeminiProB;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PatientAssesment extends AppCompatActivity {

    private TextInputEditText queryEditText;
    private Button sendQueryButton ;
    private ProgressBar progressBar;
    private LinearLayout chatBodyContainer;
    private ChatFutures chatModel;
    private TextView clearChatTextView;
    private Bitmap image;
    private ImageView imageView, attachfile, exit;

    private static final int SELECT_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int CAMERA_PERMISSION_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_assesment);

        chatModel = getChatModel();

        queryEditText = findViewById(R.id.queryEditText);
        sendQueryButton = findViewById(R.id.sendPromptButton);
        progressBar = findViewById(R.id.sendPromptProgressBar);
        chatBodyContainer = findViewById(R.id.chatResponseLayout);
        clearChatTextView = findViewById(R.id.clearChat);
        imageView = findViewById(R.id.imageView);
        attachfile = findViewById(R.id.attachFile);
        exit = findViewById(R.id.backButton);

        exit.setOnClickListener((v)-> onBackPressed());

        clearChatTextView.setOnClickListener(v -> chatBodyContainer.removeAllViews());

        attachfile.setOnClickListener(v -> {
            showImageSelectionDialog();
        });

        sendQueryButton.setOnClickListener(v -> {
            generateQuery();
        });
    }

    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Capture from Camera"},
                (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
                    } else {
                        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                        } else {
                            openCamera();
                        }
                    }
                });
        builder.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
        }
    }

    private ChatFutures getChatModel() {
        GeminiProB model = new GeminiProB();
        GenerativeModelFutures modelFutures = model.getModel();

        return modelFutures.startChat();
    }

    private void generateQuery() {
        // Proceed with query generation and deduction of bluecoins
        GeminiPro model = new GeminiPro();
        String query = queryEditText.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        queryEditText.setText("");
        populateChatBody("You", query, getDate());

        if (image != null) {
            model.getResponse(query, image, new ResponseCallback() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    populateChatBody("Blue", response, getDate());
                    // Clear the image after a successful response
                    imageView.setImageBitmap(null);
                }

                @Override
                public void onError(Throwable throwable) {
                    progressBar.setVisibility(View.GONE);
                    populateChatBody("Blue", "Sorry, I'm having trouble understanding that. Please try again.", getDate());
                }
            });

        } else {

            GeminiProB.getResponse(chatModel, query, new ResponseCallback() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    populateChatBody("Blue", response, getDate());
                }

                @Override
                public void onError(Throwable throwable) {
                    populateChatBody("Blue", "Sorry, I'm having trouble understanding that. Please try again.", getDate());
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    }

    public void populateChatBody(String userName, String message, String date) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_message_block, null);

        TextView userAgentName = view.findViewById(R.id.userAgentNameTextView);
        TextView userAgentMessage = view.findViewById(R.id.userAgentMessageTextView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);

        userAgentName.setText(userName);
        userAgentMessage.setText(message);
        dateTextView.setText(date);

        // Set the username text color to blue
        userAgentName.setTextColor(getResources().getColor(R.color.greenTheme, null));

        chatBodyContainer.addView(view);

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

        /** if ("Blue".equals(userName)) {
         String cleanedMessage = message.replaceAll("(?<=\\S)\\*\\*", "");
         speak(cleanedMessage);
         }**/

    }

    private String getDate() {
        Instant instant = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instant = Instant.now();
        }
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH-mm").withZone(ZoneId.systemDefault());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return formatter.format(instant);
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap originalImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                int originalWidth = originalImage.getWidth();
                int originalHeight = originalImage.getHeight();

                int targetWidth = (int) (originalWidth * 0.5);
                int targetHeight = (int) (originalHeight * 0.5);

                image = Bitmap.createScaledBitmap(originalImage, targetWidth, targetHeight, false);
                imageView.setImageBitmap(originalImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap capturedImage = (Bitmap) extras.get("data");
            imageView.setImageBitmap(capturedImage);
            image = capturedImage;
        }
    }
}