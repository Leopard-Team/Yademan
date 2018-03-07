package ir.fekrafarinan.yademman.Leitner.Models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import ir.fekrafarinan.yademman.R;

public enum QuizCenter {
    GAJ, GHALAMCHI, GOZINEDO, SANJESH, NOTCONNECTED, CONNECTING;

    public static Drawable getImage(Context context, QuizCenter quizCenter) {
        Drawable drawable = null;
        switch (quizCenter) {
            case GAJ:
                drawable = ContextCompat.getDrawable(context, R.drawable.gaj);
                break;
            case GHALAMCHI:
                drawable = ContextCompat.getDrawable(context, R.drawable.ghalamchi);
                break;
            case GOZINEDO:
                drawable = ContextCompat.getDrawable(context, R.drawable.gozinedo);
                break;
            case SANJESH:
                drawable = ContextCompat.getDrawable(context, R.drawable.sanjesh);
                break;
            case NOTCONNECTED:
                drawable = ContextCompat.getDrawable(context, R.drawable.not_connected);
                break;
            case CONNECTING:
                drawable = ContextCompat.getDrawable(context, R.drawable.loading_anim);
                break;
        }
        return drawable;
    }

    public static QuizCenter getByName(String name){
        for (int i = 0; i < QuizCenter.values().length; i++) {
            if (name.equals(QuizCenter.values()[i].name()))
                return QuizCenter.values()[i];
        }
        return null;
    }
}
