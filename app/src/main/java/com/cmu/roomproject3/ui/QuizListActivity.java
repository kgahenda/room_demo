package com.cmu.roomproject3.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmu.roomproject3.R;
import com.cmu.roomproject3.viewmodel.QuizViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class QuizListActivity extends AppCompatActivity {

    private Long courseId;
    private QuizViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        courseId = getIntent().getLongExtra("COURSE_ID", -1L);
        setTitle(getIntent().getStringExtra("COURSE_TITLE"));

        RecyclerView recyclerView = findViewById(R.id.quiz_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        QuizAdapter adapter = new QuizAdapter(quiz -> {
            Intent intent = new Intent(this, QuizDetailsActivity.class);
            intent.putExtra("QUIZ_ID", quiz.getQuizId());
            intent.putExtra("QUIZ_NAME", quiz.getName());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        viewModel.getQuizzesByCourse(courseId).observe(this, adapter::setQuizzes);

        FloatingActionButton fab = findViewById(R.id.fab_add_quiz);
        fab.setOnClickListener(v -> {
            AddQuizFragment fragment = AddQuizFragment.newInstance(courseId);
            fragment.show(getSupportFragmentManager(), "add_quiz");
        });
    }
}