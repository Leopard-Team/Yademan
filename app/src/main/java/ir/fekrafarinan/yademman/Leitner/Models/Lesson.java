package ir.fekrafarinan.yademman.Leitner.Models;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import ir.fekrafarinan.yademman.Leitner.Activities.HomeActivity;
import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Ui.DialogHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class Lesson implements Serializable {
    private int id;
    private String name;
    private Subject subject;
    private int year;
    private int price = 100;
    private boolean isBought;

    public Lesson(int id, String name, Subject subject, int year, int price, boolean isBought) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.year = year;
        this.price = price;
        this.isBought = isBought;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }


    public void buy(final Context context, final TextView budgetTv) {
        new DialogHandler(context).showAlert(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Connection.isConnected(context)) {
                    connectAndBuy(context, budgetTv);
                } else {
                    Toast.makeText(context, "لطفا اتصال اینترنت خود را برقرار کنید", Toast.LENGTH_LONG).show();
                }
            }
        }, "آیا مایل به خرید " + name + "در ازای " + price + " تومان هستید؟");
    }

    public void connectAndBuy(final Context context, final TextView budgetTv) {
        HashMap<String, String> params = new HashMap<>();
        params.put("price", Integer.toString(price));
        params.put("ids", "[" + id + "]");
        params.put("user", HomeActivity.student.getUserName());
        params.put("size", 1+"");
        params.put(HomeActivity.student.getUserName(), new SharedPreferencesHandler(context).getToken());
        Connection connection = new Connection(context.getString(R.string.base_url) + "buy.php",
                params, ConnectionUi.getDefault(context)) {
            @Override
            protected void onResult(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    boolean success = json.getBoolean("success");
                    int budget = json.getInt("budget");
                    String message = json.getString("message");
                    if (success) {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        afterBuy(context);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                    HomeActivity.student.setBudget(budget, context);
                    budgetTv.setText("اعتبار:\n" + new SharedPreferencesHandler(context).getBudget() + " تومان");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        connection.execute();
    }

    public void afterBuy(Context context){
        setBought(true);
        SharedPreferencesHandler handler = new SharedPreferencesHandler(context);
        ArrayList<Integer> lessons = handler.getLessonIds();
        lessons.add(id);
        handler.setLessonIds(new JSONArray(lessons).toString());
        DbHelper.getInstance(context).setLessonBought(true, id);
    }

    public static String getIdList(ArrayList<Lesson> lessons){
        ArrayList<Integer> ids = new ArrayList<>();
        for (int i = 0; i < lessons.size(); i++) {
            ids.add(lessons.get(i).getId());
        }
        return new JSONArray(ids).toString();
    }
}
