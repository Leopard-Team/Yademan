package ir.fekrafarinan.yademman.Leitner.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import ir.fekrafarinan.yademman.Leitner.Activities.HomeActivity;
import ir.fekrafarinan.yademman.Leitner.Models.Lesson;
import ir.fekrafarinan.yademman.Leitner.Models.Subject;
import ir.fekrafarinan.yademman.Leitner.Models.Tick;

/**
 * Created by Amin on 7/26/2017.
 */

public class TickDbHelper {
    public static void addTick(Tick tick, DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DbHelper.KEY_YEAR, tick.getYear());
            values.put(DbHelper.KEY_SUBJECT_ID, tick.getSubjectId());
            values.put(DbHelper.KEY_LESSON_ID, tick.getLessonId());
            tick.setId((int) db.insertOrThrow(DbHelper.TABLE_TICKS, null, values));
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("exeption", e.getMessage());
        } finally {
            db.endTransaction();
        }

    }

    public static void deleteTick(int id, DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM  " + DbHelper.TABLE_TICKS +
                    " WHERE " + DbHelper.KEY_ID + "=" + id);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d("Exception", "Error while trying to delete  users detail");
        } finally {
            db.endTransaction();
        }

    }

    public static ArrayList<Tick> getAllTicks(DbHelper dbHelper) {
        ArrayList<Tick> output = new ArrayList<Tick>();
        try {
            String query = "SELECT * FROM " + DbHelper.TABLE_TICKS + " WHERE " + DbHelper.KEY_SUBJECT_ID +
                    " = " + dbHelper.getSubject().getId();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Tick tick = new Tick(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SUBJECT_ID)),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_YEAR)),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LESSON_ID)));
                    tick.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)));
                    output.add(tick);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return output;
    }

    public static ArrayList<Tick> getTicksWithYear(int year, DbHelper dbHelper) {
        ArrayList<Tick> output = new ArrayList<Tick>();
        try {
            String query = "SELECT * FROM " + DbHelper.TABLE_TICKS + " WHERE " + DbHelper.KEY_SUBJECT_ID +
                    " = " + dbHelper.getSubject().getId() + " AND " + DbHelper.KEY_YEAR + " = " + year;

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Tick tick = new Tick(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SUBJECT_ID)),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_YEAR)),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LESSON_ID)));
                    tick.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)));
                    output.add(tick);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return output;

    }

    public static Tick getTick(int id, DbHelper dbHelper) {
        Tick output = null;
        try {
            String query = "SELECT * FROM " + DbHelper.TABLE_TICKS + " WHERE " + DbHelper.KEY_ID + " = " +
                    id;

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                Tick tick = new Tick(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SUBJECT_ID)),
                        cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_YEAR)),
                        cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LESSON_ID)));
                tick.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)));
                output = tick;
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return output;

    }

    public static void updateTick(Tick tick, DbHelper dbHelper) {
        try {
            String UPDATE_TICK_TABLE = "UPDATE " + DbHelper.TABLE_TICKS + " SET " +
                    DbHelper.KEY_LESSON_ID +
                    "=" + tick.getLessonId() + ", " +
                    DbHelper.KEY_SUBJECT_ID + "=" + tick.getSubjectId() + ", " + DbHelper.KEY_YEAR + "=" +
                    tick.getYear() + " WHERE " + DbHelper.KEY_ID + "=" + tick.getId();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.execSQL(UPDATE_TICK_TABLE);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public static void checkTicks(DbHelper dbHelper) {
        try {
            ArrayList<Subject> subjects = dbHelper.getAllSubjects();
            for (Subject subject :
                    subjects) {
                HomeActivity.subject = subject;
                dbHelper.setSubject(subject);
                ArrayList<Lesson> lessons = dbHelper.getAllLessons();
                for (int i = 0; i < lessons.size(); i++) {
                    int studentCardSize = dbHelper.getStudentCardsWithLesson(lessons.get(i).getId()).size();
                    int cardsSize = dbHelper.getCardsWithLesson(lessons.get(i).getId()).size();
                    if (studentCardSize >=
                            cardsSize) {
                        Tick tick = new Tick(dbHelper.getSubject().getId(), lessons.get(i).getYear(),
                                lessons.get(i).getId());
                        if (!getAllTicks(dbHelper).contains(tick))
                            addTick(tick, dbHelper);
                    }

                }
            }
            HomeActivity.subject = subjects.get(0);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public static void deleteAllTicks(DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM  " + DbHelper.TABLE_TICKS);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }
}
