package com.cmu.roomproject3.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cmu.roomproject3.R;
import com.cmu.roomproject3.viewmodel.CourseStatsViewModel;

import java.util.List;

public class CourseStatsFragment extends Fragment {
    private static final String TAG = "CourseStatsFragment";
    private static final String ARG_COURSE_ID = "course_id";
    private static final String ARG_COURSE_TITLE = "course_title";

    private CourseStatsViewModel viewModel;
    private TableLayout statsTable;

    public static CourseStatsFragment newInstance(long courseId, String courseTitle) {
        CourseStatsFragment fragment = new CourseStatsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_COURSE_ID, courseId);
        args.putString(ARG_COURSE_TITLE, courseTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CourseStatsViewModel.class);

        if (getArguments() != null) {
            String courseTitle = getArguments().getString(ARG_COURSE_TITLE);
            viewModel.setCourseName(courseTitle);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_stats, container, false);

        statsTable = view.findViewById(R.id.stats_table);

        if (getArguments() != null) {
            long courseId = getArguments().getLong(ARG_COURSE_ID);
            viewModel.loadStatsForCourse(courseId, requireActivity());
            viewModel.getCourseStats().observe(getViewLifecycleOwner(), this::populateTable);
        }

        return view;
    }

    private void populateTable(List<String> stats) {
        Log.d(TAG, "Stats updated, size: " + stats.size());
        // Clear existing rows
        statsTable.removeAllViews();

        for (String line : stats) {
            TableRow row = new TableRow(requireContext());
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            // Split the line into columns (assuming columns are space-separated with fixed widths)
            String[] columns = line.trim().split("\\s+");

            for (String column : columns) {
                TextView cell = new TextView(requireContext());
                cell.setText(column);
                cell.setPadding(8, 8, 8, 8);
                cell.setTextSize(16);
                cell.setLayoutParams(new TableRow.LayoutParams(
                        0, // Width will be set by stretchColumns
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1f // Equal weight for stretching
                ));

                // Make headers bold
                if (line.startsWith("Course Title") || line.startsWith("Student") || line.startsWith("High") ||
                        line.startsWith("Low") || line.startsWith("Avg")) {
                    cell.setTypeface(null, Typeface.BOLD);
                }

                row.addView(cell);
            }

            statsTable.addView(row);
        }
    }
}