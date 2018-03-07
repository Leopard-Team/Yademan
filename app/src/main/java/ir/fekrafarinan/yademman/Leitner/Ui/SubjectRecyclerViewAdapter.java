package ir.fekrafarinan.yademman.Leitner.Ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ir.fekrafarinan.yademman.Leitner.Activities.HomeActivity;
import ir.fekrafarinan.yademman.Leitner.Database.DataFinderOfSubject;
import ir.fekrafarinan.yademman.Leitner.Models.Subject;
import ir.fekrafarinan.yademman.R;

public class SubjectRecyclerViewAdapter extends RecyclerView.Adapter<SubjectRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Subject> subjects;
    private Context context;
    private static int todayCards;
    public SubjectRecyclerViewAdapter(ArrayList<Subject> subjects, Context context) {
        this.subjects = subjects;
        this.context = context;
    }

    public static int getTodayCards() {
        return todayCards;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subject, allCards, todayCards;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            subject = (TextView) itemView.findViewById(R.id.txtSubject);
            allCards = (TextView) itemView.findViewById(R.id.txtAllCardsSubject);
            todayCards = (TextView) itemView.findViewById(R.id.txtTodayCardsSubject);
            subject.setTypeface(TypeFaceHandler.sultanBold);
            allCards.setTypeface(TypeFaceHandler.bYekanLight);
            todayCards.setTypeface(TypeFaceHandler.bYekanLight);
            this.itemView = itemView;
        }
    }

    @Override
    public SubjectRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subject_recycler_view_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubjectRecyclerViewAdapter.MyViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.subject.setText(subject.getName());
        DataFinderOfSubject dataFinderOfSubject =
                new DataFinderOfSubject(holder.allCards, holder.todayCards, subject, context);
        dataFinderOfSubject.execute();
        if (position == HomeActivity.subject.getId() - 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#27ffba26"));
        }
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }



}
