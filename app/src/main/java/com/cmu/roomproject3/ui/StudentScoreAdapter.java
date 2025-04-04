package com.cmu.roomproject3.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cmu.roomproject3.R;
import com.cmu.roomproject3.model.StudentQuizCrossRef;

import java.util.ArrayList;
import java.util.List;

public class StudentScoreAdapter extends RecyclerView.Adapter<StudentScoreAdapter.ViewHolder>
{
    private List<StudentQuizCrossRef> scores = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StudentQuizCrossRef score = scores.get(position);
        holder.studentIdText.setText(String.valueOf(score.studentId));
        holder.scoreText.setText(String.valueOf(score.score));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public void setScores(List<StudentQuizCrossRef> scores) {
        this.scores = scores;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentIdText, scoreText;

        ViewHolder(View itemView) {
            super(itemView);
            studentIdText = itemView.findViewById(R.id.student_id_text);
            scoreText = itemView.findViewById(R.id.score_text);
        }
    }
}
