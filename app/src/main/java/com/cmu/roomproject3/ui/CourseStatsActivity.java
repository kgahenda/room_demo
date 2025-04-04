package com.cmu.roomproject3.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cmu.roomproject3.R;
import com.cmu.roomproject3.viewmodel.CourseStatsViewModel;

public class CourseStatsActivity extends AppCompatActivity {
    private static final String TAG = "CourseStatsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_stats);

        Long courseId = getIntent().getLongExtra("COURSE_ID", -1L);
        String courseTitle = getIntent().getStringExtra("COURSE_TITLE");
        Log.d(TAG, "Course ID: " + courseId + ", Course Title: " + courseTitle);
        setTitle(courseTitle);

        TextView statsTextView = findViewById(R.id.stats_text_view);

        CourseStatsViewModel viewModel = new ViewModelProvider(this).get(CourseStatsViewModel.class);
        viewModel.setCourseName(courseTitle);
        viewModel.loadStatsForCourse(courseId, this); // Pass the activity as LifecycleOwner
        viewModel.getCourseStats().observe(this, stats -> {
            Log.d(TAG, "Stats updated, size: " + stats.size());
            StringBuilder displayText = new StringBuilder();
            for (String line : stats) {
                displayText.append(line).append("\n");
            }
            statsTextView.setText(displayText.toString());
        });
    }
}