package com.example.studentmanagementsystem.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.util.Prefs;
import com.google.android.material.card.MaterialCardView;

public class StudentHomeFragment extends Fragment {

    private TextView tvWelcome;
    private MaterialCardView cardGoToSchedule;
    private MaterialCardView cardGoToAttendance;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize NavController
        navController = Navigation.findNavController(view);

        // Find views by their IDs
        tvWelcome = view.findViewById(R.id.tv_welcome_student);
        cardGoToSchedule = view.findViewById(R.id.card_go_to_schedule);
        cardGoToAttendance = view.findViewById(R.id.card_go_to_attendance);

        // Set the welcome message
        updateWelcomeMessage();

        // Set click listeners for the cards
        cardGoToSchedule.setOnClickListener(v -> {
            // Navigate to the My Schedule screen
            navController.navigate(R.id.nav_my_schedule);
        });

        cardGoToAttendance.setOnClickListener(v -> {
            // Navigate to the My Attendance screen
            navController.navigate(R.id.nav_my_attendance);
        });
    }

    private void updateWelcomeMessage() {
        // Get the logged-in user from SharedPreferences
        User loggedInUser = Prefs.getInstance(getContext()).getUser();
        if (loggedInUser != null) {
            String welcomeText = "Welcome, " + loggedInUser.getName() + "!";
            tvWelcome.setText(welcomeText);
        }
    }
}
