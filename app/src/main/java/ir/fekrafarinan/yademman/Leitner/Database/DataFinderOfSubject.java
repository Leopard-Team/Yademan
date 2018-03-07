package ir.fekrafarinan.yademman.Leitner.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.util.ArrayList;

import ir.fekrafarinan.yademman.Leitner.Activities.HomeActivity;
import ir.fekrafarinan.yademman.Leitner.Models.Student;
import ir.fekrafarinan.yademman.Leitner.Models.StudentCard;
import ir.fekrafarinan.yademman.Leitner.Models.Subject;
import ir.fekrafarinan.yademman.R;

public class DataFinderOfSubject extends AsyncTask<Void, Void, int[]> {

    private TextView tVCardsNum, tVFrontCardNum;
    private Subject subject;
    private Context context;
    private DbHelper dbHelper;
    private static int todayCardsNumber;

    public DataFinderOfSubject(TextView tVCardsNum, TextView tVFrontCardNum, Subject subject, Context context) {
        this.tVCardsNum = tVCardsNum;
        this.tVFrontCardNum = tVFrontCardNum;
        this.subject = subject;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        tVCardsNum.setText(R.string.text_all_cards);
        tVCardsNum.setText(tVCardsNum.getText().toString() + context.getString(R.string.calculating));
        tVFrontCardNum.setText(R.string.text_today_cards);
        tVFrontCardNum.setText(tVFrontCardNum.getText().toString() + context.getString(R.string.calculating));

    }

    @Override

    protected int[] doInBackground(Void... voids) {
        int cardsNum = 0;
        int frontCardNum = 0;
        dbHelper = DbHelper.getInstance(context);
        ArrayList<StudentCard> sCS = dbHelper.getAllStudentCardsWithSubject(subject);
        cardsNum = sCS.size();
        for (StudentCard studentCards : sCS) {
            if (studentCards.isInFront(Student.getLeitnerRule())) {
                frontCardNum++;
            }
        }
        return new int[]{cardsNum, frontCardNum};
    }

    @Override
    protected void onPostExecute(int[] ints) {
        super.onPostExecute(ints);
        tVCardsNum.setText(R.string.text_all_cards);
        tVCardsNum.setText(tVCardsNum.getText().toString() + String.valueOf(ints[0]));
        tVFrontCardNum.setText(R.string.text_today_cards);
        tVFrontCardNum.setText(tVFrontCardNum.getText().toString() + String.valueOf(ints[1]));
        if (HomeActivity.subject.getId() == subject.getId())
            todayCardsNumber = ints[1];
    }

    public static int getTodayCardsNumber() {
        return todayCardsNumber;
    }

    public static void getTodayCardsNumber(final Context context,
                                           final Subject subject, final onFindEnd onFindEnd){
        new AsyncTask<Void, Void, Integer>(){

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                int frontCardNum = 0;
                DbHelper dbHelper = DbHelper.getInstance(context);
                ArrayList<StudentCard> sCS = dbHelper.getAllStudentCardsWithSubject(subject);
                for (StudentCard studentCards : sCS) {
                    if (studentCards.isInFront(Student.getLeitnerRule())) {
                        frontCardNum++;
                    }
                }
                return frontCardNum;
            }

            @Override
            protected void onPostExecute(Integer result) {
                onFindEnd.onEnd(result);
            }
        }.execute();
    }

    public interface onFindEnd{
        public void onEnd(int number);
    }
}
