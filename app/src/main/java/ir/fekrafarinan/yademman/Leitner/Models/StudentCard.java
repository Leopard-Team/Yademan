package ir.fekrafarinan.yademman.Leitner.Models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Utils.DateTimeUtils;


public class StudentCard implements Serializable{
    private int id;
    private Date lastAns;
    private Card card;
    private int serverId;
    private int level;
    private Student student;

    public StudentCard(Date lastAns, Card card, int level, Student student) {
        this.lastAns = lastAns;
        this.card = card;
        this.level = level;
        this.student = student;
        setServerId(-1);
    }

    public Date getLastAns() {
        return lastAns;
    }

    public void setLastAns(Date lastAns) {
        this.lastAns = lastAns;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void getAnswer(boolean answer, int[] leitnerRule, Context context){
        setLastAns(DateTimeUtils.getNow());
        if (answer){
            addLevel(leitnerRule, context);
        }else {
            setLevel(2);
        }
        DbHelper.getInstance(context).updateStudentCard(this, true);
    }

    public void addLevel(int[] leitnerRule, Context context){
        if (level == leitnerRule.length){
            DbHelper.getInstance(context).deleteStudentCard(getId(), true);
        }
        else {
            level++;
        }
    }

    public boolean isInFront(int[] leitnerRule){
        if (DateTimeUtils.daysDifference(lastAns, DateTimeUtils.getNow())
                >= leitnerRule[getLevel() - 1]){
            return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public JSONObject makeJson(){
        JSONObject output = new JSONObject();
        try {
            output.put("local_id", getId());
            output.put("lastAns", DateTimeUtils.dateToDbString(lastAns));
            output.put("card_id", card.getId());
            output.put("level", level);
            output.put("student", student.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    public String toString() {
        return "id: " + id + " lastAns: " + DateTimeUtils.dateToString(lastAns) + " card: " + card.getId() +
                " serverId: " + serverId + " level: " + level;
    }
}
