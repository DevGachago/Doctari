package com.example.doctari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.doctari.R;
import com.example.doctari.models.NotificationModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends ArrayAdapter<NotificationModel> {

    private final Context mContext;
    private final List<NotificationModel> notificationList;
    private final OnNotificationClickListener onNotificationClickListener;

    public NotificationAdapter(@NonNull Context context, @NonNull List<NotificationModel> objects, OnNotificationClickListener listener) {
        super(context, 0, objects);
        mContext = context;
        notificationList = objects;
        this.onNotificationClickListener = listener; // Initialize the click listener
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        }

        NotificationModel notification = notificationList.get(position);

        TextView titleTextView = convertView.findViewById(R.id.notification_title);
        TextView messageTextView = convertView.findViewById(R.id.notification_message);
        TextView timestampTextView = convertView.findViewById(R.id.notification_timestamp);

        titleTextView.setText(notification.getTitle());
        messageTextView.setText(notification.getMessage());

        // Convert the timestamp from long to Date and format it
        Date date = new Date(notification.getTimestamp());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String formattedTimestamp = sdf.format(date);
        timestampTextView.setText(formattedTimestamp);

        if (notification.isRead()) {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.white));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.darker_gray));
        }

        // Set the OnClickListener for the item view
        convertView.setOnClickListener(v -> {
            if (onNotificationClickListener != null) {
                onNotificationClickListener.onNotificationClick(notification);
            }
        });

        return convertView;
    }

    // Interface to handle item clicks
    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationModel notification);
    }
}
