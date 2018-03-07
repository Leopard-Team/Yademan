package ir.fekrafarinan.yademman.Leitner.Models;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ir.fekrafarinan.yademman.Leitner.Activities.HomeActivity;
import ir.fekrafarinan.yademman.Leitner.Activities.SelectCardsActivity;
import ir.fekrafarinan.yademman.Leitner.Activities.ShoppingActivity;
import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Ui.DialogHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.Leitner.Utils.DateTimeUtils;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class Quiz {

    private String name;
    private String date;
    private QuizCenter quizCenter;
    private String lessonIdsList;
    private ArrayList<Lesson> lessons;
    private boolean isBought = false;

    public Quiz(String name, String date, QuizCenter quizCenter, String lessonIdsList) {
        this.name = name;
        this.date = date;
        this.quizCenter = quizCenter;
        this.lessonIdsList = lessonIdsList;
        lessons = new ArrayList<>();
    }

    public void makeLessons(Context context) {
        try {
            DbHelper dbHelper = DbHelper.getInstance(context);
            JSONArray jsonArray = new JSONArray(lessonIdsList);
            for (int i = 0; i < jsonArray.length(); i++) {
                lessons.add(dbHelper.getLesson(jsonArray.getInt(i)));
            }
        } catch (JSONException e) {
            Log.e("Quiz/makeLessons", e.getMessage());
        }

    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(ArrayList<Lesson> lessons) {
        this.lessons = lessons;
    }

    public String getLessonIdsList() {
        return lessonIdsList;
    }

    public void setLessonIdsList(String lessonIdsList) {
        this.lessonIdsList = lessonIdsList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public QuizCenter getQuizCenter() {
        return quizCenter;
    }

    public void setQuizCenter(QuizCenter quizCenter) {
        this.quizCenter = quizCenter;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public void buy(final Context context, final Activity activity) {
        int price = 0;
        isBought = true;
        ArrayList<Lesson> lessons = getLessonsWithSubject();
        final ArrayList<Lesson> notBoughtLessons = new ArrayList<>();
        for (Lesson lesson :
                lessons) {
            if (!lesson.isBought()) {
                notBoughtLessons.add(lesson);
                price += lesson.getPrice();
                isBought = false;
            }
        }
        price *= 0.9;
        final int finalPrice = price;
        if (!isBought) {
            new DialogHandler(context).showAlert(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    activity.startActivity(new Intent(context, ShoppingActivity.class));
                    activity.finish();
                }
            }, "آیا مایل به خرید این درس ها از فروشگاه هستید؟");
        } else

        {
            addLessons(context, activity, lessons);
        }

    }

    private ArrayList<Lesson> getLessonsWithSubject() {
        ArrayList<Lesson> result = new ArrayList<>();
        for (Lesson lesson :
                lessons) {
            if (lesson.getSubject().getId() == HomeActivity.subject.getId())
                result.add(lesson);
        }
        return result;
    }

    private void addLessons(final Context context, final Activity activity, final ArrayList<Lesson> lessons) {
        new DialogHandler(context).showAlert(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        ConnectionUi.getDefault(context).start();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        for (int i = 0; i < lessons.size(); i++) {
                            Lesson lesson = lessons.get(i);
                            ArrayList<Tick> ticks = DbHelper.getInstance(context).getAllTicks();
                            Tick tick = new Tick(lesson.getSubject().getId(), lesson.getYear(), lesson.getId());
                            if (!ticks.contains(tick))
                                addTickToDb(tick, context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        ConnectionUi.getDefault(context).end();
                        Toast.makeText(context, "درس ها با موفقیت افزوده شد", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, SelectCardsActivity.class));
                        activity.finish();
                    }
                }.execute();
            }
        }, "آیا مایل به افزودن درس های مربوط به " + name + " به لیست کارت های خود هستید؟");
    }

    private void connectAndBuy(final Context context, int price,
                               final ArrayList<Lesson> notBoughtLessons, final Activity activity) {
        HashMap<String, String> params = new HashMap<>();
        params.put("price", Integer.toString(price));
        params.put("ids", Lesson.getIdList(notBoughtLessons));
        params.put("user", HomeActivity.student.getUserName());
        params.put("size", notBoughtLessons.size() + "");
        params.put(HomeActivity.student.getUserName(), new SharedPreferencesHandler(context).getToken());
        Connection connection = new Connection(context.getString(R.string.base_url) + "buy.php",
                params, ConnectionUi.getDefault(context)) {
            @Override
            protected void onResult(String result) {
                Log.i("quizBuyResult", result);
                try {
                    JSONObject json = new JSONObject(result);
                    boolean success = json.getBoolean("success");
                    int budget = json.getInt("budget");
                    String message = json.getString("message");
                    if (success) {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        for (int i = 0; i < notBoughtLessons.size(); i++) {
                            notBoughtLessons.get(i).afterBuy(context);
                        }
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                    HomeActivity.student.setBudget(budget, context);
                    addLessons(context, activity, lessons);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        connection.execute();
    }

    private void addTickToDb(Tick tick, Context context) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        dbHelper.addTick(tick);
        ArrayList<Card> cards = dbHelper.getCardsWithLesson(tick.getLessonId());
        ArrayList<StudentCard> studentCards = new ArrayList<>();
        for (Card card : cards) {
            studentCards.add(new StudentCard(DateTimeUtils.getNow(), card, 1, HomeActivity.student));
        }
        dbHelper.addStudentCards(studentCards, true);
    }
}
