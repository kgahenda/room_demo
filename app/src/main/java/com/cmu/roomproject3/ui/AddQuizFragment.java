package com.cmu.roomproject3.ui;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cmu.roomproject3.R;
import com.cmu.roomproject3.model.Quiz;
import com.cmu.roomproject3.viewmodel.QuizViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddQuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddQuizFragment extends DialogFragment {

    private static final String ARG_COURSE_ID = "course_id";
    private Long courseId;

    public static AddQuizFragment newInstance(Long courseId) {
        AddQuizFragment fragment = new AddQuizFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_COURSE_ID, courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getLong(ARG_COURSE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_quiz, container, false);

        EditText quizNameEditText = view.findViewById(R.id.quiz_name_edit_text);
        Button saveButton = view.findViewById(R.id.save_button);

        QuizViewModel viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        saveButton.setOnClickListener(v -> {
            String quizName = quizNameEditText.getText().toString().trim();
            if (!quizName.isEmpty()) {
                Quiz quiz = new Quiz();
                quiz.setCourseId(courseId);
                quiz.setName(quizName);
                quiz.setHighScore(0); // Initial values
                quiz.setLowScore(0);
                quiz.setAverageScore(0.0);
                viewModel.insert(quiz);
                dismiss();
            }
        });

        return view;
    }
}