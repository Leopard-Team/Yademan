package ir.fekrafarinan.yademman.Leitner.Ui;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ir.fekrafarinan.yademman.Leitner.Models.Quiz;
import ir.fekrafarinan.yademman.Leitner.Models.QuizCenter;
import ir.fekrafarinan.yademman.R;

/**
 * Created by Mohammad on 9/11/2017.
 */

public class QuizRecyclerViewAdapter extends RecyclerView.Adapter<QuizRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Quiz> quizs;
    private Context context;
    private Activity activity;

    public QuizRecyclerViewAdapter(ArrayList<Quiz> quizs, Context context, Activity activity) {
        this.quizs = quizs;
        this.context = context;
        this.activity = activity;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, date;
        ImageView img;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.quizName);
            date = (TextView) itemView.findViewById(R.id.quizDate);
            img = (ImageView) itemView.findViewById(R.id.quizImage);
            name.setTypeface(TypeFaceHandler.sultanBold);
            date.setTypeface(TypeFaceHandler.sultanLight);
            this.itemView = itemView;
        }
    }

    @Override
    public QuizRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_quiz_recycler_view_item, parent, false);
        return new QuizRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuizRecyclerViewAdapter.MyViewHolder holder, final int position) {
        final Quiz quiz = quizs.get(position);
        holder.name.setText(quiz.getName());
        holder.date.setText(quiz.getDate());
        holder.img.setImageDrawable(QuizCenter.getImage(context, quiz.getQuizCenter()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quiz.buy(context, activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizs.size();
    }

}
