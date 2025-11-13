package com.example.studentmanagementsystem.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.util.Prefs;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentProfileFragment extends Fragment {

    private CircleImageView ivProfileImage;
    private TextView tvProfileName, tvProfileEmail, tvProfileStudentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the views from the layout
        ivProfileImage = view.findViewById(R.id.iv_profile_image);
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);
        tvProfileStudentId = view.findViewById(R.id.tv_profile_student_id);

        // Load and display the user's data
        loadUserProfile();
    }

    private void loadUserProfile() {
        // Get user data from SharedPreferences
        User loggedInUser = Prefs.getInstance(getContext()).getUser();

        if (loggedInUser != null) {
            // Set the text for Name and Email
            tvProfileName.setText(loggedInUser.getName());
            tvProfileEmail.setText(loggedInUser.getEmail());

            // You can add more fields to your User model and display them here
            // For example, if you add a studentId field:
            // tvProfileStudentId.setText(loggedInUser.getStudentId());
            tvProfileStudentId.setText("SID12345678"); // Placeholder

            // Use Glide to load the profile picture
            Glide.with(this)
                    .load(loggedInUser.getImage())
                    .placeholder(R.drawable.ic_person_outline) // Default image
                    .error(R.drawable.ic_person_outline) // Image on error
                    .into(ivProfileImage);
        }
    }
}
