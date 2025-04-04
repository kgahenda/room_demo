package com.cmu.roomproject3.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Quiz
{
    @PrimaryKey(autoGenerate = true)
    private Long quizId;
    private Long courseId;
    private String name;
    private int highScore;
    private int lowScore;
    private double averageScore;

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long cId)
    {
        courseId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getLowScore() {
        return lowScore;
    }

    public void setLowScore(int lowScore) {
        this.lowScore = lowScore;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }
}
