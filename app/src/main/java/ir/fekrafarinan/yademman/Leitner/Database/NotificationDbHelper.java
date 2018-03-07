package ir.fekrafarinan.yademman.Leitner.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import ir.fekrafarinan.yademman.Leitner.Models.Notification;
import ir.fekrafarinan.yademman.Leitner.Utils.DateTimeUtils;

/**
 * Created by admin on 9/10/2017.
 */

public class NotificationDbHelper {

    public static void addNotification(Notification notification, DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DbHelper.KEY_TITLE, notification.getTitle());
            values.put(DbHelper.KEY_MESSAGE, notification.getMessage());
            values.put(DbHelper.KEY_DATE, DateTimeUtils.dateToDbString(notification.getDate()));
            notification.setId((int) db.insertOrThrow(DbHelper.TABLE_NOTIFICATIONS, null, values));
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("exeption", e.getMessage());
        } finally {
            db.endTransaction();
        }

    }



    public static void deleteNotification(int id, DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM  " + DbHelper.TABLE_NOTIFICATIONS +
                    " WHERE " + DbHelper.KEY_ID + "=" + id);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d("Exception", "Error while trying to delete  users detail");
        } finally {
            db.endTransaction();
        }

    }


    public static ArrayList<Notification> getAllNotification(DbHelper dbHelper) {
        ArrayList<Notification> output = new ArrayList<Notification>();
        try {
            String query = "SELECT * FROM " + DbHelper.TABLE_NOTIFICATIONS ;

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Notification notification = new Notification(cursor.getString(cursor.getColumnIndex(DbHelper.KEY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(DbHelper.KEY_MESSAGE)),
                            DateTimeUtils.dbStringToDate(cursor.getString(cursor.getColumnIndex(DbHelper.KEY_DATE))));
                    notification.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)));
                    output.add(notification);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return output;
    }


    public static Notification getNotification(int id, DbHelper dbHelper) {
        Notification output = null;
        try {
            String query = "SELECT * FROM " + DbHelper.TABLE_NOTIFICATIONS+ " WHERE " + DbHelper.KEY_ID + " = " +
                    id;

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                Notification notification = new Notification(
                        cursor.getString(cursor.getColumnIndex(DbHelper.KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(DbHelper.KEY_MESSAGE)),
                        DateTimeUtils.dbStringToDate(cursor.getString(cursor.getColumnIndex(DbHelper.KEY_DATE))));
                notification.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)));
                output = notification;
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return output;

    }


    public static void deleteAllNotifications(DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM  " + DbHelper.TABLE_NOTIFICATIONS);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }
}
