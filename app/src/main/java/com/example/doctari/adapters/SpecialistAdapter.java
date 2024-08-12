package com.example.doctari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doctari.R;
import com.example.doctari.models.Specialist;

import java.util.List;

public class SpecialistAdapter extends RecyclerView.Adapter<SpecialistAdapter.SpecialistViewHolder> {

    private final Context context;
    private final List<Specialist> specList;
    private final SpecialistItemClickListener itemClickListener;

    public interface SpecialistItemClickListener {
        void onSpecialistItemclick(String name, String profilePictureUrl, float rating, String specialization, String certification, String details);
    }

    public SpecialistAdapter(Context context, List<Specialist> specialistList, SpecialistItemClickListener itemClickListener) {
        this.context = context;
        this.specList = specialistList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public SpecialistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_specialist, parent, false);
        return new SpecialistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialistViewHolder holder, int position) {
        Specialist specialist = specList.get(position);

        holder.bind(specialist);
    }

    @Override
    public int getItemCount() {
        return specList.size();
    }

    public class SpecialistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView profilePicture;
        public TextView name;
        public TextView specialization;
        public TextView location;
        public RatingBar rating;
        public TextView verification;
        private final CardView cardView;

        public SpecialistViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.name);
            specialization = itemView.findViewById(R.id.specialization);
            location = itemView.findViewById(R.id.specialist_location);
            //rating = itemView.findViewById(R.id.rating);
            verification = itemView.findViewById(R.id.verification);
            cardView = itemView.findViewById(R.id.specListC);

            cardView.setOnClickListener(this);
        }

        public void bind(Specialist specialist) {
            name.setText(specialist.getName());
            specialization.setText(specialist.getSpecialization());
            location.setText(specialist.getLocation());
            //rating.setRating(specialist.getRating());

            // Set the verification status text
            if (specialist.isVerified()) {
                verification.setText("Verified✓");
                verification.setTextColor(ContextCompat.getColor(context, R.color.blueTheme));
            } else {
                verification.setText("Not Verified");
                verification.setTextColor(ContextCompat.getColor(context, R.color.redTheme));
            }

            // Use Glide to load the profile picture
            Glide.with(context)
                    .load(specialist.getProfilePictureUrl())
                    .placeholder(R.drawable.avatar)
                    .into(profilePicture);
        }

        @Override
        public void onClick(View v) {
            // Pass the clicked news item data to the click listener
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Specialist clickedItem = specList.get(position);
                itemClickListener.onSpecialistItemclick(clickedItem.getName(), clickedItem.getProfilePictureUrl(), clickedItem.getRating(), clickedItem.getSpecialization(), clickedItem.getCertification(), clickedItem.getDetails());
            }
        }
    }
}
