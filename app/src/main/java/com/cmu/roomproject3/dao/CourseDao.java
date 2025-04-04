package com.cmu.roomproject3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.cmu.roomproject3.model.Course;
import com.cmu.roomproject3.model.relations.CourseWithQuizzes;

import java.util.List;

@Dao
public interface CourseDao
{
    @Insert
    Long insert(Course course);

    @Query("SELECT * FROM Course")
    LiveData<List<Course>> getAllCourses();

    @Transaction
    @Query("SELECT * FROM Course WHERE courseId = :courseId")
    LiveData<CourseWithQuizzes> getCourseWithQuizzes(Long courseId);
}
