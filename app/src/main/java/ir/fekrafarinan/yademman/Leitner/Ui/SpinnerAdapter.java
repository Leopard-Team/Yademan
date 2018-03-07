package ir.fekrafarinan.yademman.Leitner.Ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ir.fekrafarinan.yademman.Leitner.Models.Subject;
import ir.fekrafarinan.yademman.R;

public class SpinnerAdapter extends ArrayAdapter<Subject> {

    private ArrayList<Subject> data = new ArrayList<>();
    private Activity activity;
    private LayoutInflater inflater;


    public SpinnerAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull ArrayList<Subject> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.data = objects;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.select_spinner_item, parent, false);
        TextView subjectName = (TextView) row.findViewById(R.id.spinner_row_text);
        subjectName.setTypeface(TypeFaceHandler.sultanBold);
        Subject subject = data.get(position);
        subjectName.setText(subject.getName());
        subjectName.setTextColor(Color.parseColor("#808080"));
        return row;
    }
}
