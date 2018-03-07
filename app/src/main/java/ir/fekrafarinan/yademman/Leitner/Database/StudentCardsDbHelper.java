package ir.fekrafarinan.yademman.Leitner.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import ir.fekrafarinan.yademman.Leitner.Models.Student;
import ir.fekrafarinan.yademman.Leitner.Models.StudentCard;
import ir.fekrafarinan.yademman.Leitner.Models.Subject;
import ir.fekrafarinan.yademman.Leitner.Utils.DateTimeUtils;

public class StudentCardsDbHelper {

    public static void addStudentCard(StudentCard studentCard, DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            if (studentCard == null) {
                Log.e("NULL OBJECT", "Student card");
            }
            if (studentCard.getCard() == null) {
                Log.e("NULL OBJECT", "card");
            }
            ContentValues values = new ContentValues();
            values.put(DbHelper.KEY_CARD_ID, studentCard.getCard().getId());
            values.put(DbHelper.KEY_LAST_ANSWER, DateTimeUtils.dateToDbString(studentCard.getLastAns()));
            values.put(DbHelper.KEY_LEVEL, studentCard.getLevel());
            values.put(DbHelper.KEY_SERVER_ID, studentCard.getServerId());
            values.put(DbHelper.KEY_IS_IN_FRONT, (studentCard.isInFront(Student.getLeitnerRule()) ? 1 : 0));
            studentCard.setId((int) db.insertOrThrow(DbHelper.TABLE_STUDENT_CARD, null, values));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception", e.getMessage());
        } finally {

            db.endTransaction();
        }

    }

    public static ArrayList<StudentCard> getAllStudentCards(DbHelper dbHelper) {
        ArrayList<StudentCard> studentCardList = new ArrayList<StudentCard>();
        String selectQuery = "SELECT " + DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_ID + ", " +
                DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_LAST_ANSWER + ", " +
                DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_CARD_ID + " , " +
                DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_LEVEL +
                " , " + DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_SERVER_ID +
                " FROM " + DbHelper.TABLE_STUDENT_CARD +
                " , " + DbHelper.TABLE_CARDS + " ," + DbHelper.TABLE_LESSONS +
                " WHERE " + DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_CARD_ID + " = " +
                DbHelper.TABLE_CARDS + "." + DbHelper.KEY_ID + " AND " +
                DbHelper.TABLE_CARDS + "." + DbHelper.KEY_LESSON_ID + " = " +
                DbHelper.TABLE_LESSONS + "." + DbHelper.KEY_ID + " AND " +
                DbHelper.TABLE_LESSONS + "." + DbHelper.KEY_SUBJECT_ID + " = " + dbHelper.getSubject().getId()
                + " ORDER BY " + DbHelper.KEY_IS_IN_FRONT + " DESC, " + DbHelper.KEY_LEVEL + " ASC";


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    StudentCard studentCard = new StudentCard(DateTimeUtils.dbStringToDate(
                            cursor.getString(cursor.getColumnIndex(DbHelper.KEY_LAST_ANSWER)))
                            , dbHelper.getCard(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_CARD_ID))),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LEVEL)), dbHelper.getStudent());
                    studentCard.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)));
                    studentCard.setServerId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SERVER_ID)));
                    studentCardList.add(studentCard);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        if (cursor != null)
            cursor.close();
        return studentCardList;
    }

    public static ArrayList<StudentCard> getStudentCardsWithLesson(int lessonId, DbHelper dbHelper) {
        ArrayList<StudentCard> studentCardList = new ArrayList<StudentCard>();
        String selectQuery = "SELECT " + DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_ID + ", " +
                DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_LAST_ANSWER + ", " +
                DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_CARD_ID + " , " +
                DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_LEVEL +
                " , " + DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_SERVER_ID +
                " FROM " + DbHelper.TABLE_STUDENT_CARD +
                " , " + DbHelper.TABLE_CARDS + " ," + DbHelper.TABLE_LESSONS +
                " WHERE " + DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_CARD_ID + " = " +
                DbHelper.TABLE_CARDS + "." + DbHelper.KEY_ID + " AND " +
                DbHelper.TABLE_CARDS + "." + DbHelper.KEY_LESSON_ID + " = " +
                DbHelper.TABLE_LESSONS + "." + DbHelper.KEY_ID + " AND " +
                DbHelper.TABLE_LESSONS + "." + DbHelper.KEY_SUBJECT_ID + " = " + dbHelper.getSubject().getId() +
                " AND " + DbHelper.TABLE_LESSONS + "." + DbHelper.KEY_ID + "=" + lessonId;


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    StudentCard studentCard = new StudentCard(DateTimeUtils.dbStringToDate(
                            cursor.getString(cursor.getColumnIndex(DbHelper.KEY_LAST_ANSWER)))
                            , dbHelper.getCard(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_CARD_ID))),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LEVEL)), dbHelper.getStudent());
                    studentCard.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)));
                    studentCard.setServerId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SERVER_ID)));
                    studentCardList.add(studentCard);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        if (cursor != null)
            cursor.close();
        return studentCardList;
    }

    public static StudentCard getStudentCard(DbHelper dbHelper, int id) {
        String query = "SELECT * FROM " + DbHelper.TABLE_STUDENT_CARD + " WHERE " +
                DbHelper.KEY_ID + "=" + id;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        StudentCard output = null;
        try {
            if (cursor != null && cursor.moveToFirst()) {
                output = new StudentCard(DateTimeUtils.dbStringToDate
                        (cursor.getString(cursor.getColumnIndex(DbHelper.KEY_LAST_ANSWER))),
                        dbHelper.getCard(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_CARD_ID))),
                        cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LEVEL)), dbHelper.getStudent());
                output.setId(id);
                output.setServerId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SERVER_ID)));
            }
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return output;
    }

    public static void deleteStudentCard(int studentCardId, DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM  " + DbHelper.TABLE_STUDENT_CARD +
                    " WHERE " + DbHelper.KEY_ID + "=" + studentCardId);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d("Exception", "Error while trying to delete  users detail");
        } finally {
            db.endTransaction();
        }
    }

    public static void updateStudentCard(StudentCard studentCard, DbHelper dbHelper) {
        try {
            String UPDATE_STUDENT_CARD_TABLE = "UPDATE " + DbHelper.TABLE_STUDENT_CARD + " SET " +
                    DbHelper.KEY_LAST_ANSWER +
                    "=\"" + DateTimeUtils.dateToDbString(studentCard.getLastAns()) + "\", " +
                    DbHelper.KEY_LEVEL + "=" + studentCard.getLevel() + ", " + DbHelper.KEY_SERVER_ID + "=" +
                    studentCard.getServerId() + ", " + DbHelper.KEY_IS_IN_FRONT + "=" +
                    (studentCard.isInFront(Student.getLeitnerRule()) ? 1 : 0) +
                    " WHERE " + DbHelper.KEY_ID + "=" + studentCard.getId();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.execSQL(UPDATE_STUDENT_CARD_TABLE);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }

    }

    public static int findInFrontCardsNum(DbHelper dbHelper) {

        int result = 0;
        try {
            ArrayList<StudentCard> studentCards = getAllStudentCardsWithoutSubject(dbHelper);
            for (StudentCard studentCard :
                    studentCards) {
                if (studentCard.isInFront(Student.getLeitnerRule()))
                    result++;
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return result;
    }

    private static ArrayList<StudentCard> getAllStudentCardsWithoutSubject(DbHelper dbHelper) {
        ArrayList<StudentCard> studentCardList = new ArrayList<StudentCard>();
        String selectQuery = "SELECT * " +
                " FROM " + DbHelper.TABLE_STUDENT_CARD;


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    StudentCard studentCard = new StudentCard(DateTimeUtils.dbStringToDate(
                            cursor.getString(cursor.getColumnIndex(DbHelper.KEY_LAST_ANSWER)))
                            , dbHelper.getCard(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_CARD_ID))),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LEVEL)), dbHelper.getStudent());
                    studentCard.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)));
                    studentCard.setServerId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SERVER_ID)));
                    studentCardList.add(studentCard);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        if (cursor != null)
            cursor.close();
        return studentCardList;
    }

    public static ArrayList<StudentCard> getAllStudentCardsWithSubject(DbHelper dbHelper, Subject subject) {
        ArrayList<StudentCard> studentCardList = new ArrayList<StudentCard>();
        String selectQuery = "SELECT " + DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_ID + ", " +
                DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_LAST_ANSWER + ", " +
                DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_CARD_ID + " , " +
                DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_LEVEL +
                " , " + DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_SERVER_ID +
                " FROM " + DbHelper.TABLE_STUDENT_CARD +
                " , " + DbHelper.TABLE_CARDS + " ," + DbHelper.TABLE_LESSONS +
                " WHERE " + DbHelper.TABLE_STUDENT_CARD + "." + DbHelper.KEY_CARD_ID + " = " +
                DbHelper.TABLE_CARDS + "." + DbHelper.KEY_ID + " AND " +
                DbHelper.TABLE_CARDS + "." + DbHelper.KEY_LESSON_ID + " = " +
                DbHelper.TABLE_LESSONS + "." + DbHelper.KEY_ID + " AND " +
                DbHelper.TABLE_LESSONS + "." + DbHelper.KEY_SUBJECT_ID + " = " + subject.getId()
                + " ORDER BY " + DbHelper.KEY_IS_IN_FRONT + " DESC, " + DbHelper.KEY_LEVEL + " ASC";


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    StudentCard studentCard = new StudentCard(DateTimeUtils.dbStringToDate(
                            cursor.getString(cursor.getColumnIndex(DbHelper.KEY_LAST_ANSWER)))
                            , dbHelper.getCard(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_CARD_ID))),
                            cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_LEVEL)), dbHelper.getStudent());
                    studentCard.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_ID)));
                    studentCard.setServerId(cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_SERVER_ID)));
                    studentCardList.add(studentCard);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        if (cursor != null)
            cursor.close();
        return studentCardList;
    }

    public static void deleteAllStudentCards(DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM " + DbHelper.TABLE_STUDENT_CARD);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }
}
