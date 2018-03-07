package ir.fekrafarinan.yademman.Leitner.Models;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;


public class Student implements Serializable {
    private int id;
    private String userName;
    private String password;
    private DbHelper dbHelper;
    private String email;
    private List<StudentCard> studentCards = new ArrayList<>();
    private ArrayList<Integer> bought_ids = new ArrayList<>();
    private int budget = 0;
    private static final int[] leitnerRule = {0, 1, 3, 7, 14};

    public void updateStudentCards(Context context) {
        dbHelper = DbHelper.getInstance(context);
        studentCards = dbHelper.getAllStudentCards();
    }

    public Student(String userName, String email,
                   DbHelper dbHelper, int budget) {
        this.userName = userName;
        this.email = email;
        this.dbHelper = dbHelper;
        this.budget = budget;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<StudentCard> getStudentCards() {
        return studentCards;
    }

    public void setStudentCards(List<StudentCard> studentCards) {
        this.studentCards = studentCards;
    }

    public void addToStudentCards(List<StudentCard> cards) {

    }

    public StudentCard getFirstCard() {
        if (studentCards.size() == 0)
            return null;
        int r = (int) (Math.random() * studentCards.size());
        for (int i = r; i < studentCards.size(); i++)
            if (studentCards.get(i).isInFront(leitnerRule))
                return studentCards.get(i);
        for (int i = r; i >= 0; i--)
            if (studentCards.get(i).isInFront(leitnerRule))
                return studentCards.get(i);
        return null;
    }

    public static int[] getLeitnerRule() {
        return leitnerRule;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DbHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget, Context context) {
        this.budget = budget;
        new SharedPreferencesHandler(context).setBudget(budget);
    }

    public void decreaseBudget(int value) {
        budget -= value;
    }

    public void increaseBudget(int value) {
        budget += value;
    }

    public ArrayList<Integer> getBought_ids() {
        return bought_ids;
    }

    public void setBought_ids(ArrayList<Integer> bought_ids) {
        this.bought_ids = bought_ids;
    }

    public void updateBoughtDatabase() {
        int[] defaultBoughtIds = {1, 2, 3, 4, 5, 6, 47, 48, 49, 75
            , 76, 77, 107, 108, 118, 119, 120, 121, 122, 123, 166, 167, 168, 215, 223};
        for (int id :
                bought_ids) {
            dbHelper.setLessonBought(true, id);
        }
        for (int id :
                defaultBoughtIds) {
            dbHelper.setLessonBought(true, id);
        }
    }
}
