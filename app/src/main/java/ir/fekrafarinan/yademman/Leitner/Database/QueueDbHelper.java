package ir.fekrafarinan.yademman.Leitner.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import ir.fekrafarinan.yademman.Leitner.Models.QueueObject;
import ir.fekrafarinan.yademman.Leitner.Models.StudentCard;

/**
 * Created by admin on 7/26/2017.
 */

public class QueueDbHelper {
    public static ArrayList<QueueObject> getAllQueues(DbHelper dbHelper) {
        ArrayList<QueueObject> cardList = new ArrayList<QueueObject>();
        try {
            String selectQuery = "SELECT  * FROM " + DbHelper.TABLE_QUEUE;

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    QueueObject queueObject = new QueueObject(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(DbHelper.KEY_WORK)),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LOCAL_ID)),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SERVER_ID)));
                    cardList.add(queueObject);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return cardList;
    }

    public static ArrayList<QueueObject> getQueuesWithWork(String work, DbHelper dbHelper) {
        ArrayList<QueueObject> cardList = new ArrayList<QueueObject>();
        try {
            String selectQuery = "SELECT * FROM " + DbHelper.TABLE_QUEUE + " WHERE " + DbHelper.KEY_WORK + "=\"" + work + "\"";
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    QueueObject tmp = new QueueObject(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(DbHelper.KEY_WORK))
                            , cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LOCAL_ID)),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SERVER_ID)));
                    cardList.add(tmp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return cardList;
    }

    public static QueueObject getQueue(int id, DbHelper dbHelper) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(DbHelper.TABLE_QUEUE, new String[]{DbHelper.KEY_ID,
                            DbHelper.KEY_WORK, DbHelper.KEY_LOCAL_ID, DbHelper.KEY_SERVER_ID}, DbHelper.KEY_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }

            QueueObject output = new QueueObject(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DbHelper.KEY_ID))),
                    cursor.getString(cursor.getColumnIndex(DbHelper.KEY_WORK)),
                    cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LOCAL_ID)),
                    cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SERVER_ID)));
            // return contact
            cursor.close();
            return output;
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return null;
    }

    public static void addQueue(String work, StudentCard studentCard, DbHelper dbHelper) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DbHelper.KEY_WORK, work);
            values.put(DbHelper.KEY_LOCAL_ID, studentCard.getId());
            values.put(DbHelper.KEY_SERVER_ID, studentCard.getServerId());
            db.insertOrThrow(DbHelper.TABLE_QUEUE, null, values);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("Exception", e.getMessage());
        } finally {

            db.endTransaction();
        }
    }

    public static void deleteAllQueues(DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM  " + DbHelper.TABLE_QUEUE);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d("Exeption", "Error while trying to delete  users detail");
        } finally {
            db.endTransaction();
        }
    }

    public static void deleteQueue(int queueId, DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM  " + DbHelper.TABLE_QUEUE +
                    " WHERE " + DbHelper.KEY_ID + "=" + queueId);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d("Exception", "Error while trying to delete  users detail");
        } finally {
            db.endTransaction();
        }
    }

}
