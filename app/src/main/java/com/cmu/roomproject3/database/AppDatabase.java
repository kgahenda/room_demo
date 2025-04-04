package com.cmu.roomproject3.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cmu.roomproject3.dao.CourseDao;
import com.cmu.roomproject3.dao.QuizDao;
import com.cmu.roomproject3.dao.StudentDao;
import com.cmu.roomproject3.model.Course;
import com.cmu.roomproject3.model.CourseStudentCrossRef;
import com.cmu.roomproject3.model.Quiz;
import com.cmu.roomproject3.model.Student;
import com.cmu.roomproject3.model.StudentQuizCrossRef;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Database(
        entities = {
                Course.class,
                Quiz.class,
                Student.class,
                CourseStudentCrossRef.class,
                StudentQuizCrossRef.class
        },
        version = 1
)
public abstract class AppDatabase extends RoomDatabase
{
    public abstract CourseDao courseDao();
    public abstract StudentDao studentDao();
    public abstract QuizDao quizDao();

    private static volatile AppDatabase INSTANCE;

    // Executor service for database write operations
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    // Method to get the database instance
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "quiz_database")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Seed the database on creation
                                    databaseWriteExecutor.execute(() -> {
                                        AppDatabase database = getDatabase(context);
                                        seedDatabase(database);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void seedDatabase(AppDatabase database) {
        CourseDao courseDao = database.courseDao();
        QuizDao quizDao = database.quizDao();
        StudentDao studentDao = database.studentDao();

        // Insert 3 courses
        Course course1 = new Course();
        course1.setName("Programming 101");
        course1.setDescription("Intro to Programming");
        Long course1Id = courseDao.insert(course1);

        Course course2 = new Course();
        course2.setName("Data Structures");
        course2.setDescription("Advanced Data Structures");
        Long course2Id = courseDao.insert(course2);

        Course course3 = new Course();
        course3.setName("Algorithms");
        course3.setDescription("Algorithm Design");
        Long course3Id = courseDao.insert(course3);

        // Insert 5 quizzes for each course
        for (int i = 1; i <= 5; i++) {
            Quiz quiz1 = new Quiz();
            quiz1.setCourseId(course1Id);
            quiz1.setName("Quiz " + i);
            quiz1.setHighScore(0);
            quiz1.setLowScore(0);
            quiz1.setAverageScore(0.0);
            Long quiz1Id = quizDao.insert(quiz1);

            Quiz quiz2 = new Quiz();
            quiz2.setCourseId(course2Id);
            quiz2.setName("Quiz " + i);
            quiz2.setHighScore(0);
            quiz2.setLowScore(0);
            quiz2.setAverageScore(0.0);
            Long quiz2Id = quizDao.insert(quiz2);

            Quiz quiz3 = new Quiz();
            quiz3.setCourseId(course3Id);
            quiz3.setName("Quiz " + i);
            quiz3.setHighScore(0);
            quiz3.setLowScore(0);
            quiz3.setAverageScore(0.0);
            Long quiz3Id = quizDao.insert(quiz3);
        }

        // Insert 10 students
        for (int i = 1; i <= 10; i++) {
            Student student = new Student();
            student.setName("Student " + String.format("%04d", i));
            Long studentId = studentDao.insert(student);

            // Enroll in all courses
            studentDao.insertCourseStudentCrossRef(new CourseStudentCrossRef(course1Id, studentId));
            studentDao.insertCourseStudentCrossRef(new CourseStudentCrossRef(course2Id, studentId));
            studentDao.insertCourseStudentCrossRef(new CourseStudentCrossRef(course3Id, studentId));
        }

        // Insert scores and update stats
        for (long courseId = 1; courseId <= 3; courseId++) {
            for (int quizNum = 1; quizNum <= 5; quizNum++) {
                Long quizId = (courseId - 1) * 5 + quizNum;
                for (long studentId = 1; studentId <= 10; studentId++) {
                    StudentQuizCrossRef score = new StudentQuizCrossRef();
                    score.studentId = studentId;
                    score.quizId = quizId;
                    score.score = 60 + (Math.random() * 40);
                    quizDao.insertStudentQuizCrossRef(score);
                }

                // Update quiz stats synchronously
                List<StudentQuizCrossRef> scores = quizDao.getScoresForQuiz(quizId);
                Quiz quiz = quizDao.getQuizWithStudents(quizId).getValue().quiz;
                quiz.setHighScore((int) scores.stream().mapToDouble(s -> s.score).max().orElse(0));
                quiz.setLowScore((int) scores.stream().mapToDouble(s -> s.score).min().orElse(0));
                quiz.setAverageScore(scores.stream().mapToDouble(s -> s.score).average().orElse(0.0));
                quizDao.update(quiz);
            }
        }
    }
}
