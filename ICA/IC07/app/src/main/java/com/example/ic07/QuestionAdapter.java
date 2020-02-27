package com.example.ic07;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    public static InteractWithTriviaActivity interact;

    public interface InteractWithTriviaActivity{
        void selectAnswer(int selection);
    }

    public String TAG = "IC07";


    ArrayList<String> choices = new ArrayList<String>();
    Context ctx;

    public QuestionAdapter(ArrayList<String> choices, Context ctx) {
        this.choices = choices;

        this.ctx = ctx;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.question_layout, parent, false);
        return new ViewHolder(rv_layout);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        interact = (InteractWithTriviaActivity) ctx;

        holder.questionText.setText(choices.get(position));

        holder.questionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SELECTED: " + choices.get(position) + " at " + position);
                interact.selectAnswer(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return choices.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.invidiual_question);
        }
    }
}

