package ir.fekrafarinan.yademman.Leitner.Utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Models.Student;
import ir.fekrafarinan.yademman.R;

public class SharedPreferencesHandler {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferencesHandler(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("shared", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLastNotifId(int notificationId){

        editor.putInt("notifid", notificationId);
        editor.commit();
    }

    public int getLastNotifId(){
        return sharedPreferences.getInt("notifid", 0);
    }

    public void setDatabaseVersion(String version) {
        editor.putString(context.getString(R.string.database_version_key), version);
        editor.commit();
    }

    public String getDatabaseVersion() {
        return sharedPreferences.getString(context.getString(R.string.database_version_key), "1.0.0");
    }

    public void write(Student student) {
        editor.putBoolean("haveuser", true);
        editor.putInt("id", -1);
        editor.putInt("budget", student.getBudget());
        editor.putString("username", student.getUserName());
        editor.putString("email", student.getEmail());
        editor.commit();
    }

    public Student read() {
        return new Student(sharedPreferences.getString("username", null),
                sharedPreferences.getString("email", null),
                DbHelper.getInstance(context),
                sharedPreferences.getInt("budget", 0));
    }
    public void delete(){
        editor.putBoolean("haveuser",false);
        editor.remove("id");
        editor.remove("username");
        editor.remove("email");
        editor.remove("haveuser");
        editor.remove("budget");
        editor.commit();
    }
    public boolean doesContain(){
        if(sharedPreferences.contains("haveuser")&&sharedPreferences.getBoolean("haveuser",false))
            return true;
        return false;
    }

    public void setIsTutorialShowed(boolean isTutorialShowed){
        editor.putBoolean(context.getString(R.string.key_is_tutorial_showed), isTutorialShowed);
        editor.commit();
    }

    public boolean getIsTutorialShowed(){
        return sharedPreferences.getBoolean(context.getString(R.string.key_is_tutorial_showed), false);
    }

    public void setIsTutorialSelectCardsShowed(boolean isTutorialShowed){
        editor.putBoolean(context.getString(R.string.key_is_tutorial_selected_showed), isTutorialShowed);
        editor.commit();
    }

    public boolean getIsTutorialSelectCardsShowed(){
        return sharedPreferences.getBoolean(context.getString(R.string.key_is_tutorial_selected_showed), false);
    }

    public int getBudget(){
        return sharedPreferences.getInt("budget", 0);
    }

    public void decreaseBudget(int value){
        editor.putInt("budget", sharedPreferences.getInt("budget", 0) - value);
        editor.commit();
    }

    public void increaseBudget(int value){
        editor.putInt("budget", sharedPreferences.getInt("budget", 0) + value);
        editor.commit();
    }

    public void setBudget(int value){
        editor.putInt("budget", value);
        editor.commit();
    }

    public ArrayList<Integer> getLessonIds(){
        String string = sharedPreferences.getString("lesson_ids", "[]");
        ArrayList<Integer> output = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(string);
            for (int i = 0; i < jsonArray.length(); i++) {
                output.add(jsonArray.getInt(i));
            }
            return output;
        } catch (JSONException e) {
            Log.d("Exception", e.getMessage());
        }
        return null;
    }

    public void setLessonIds(String idsString){
        editor.putString("lesson_ids", idsString);
        editor.commit();
    }

    public Date getLastSyncDate(){
        try {
            return DateTimeUtils.stringToDate(sharedPreferences.getString(context.getString(R.string.last_notification_date_key),
                    DateTimeUtils.dateToString(DateTimeUtils.getNow())));
        } catch (ParseException e) {
            Log.e("Exception", e.getMessage());
        }
        return DateTimeUtils.getNow();
    }

    public void setLastSyncDate(Date date){
        editor.putString(context.getString(R.string.last_notification_date_key), DateTimeUtils.dateToString(date));
        editor.commit();
    }

    public String getToken(){
        return sharedPreferences.getString(context.getString(R.string.token_key), "");
    }

    public void setToken(String token){
        editor.putString(context.getString(R.string.token_key), token);
        editor.commit();
    }

    public String getCookie(){
        return sharedPreferences.getString("cookie", "");
    }

    public void setCookie(String cookie){
        editor.putString("cookie", cookie);
        editor.commit();
    }

    public boolean hasCookie(){
        return sharedPreferences.getBoolean("hasCookie", false);
    }

    public void setHasCookie(boolean hasCookie){
        editor.putBoolean("hasCookie", hasCookie);
        editor.commit();
    }

    public boolean hasToNotification(){
        return sharedPreferences.getBoolean(context.getString(R.string.has_to_notification_key), true);
    }

    public void setHasToNotitfication(boolean hasToNotitfication){
        editor.putBoolean(context.getString(R.string.has_to_notification_key), hasToNotitfication);
        editor.commit();
    }

    public void setDbHelperVersion(int version){
        editor.putInt(context.getString(R.string.dbhelper_version_key), version);
        editor.commit();
    }

    public int getDbHelperVersion(){
        return sharedPreferences.getInt(context.getString(R.string.dbhelper_version_key), 1);
    }

    public void setNotificationsNumber(int num){
        editor.putInt("notifNum", num);
        editor.commit();
    }

    public int getNotificationsNumber(){
        return sharedPreferences.getInt("notifNum", 0);
    }

}
