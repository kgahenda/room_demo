package com.cmu.roomproject3.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cmu.roomproject3.database.AppDatabase;
import com.cmu.roomproject3.model.*;
import com.cmu.roomproject3.model.StudentQuizCrossRef;
import com.cmu.roomproject3.model.relations.CourseWithQuizzes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseStatsViewModel extends AndroidViewModel {
    private static final String TAG = "CourseStatsViewModel";
    private final AppDatabase db;
    private final MutableLiveData<List<String>> courseStats;
    private String courseName;

    public CourseStatsViewModel(Application application) {
        super(application);
        db = AppDatabase.getDatabase(application);
        courseStats = new MutableLiveData<>();
    }

    public LiveData<List<String>> getCourseStats() {
        return courseStats;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void loadStatsForCourse(Long courseId, LifecycleOwner lifecycleOwner) {
        try {
            db.courseDao().getCourseWithQuizzes(courseId).observe(lifecycleOwner, courseWithQuizzes -> {
                if (courseWithQuizzes == null) {
                    Log.e(TAG, "CourseWithQuizzes is null for courseId: " + courseId);
                    courseStats.setValue(new ArrayList<>());
                    return;
                }

                Log.d(TAG, "Course: " + courseWithQuizzes.course.getName() + ", Quiz count: " + courseWithQuizzes.quizzes.size());
                for (Quiz quiz : courseWithQuizzes.quizzes) {
                    Log.d(TAG, "Quiz ID: " + quiz.getQuizId() + ", Name: " + quiz.getName() + ", Course ID: " + quiz.getCourseId());
                }

                // Define a final list to hold students
                final List<Student> studentList = new ArrayList<>();

                db.studentDao().getStudentsByCourse(courseId).observe(lifecycleOwner, students -> {
                    if (students == null) {
                        Log.e(TAG, "Students list is null for courseId: " + courseId);
                        studentList.clear();
                    } else {
                        studentList.clear();
                        studentList.addAll(students);
                    }

                    int maxQuizNumber = courseWithQuizzes.quizzes.size();
                    if (maxQuizNumber == 0) {
                        Log.w(TAG, "No quizzes found for courseId: " + courseId);
                        courseStats.setValue(new ArrayList<>());
                        return;
                    }

                    List<String> stats = new ArrayList<>();
                    stats.add("Course Title: " + courseName);
                    stats.add("");
                    stats.add(String.format("%-12s", "Student"));
                    for (int i = 1; i <= maxQuizNumber; i++) {
                        stats.set(stats.size() - 1, stats.get(stats.size() - 1) + String.format("%-11s", "Q" + i));
                    }

                    Map<Integer, List<Double>> quizScoresMap = new HashMap<>();
                    for (int i = 1; i <= maxQuizNumber; i++) {
                        quizScoresMap.put(i, new ArrayList<>());
                    }

                    // Map to store scores for each quiz
                    Map<Long, List<StudentQuizCrossRef>> allScores = new HashMap<>();
                    List<Long> quizIds = new ArrayList<>();
                    for (Quiz quiz : courseWithQuizzes.quizzes) {
                        quizIds.add(quiz.getQuizId());
                    }

                    // Counter to track when all scores are loaded
                    final int[] quizzesProcessed = {0};
                    final int totalQuizzes = quizIds.size();

                    for (int i = 0; i < totalQuizzes; i++) {
                        Long quizId = quizIds.get(i);
                        final int quizIndex = i + 1; // For quizScoresMap (1-based index)

                        db.quizDao().getScoresForQuiz(quizId).observe(lifecycleOwner, scores -> {
                            allScores.put(quizId, scores != null ? scores : new ArrayList<>());
                            Log.d(TAG, "Scores for quizId " + quizId + ": " + allScores.get(quizId).size() + " entries");
                            for (StudentQuizCrossRef score : allScores.get(quizId)) {
                                String studentIdStr = (score.studentId != null) ? score.studentId.toString() : "null";
                                String quizIdStr = (score.quizId != null) ? score.quizId.toString() : "null";
                                Log.d(TAG, "Score - Student ID: " + studentIdStr + ", Quiz ID: " + quizIdStr + ", Score: " + score.score);
                            }

                            quizzesProcessed[0]++;
                            if (quizzesProcessed[0] == totalQuizzes) {
                                // All scores are loaded, build the table
                                for (Student student : studentList) { // Use studentList here
                                    if (student.getStudentId() == null) {
                                        Log.e(TAG, "Student ID is null for student: " + student.getName());
                                        continue;
                                    }

                                    StringBuilder line = new StringBuilder(String.format("%-12d", student.getStudentId()));
                                    for (int j = 0; j < maxQuizNumber; j++) {
                                        Long qId = quizIds.get(j);
                                        List<StudentQuizCrossRef> scoresForQuiz = allScores.get(qId);
                                        StudentQuizCrossRef score = scoresForQuiz.stream()
                                                .filter(s -> s.studentId != null && student.getStudentId().equals(s.studentId))
                                                .findFirst()
                                                .orElse(null);
                                        double scoreValue = (score != null) ? score.score : 0.0;
                                        line.append(String.format("%-11.0f", scoreValue));
                                        quizScoresMap.get(j + 1).add(scoreValue);
                                    }
                                    stats.add(line.toString());
                                }

                                // Add statistics
                                stats.add("");
                                stats.add(String.format("%-12s", "High Score"));
                                stats.add(String.format("%-12s", "Low Score"));
                                stats.add(String.format("%-12s", "Average"));
                                for (int j = 1; j <= maxQuizNumber; j++) {
                                    List<Double> scoresForQuiz = quizScoresMap.get(j);
                                    if (!scoresForQuiz.isEmpty()) {
                                        double high = scoresForQuiz.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                                        double low = scoresForQuiz.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                                        double avg = scoresForQuiz.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                                        stats.set(stats.size() - 3, stats.get(stats.size() - 3) + String.format("%-11.0f", high));
                                        stats.set(stats.size() - 2, stats.get(stats.size() - 2) + String.format("%-11.0f", low));
                                        stats.set(stats.size() - 1, stats.get(stats.size() - 1) + String.format("%-11.1f", avg));
                                    }
                                }
                                courseStats.setValue(stats);
                            }
                        });
                    }
                });
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading stats: " + e.getMessage());
            courseStats.setValue(new ArrayList<>());
        }
    }
}