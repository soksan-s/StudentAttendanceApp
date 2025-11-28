package com.example.studentmanagementsystem.ui;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.api.ApiClient;
import com.example.studentmanagementsystem.api.ApiServices;
import com.example.studentmanagementsystem.model.AttendanceDataItem; // Assuming you use this or similar
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPerformanceActivity extends AppCompatActivity {

    private TextView tvSelectedDate;
    private ImageView btnPickDate;
    private PieChart pieChart;
    private BarChart barChart;
    private ProgressBar pbLoading;

    private ApiServices apiService;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    // Store counts
    private int countPresent = 0;
    private int countAbsent = 0;
    private int countLate = 0;
    private int countExcused = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_performance);

        initializeViews();
        setupToolbar();

        apiService = ApiClient.getClient(this).create(ApiServices.class);

        // Initialize Date
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Set Default Date (Today)
        updateDateDisplay();
        fetchAttendanceData(tvSelectedDate.getText().toString());

        // Date Picker Listener
        btnPickDate.setOnClickListener(v -> showDatePicker());
    }

    private void initializeViews() {
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        btnPickDate = findViewById(R.id.btn_pick_date);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        pbLoading = findViewById(R.id.pb_loading_charts);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_performance);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateDateDisplay();
                    fetchAttendanceData(tvSelectedDate.getText().toString());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        // Optional: restrict future dates if needed
        // datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        tvSelectedDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void fetchAttendanceData(String date) {
        pbLoading.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.INVISIBLE);

        // Reset counts
        countPresent = 0; countAbsent = 0; countLate = 0; countExcused = 0;

        // NOTE: You need an API endpoint that returns attendance for a specific date.
        // If you don't have a specific "summary" endpoint, you might need to fetch the class attendance list
        // and calculate it yourself here.

        // Assuming you have an endpoint like: getAttendanceByDate(date)
        // Or you might be reusing a method that returns List<AttendanceDataItem>

        // EXAMPLE MOCK CALL - Replace with your actual API call logic
        /*
        apiService.getAttendanceByDate(date).enqueue(new Callback<List<AttendanceDataItem>>() {
            @Override
            public void onResponse(Call<List<AttendanceDataItem>> call, Response<List<AttendanceDataItem>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    processAttendanceList(response.body());
                } else {
                    Toast.makeText(ViewPerformanceActivity.this, "No data found for this date", Toast.LENGTH_SHORT).show();
                    pbLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceDataItem>> call, Throwable t) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(ViewPerformanceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */

        // --- TEMPORARY MOCK DATA FOR UI TESTING ---
        // Remove this block once you connect your API
        new android.os.Handler().postDelayed(() -> {
            // Simulating server response
            processMockData();
        }, 1000);
        // ------------------------------------------
    }

    // Replace this with actual logic from API response
    private void processMockData() {
        // Simulating data: Present=15, Absent=5, Late=3, Excused=2
        countPresent = 15;
        countAbsent = 5;
        countLate = 3;
        countExcused = 2;

        setupPieChart();
        setupBarChart();

        pbLoading.setVisibility(View.GONE);
        pieChart.setVisibility(View.VISIBLE);
        barChart.setVisibility(View.VISIBLE);
    }

    private void setupPieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();
        if (countPresent > 0) pieEntries.add(new PieEntry(countPresent, "Present"));
        if (countAbsent > 0) pieEntries.add(new PieEntry(countAbsent, "Absent"));
        if (countLate > 0) pieEntries.add(new PieEntry(countLate, "Late"));
        if (countExcused > 0) pieEntries.add(new PieEntry(countExcused, "Excused"));

        if (pieEntries.isEmpty()) {
            pieChart.setNoDataText("No attendance records for this date.");
            return;
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        // Define Colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4CAF50")); // Green - Present
        colors.add(Color.parseColor("#F44336")); // Red - Absent
        colors.add(Color.parseColor("#FF9800")); // Orange - Late
        colors.add(Color.parseColor("#2196F3")); // Blue - Excused
        dataSet.setColors(colors);

        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Attendance");
        pieChart.setCenterTextSize(18f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.animateY(1000);
        pieChart.invalidate(); // refresh
    }

    private void setupBarChart() {
        List<BarEntry> barEntries = new ArrayList<>();
        // x=0 -> Present, x=1 -> Absent, etc.
        barEntries.add(new BarEntry(0, countPresent));
        barEntries.add(new BarEntry(1, countAbsent));
        barEntries.add(new BarEntry(2, countLate));
        barEntries.add(new BarEntry(3, countExcused));

        BarDataSet dataSet = new BarDataSet(barEntries, "Attendance Status");

        // Same Colors as Pie Chart
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4CAF50"));
        colors.add(Color.parseColor("#F44336"));
        colors.add(Color.parseColor("#FF9800"));
        colors.add(Color.parseColor("#2196F3"));
        dataSet.setColors(colors);

        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);

        // Customize X Axis Labels
        String[] labels = new String[]{"Present", "Absent", "Late", "Excused"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.invalidate(); // refresh
    }
}
