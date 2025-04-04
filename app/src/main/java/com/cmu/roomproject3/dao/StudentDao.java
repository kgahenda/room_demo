package com.cmu.roomproject3.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.cmu.roomproject3.model.CourseStudentCrossRef;
import com.cmu.roomproject3.model.Student;

@Dao
public interface StudentDao
{
    @Insert
    Long insert(Student student);

    @Insert
    void insertCourseStudentCrossRef(CourseStudentCrossRef crossRef);
}
