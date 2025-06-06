package com.cmu.roomproject3.model.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.cmu.roomproject3.model.Quiz;
import com.cmu.roomproject3.model.Student;
import com.cmu.roomproject3.model.StudentQuizCrossRef;

import java.util.List;

public class QuizWithStudents
{
    @Embedded
    public Quiz quiz;

    @Relation(
            parentColumn = "quizId",
            entityColumn = "studentId",
            associateBy = @Junction(StudentQuizCrossRef.class)
    )
    public List<Student> students;
}
