package com.cmu.roomproject3.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmu.roomproject3.R;
import com.cmu.roomproject3.model.Quiz;
import com.cmu.roomproject3.model.StudentQuizCrossRef;
import com.cmu.roomproject3.viewmodel.QuizDetailsViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class QuizDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);

        Long quizId = getIntent().getLongExtra("QUIZ_ID", -1L);
        setTitle(getIntent().getStringExtra("QUIZ_NAME"));

        RecyclerView recyclerView = findViewById(R.id.student_score_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        StudentScoreAdapter adapter = new StudentScoreAdapter();
        recyclerView.setAdapter(adapter);

        TextView highScoreText = findViewById(R.id.high_score_text);
        TextView lowScoreText = findViewById(R.id.low_score_text);
        TextView avgScoreText = findViewById(R.id.avg_score_text);

        QuizDetailsViewModel viewModel = new ViewModelProvider(this).get(QuizDetailsViewModel.class);
        viewModel.getQuizWithStudents(quizId).observe(this, quizWithStudents -> {
            List<StudentQuizCrossRef> scores = quizWithStudents.students.stream()
                    .map(student -> {
                        StudentQuizCrossRef crossRef = new StudentQuizCrossRef();
                        crossRef.studentId = student.getStudentId();
                        crossRef.quizId = quizId;
                        // Fetch score from StudentQuizCrossRef table (assuming it's populated)
                        return crossRef;
                    })
                    .collect(Collectors.toList());
            adapter.setScores(scores);

            Quiz quiz = quizWithStudents.quiz;
            highScoreText.setText("High Score: " + quiz.getHighScore());
            lowScoreText.setText("Low Score: " + quiz.getLowScore());
            avgScoreText.setText("Average: " + String.format("%.1f", quiz.getAverageScore()));
        });
    }
}