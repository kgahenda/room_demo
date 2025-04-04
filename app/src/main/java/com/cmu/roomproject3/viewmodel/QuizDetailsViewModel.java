package com.cmu.roomproject3.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.cmu.roomproject3.database.AppDatabase;
import com.cmu.roomproject3.model.Quiz;
import com.cmu.roomproject3.model.StudentQuizCrossRef;
import com.cmu.roomproject3.model.relations.QuizWithStudents;

import java.util.List;
import java.util.stream.Collectors;

public class QuizDetailsViewModel extends AndroidViewModel
{
    private final AppDatabase db;

    public QuizDetailsViewModel(Application application) {
        super(application);
        db = AppDatabase.getDatabase(application);
    }

    public LiveData<QuizWithStudents> getQuizWithStudents(Long quizId) {
        return db.quizDao().getQuizWithStudents(quizId);
    }

    public LiveData<Quiz> getQuizStats(Long quizId) {
        return Transformations.map(getQuizWithStudents(quizId), quizWithStudents -> {
            Quiz quiz = quizWithStudents.quiz;
            if (quizWithStudents.students.isEmpty()) return quiz;

            List<Double> scores = db.quizDao().getQuizWithStudents(quizId).getValue()
                    .students.stream()
                    .map(student -> {
                        StudentQuizCrossRef crossRef = db.quizDao().getQuizWithStudents(quizId).getValue()
                                .students.stream()
                                .map(s -> new StudentQuizCrossRef())
                                .filter(ref -> ref.studentId.equals(student.getStudentId()) && ref.quizId.equals(quizId))
                                .findFirst().orElse(null);
                        return crossRef != null ? crossRef.score : 0.0;
                    })
                    .collect(Collectors.toList());

            quiz.setHighScore((int) scores.stream().mapToDouble(Double::doubleValue).max().orElse(0));
            quiz.setLowScore((int) scores.stream().mapToDouble(Double::doubleValue).min().orElse(0));
            quiz.setAverageScore(scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
            return quiz;
        });
    }

    public void insertStudentScore(StudentQuizCrossRef crossRef) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.quizDao().insertStudentQuizCrossRef(crossRef);
        });
    }
}
