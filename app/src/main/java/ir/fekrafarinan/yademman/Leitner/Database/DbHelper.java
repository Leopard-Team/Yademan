package ir.fekrafarinan.yademman.Leitner.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ir.fekrafarinan.yademman.Leitner.Activities.HomeActivity;
import ir.fekrafarinan.yademman.Leitner.Models.Card;
import ir.fekrafarinan.yademman.Leitner.Models.Lesson;
import ir.fekrafarinan.yademman.Leitner.Models.Notification;
import ir.fekrafarinan.yademman.Leitner.Models.QueueObject;
import ir.fekrafarinan.yademman.Leitner.Models.Student;
import ir.fekrafarinan.yademman.Leitner.Models.StudentCard;
import ir.fekrafarinan.yademman.Leitner.Models.Subject;
import ir.fekrafarinan.yademman.Leitner.Models.Tick;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;

public class DbHelper extends SQLiteOpenHelper {

    private Student student;
    private Subject subject;


    private static final String TAG = "DbHelper";
    private static final String DATABASE_NAME = "database.db";
    public static int DATABASE_VERSION = 1;

    public static final String TABLE_STUDENT_CARD = "studentCard";
    public static final String TABLE_CARDS = "cards";
    public static final String TABLE_LESSONS = "lessons";
    public static final String TABLE_SUBJECTS = "subjects";
    public static final String TABLE_TICKS = "ticks";
    public static final String TABLE_QUEUE = "queue";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String KEY_ID = "id";
    public static final String KEY_CARD_ID = "cardId";
    public static final String KEY_LAST_ANSWER = "lastAns";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_ANSWER = "answer";
    public static final String KEY_LESSON_ID = "lesson_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_SUBJECT_ID = "subject_id";
    public static final String KEY_YEAR = "year";
    public static final String KEY_WORK = "work";
    public static final String KEY_LOCAL_ID = "local_id";
    public static final String KEY_SERVER_ID = "server_id";
    public static final String KEY_QUESTION_IMAGE = "question_image";
    public static final String KEY_ANSWER_IMAGE = "answer_image";
    public static final String KEY_IS_IN_FRONT = "is_in_front";
    public static final String KEY_PRICE = "price";
    public static final String KEY_IS_BOUGHT = "is_bought";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_DATE = "date";
    public static final String KEY_HAS_TEST = "is_test";
    public static final String KEY_ANS1 = "ans1";
    public static final String KEY_ANS2 = "ans2";
    public static final String KEY_ANS3 = "ans3";


    private static DbHelper mDbHelper;
    private Context context;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("onCreate", "dbHelper");
        String CREATE_STUDENT_CARD_TABLE = "CREATE TABLE " + TABLE_STUDENT_CARD + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CARD_ID + " INT, " +
                KEY_LAST_ANSWER + " date default CURRENT_DATE, " + KEY_LEVEL + " INT, "
                + KEY_SERVER_ID + " INT, " + KEY_IS_IN_FRONT + " INT, " +
                "FOREIGN KEY(" + KEY_CARD_ID + ") REFERENCES " +
                TABLE_CARDS + "(" + KEY_ID + ")" + " ) ";
        String CREATE_TICKS_TABLE = "CREATE TABLE " + TABLE_TICKS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_YEAR + " INTEGER, " +
                KEY_LESSON_ID + " INTEGER, " + KEY_SUBJECT_ID + " INTEGER, " + " FOREIGN KEY(" +
                KEY_LESSON_ID + ") REFERENCES " +
                TABLE_LESSONS + "(" + KEY_ID + ")" + ", FOREIGN KEY(" + KEY_SUBJECT_ID + ") REFERENCES " +
                TABLE_SUBJECTS + "(" + KEY_ID + ")" + " ) ";
        String CREATE_QUEUE_TABLE = "CREATE TABLE " + TABLE_QUEUE + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_WORK + " TEXT, " +
                KEY_LOCAL_ID + " INTEGER, " + KEY_SERVER_ID + " INTEGER, " + " FOREIGN KEY(" +
                KEY_LOCAL_ID + ") REFERENCES " + TABLE_STUDENT_CARD + "(" + KEY_ID + ")," +
                "FOREIGN KEY(" + KEY_SERVER_ID + ") REFERENCES " + TABLE_STUDENT_CARD + "(" +
                KEY_SERVER_ID + ")" + " ) ";
        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TITLE + " TEXT, " +
                KEY_MESSAGE + " TEXT, " + KEY_DATE + " varchar)";
        try {
            db.execSQL(CREATE_STUDENT_CARD_TABLE);
            db.execSQL(CREATE_TICKS_TABLE);
            db.execSQL(CREATE_QUEUE_TABLE);
            db.execSQL(CREATE_NOTIFICATIONS_TABLE);
//            encrypt(db);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            onCreate(db);
            HomeActivity.student.updateBoughtDatabase();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        student = HomeActivity.student;
        this.context = context;
    }

    public static void update(Context context){
        DATABASE_VERSION++;
        new SharedPreferencesHandler(context).setDbHelperVersion(DATABASE_VERSION);
        mDbHelper = null;
    }

    public static synchronized DbHelper getInstance(Context context) {
        try {
            if (mDbHelper == null) {
                DATABASE_VERSION = new SharedPreferencesHandler(context).getDbHelperVersion();
                mDbHelper = new DbHelper(context.getApplicationContext());
            }
            mDbHelper.setStudent(HomeActivity.student);
            mDbHelper.setSubject(HomeActivity.subject);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return mDbHelper;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    //static db

    public ArrayList<Subject> getAllSubjects() {
        ArrayList<Subject> subjectList = new ArrayList<Subject>();
        try {
            String selectQuery = "SELECT  * FROM " + TABLE_SUBJECTS;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Subject subject = new Subject(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                            cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                    subjectList.add(subject);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return subjectList;

    }

    public Subject getSubject(int id) {
        try {
            String selectQuery = "SELECT * FROM " + TABLE_SUBJECTS + " WHERE " + KEY_ID + "=" + id;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                Subject output = new Subject(id, cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                cursor.close();
                return output;
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return null;

    }

    public ArrayList<Lesson> getAllLessons() {
        ArrayList<Lesson> lessonList = new ArrayList<Lesson>();
        try {
            String selectQuery = "SELECT * FROM " + TABLE_LESSONS +
                    " WHERE " + KEY_SUBJECT_ID + "=" + subject.getId();

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Lesson lesson = new Lesson(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                            cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                            getSubject(cursor.getInt(cursor.getColumnIndex(KEY_SUBJECT_ID))),
                            cursor.getInt(cursor.getColumnIndex(KEY_YEAR)),
                            cursor.getInt(cursor.getColumnIndex(KEY_PRICE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_IS_BOUGHT)) == 1);
                    lessonList.add(lesson);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return lessonList;
    }


    public ArrayList<Lesson> getLessonsWithYear(int year) {
        ArrayList<Lesson> lessonList = new ArrayList<Lesson>();
        try {
            String selectQuery = "SELECT * FROM " + TABLE_LESSONS + " WHERE " + KEY_YEAR + "=" + year +
                    " AND " + KEY_SUBJECT_ID + "=" + subject.getId();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Lesson lesson = new Lesson(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                            cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                            getSubject(cursor.getInt(cursor.getColumnIndex(KEY_SUBJECT_ID))),
                            cursor.getInt(cursor.getColumnIndex(KEY_YEAR)),
                            cursor.getInt(cursor.getColumnIndex(KEY_PRICE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_IS_BOUGHT)) == 1);
                    lessonList.add(lesson);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return lessonList;
    }

    public int getBoughtLessonsSize(int year){
        ArrayList<Lesson> lessonList = new ArrayList<Lesson>();
        try {
            String selectQuery = "SELECT COUNT(*) FROM " + TABLE_LESSONS + " WHERE " + KEY_YEAR + "=" + year +
                    " AND " + KEY_SUBJECT_ID + "=" + subject.getId() + " AND " + KEY_IS_BOUGHT + "=1";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                    return cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return 0;
    }

    public Lesson getLesson(int id) {
        try {
            String selectQuery = "SELECT * FROM " + TABLE_LESSONS + " WHERE " + KEY_ID + "=" + id;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                Lesson output = new Lesson(id,
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        getSubject(cursor.getInt(cursor.getColumnIndex(KEY_SUBJECT_ID))),
                        cursor.getInt(cursor.getColumnIndex(KEY_YEAR)),
                        cursor.getInt(cursor.getColumnIndex(KEY_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_IS_BOUGHT)) == 1);
                cursor.close();
                return output;
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return null;

    }

    public void setLessonBought(boolean isBought, int lessonId){
        try {
            String UPDATE_STUDENT_CARD_TABLE = "UPDATE " + TABLE_LESSONS + " SET " +
                    KEY_IS_BOUGHT + "=" + (isBought?1:0) + " WHERE " + KEY_ID + "=" +
                    lessonId;
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(UPDATE_STUDENT_CARD_TABLE);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    public ArrayList<Card> getAllCards() {
        ArrayList<Card> cardList = new ArrayList<Card>();
        try {
            // Select All Query
            String selectQuery = "SELECT " + TABLE_CARDS + "." + KEY_ID + " , " + TABLE_CARDS + "." + KEY_QUESTION + " , " + TABLE_CARDS + "." + KEY_ANSWER +
                    "," + TABLE_CARDS + "." + KEY_LESSON_ID + ", " + TABLE_CARDS + "." + KEY_ANSWER_IMAGE + ", " +
                    TABLE_CARDS + "." + KEY_QUESTION_IMAGE + " , " + TABLE_CARDS + "." + KEY_HAS_TEST + " , " +
                    TABLE_CARDS + "." + KEY_ANS1 + " , " + TABLE_CARDS + "." + KEY_ANS2 + " , " + TABLE_CARDS + "." + KEY_ANS3 +
                    " FROM " + TABLE_CARDS + " , " + TABLE_LESSONS +
                    " WHERE " + TABLE_CARDS + "." + KEY_LESSON_ID + "=" + TABLE_LESSONS + "." +
                    KEY_ID + " AND " + TABLE_LESSONS + "." + KEY_SUBJECT_ID + " = " + subject.getId();

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int hasAnswerImage = cursor.getInt(cursor.getColumnIndex(KEY_ANSWER_IMAGE));
                    int hasQuestionImage = cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_IMAGE));
                    Card card = new Card(context, Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                            null, null, null, hasAnswerImage, hasQuestionImage,
                            cursor.getInt(cursor.getColumnIndex(KEY_HAS_TEST)) == 1,
                            cursor.getString(cursor.getColumnIndex(KEY_ANS1)),
                            cursor.getString(cursor.getColumnIndex(KEY_ANS2)),
                            cursor.getString(cursor.getColumnIndex(KEY_ANS3)));
                    card.setQuestion(cursor.getString(cursor.getColumnIndex(KEY_QUESTION)));
                    card.setAnswer(cursor.getString(cursor.getColumnIndex(KEY_ANSWER)));
                    card.setLesson(getLesson(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))));
                    cardList.add(card);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return cardList;
    }

    public ArrayList<Card> getCardsWithLesson(int lessonId) {
        ArrayList<Card> cardList = new ArrayList<Card>();
        try {
            String selectQuery = "SELECT " + TABLE_CARDS + "." + KEY_ID + " , " + TABLE_CARDS + "." + KEY_QUESTION + " , " + TABLE_CARDS + "." + KEY_ANSWER +
                    "," + TABLE_CARDS + "." + KEY_LESSON_ID + ", " + TABLE_CARDS + "." + KEY_ANSWER_IMAGE + ", " +
                    TABLE_CARDS + "." + KEY_QUESTION_IMAGE + " , " + TABLE_CARDS + "." + KEY_HAS_TEST + " , " +
                    TABLE_CARDS + "." + KEY_ANS1 + " , " + TABLE_CARDS + "." + KEY_ANS2 + " , " + TABLE_CARDS + "." + KEY_ANS3 +
                    " FROM " + TABLE_CARDS + " , " + TABLE_LESSONS +
                    " WHERE " + TABLE_CARDS + "." + KEY_LESSON_ID + "=" + TABLE_LESSONS + "." +
                    KEY_ID + " AND " + TABLE_LESSONS + "." + KEY_SUBJECT_ID + " = " + subject.getId() +
                    " AND " + TABLE_CARDS + "." + KEY_LESSON_ID + "=" + lessonId;

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int hasAnswerImage = cursor.getInt(cursor.getColumnIndex(KEY_ANSWER_IMAGE));
                    int hasQuestionImage = cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_IMAGE));
                    Card card = new Card(context, Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                            null, null, null, hasAnswerImage, hasQuestionImage,
                            cursor.getInt(cursor.getColumnIndex(KEY_HAS_TEST)) == 1,
                            cursor.getString(cursor.getColumnIndex(KEY_ANS1)),
                            cursor.getString(cursor.getColumnIndex(KEY_ANS2)),
                            cursor.getString(cursor.getColumnIndex(KEY_ANS3)));
                    card.setQuestion(cursor.getString(cursor.getColumnIndex(KEY_QUESTION)));
                    card.setAnswer(cursor.getString(cursor.getColumnIndex(KEY_ANSWER)));
                    card.setLesson(getLesson(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))));
                    cardList.add(card);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return cardList;
    }

    public Card getCard(int id) {
        try {
            String selectQuery = "SELECT * FROM " + TABLE_CARDS + " WHERE " + KEY_ID + "=" + id;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                Card output = new Card(context, id,
                        cursor.getString(cursor.getColumnIndex(KEY_QUESTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_ANSWER)),
                        getLesson(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))),
                        cursor.getInt(cursor.getColumnIndex(KEY_ANSWER_IMAGE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_IMAGE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_HAS_TEST)) == 1,
                        cursor.getString(cursor.getColumnIndex(KEY_ANS1)),
                        cursor.getString(cursor.getColumnIndex(KEY_ANS2)),
                        cursor.getString(cursor.getColumnIndex(KEY_ANS3)));
                cursor.close();
                return output;
            }

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return null;
    }

    //StudentCards

    public void addStudentCard(StudentCard studentCard) {
        StudentCardsDbHelper.addStudentCard(studentCard, this);
    }

    public void addStudentCards(ArrayList<StudentCard> studentCards, boolean addToServer) {
        try {
            for (StudentCard studentCard :
                    studentCards) {
                addStudentCard(studentCard);
            }
            if (addToServer)
                DataSyncer.getInstance(context).addStudentCards(studentCards);

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public ArrayList<StudentCard> getAllStudentCards() {
        return StudentCardsDbHelper.getAllStudentCards(this);
    }

    public ArrayList<StudentCard> getStudentCardsWithLesson(int lessonId) {
        return StudentCardsDbHelper.getStudentCardsWithLesson(lessonId, this);
    }

    public StudentCard getStudentCard(int id) {
        return StudentCardsDbHelper.getStudentCard(this, id);
    }

    public void deleteAllStudentCards() {
        StudentCardsDbHelper.deleteAllStudentCards(this);
    }

    public void deleteStudentCard(int id, boolean deleteFromServer) {
        try {
            StudentCardsDbHelper.deleteStudentCard(id, this);
            if (deleteFromServer) {
                DataSyncer.getInstance(context).deleteStudentCards(Arrays.asList(getStudentCard(id)));
            }

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public ArrayList<StudentCard> getAllStudentCardsWithSubject(Subject subject){
        return StudentCardsDbHelper.getAllStudentCardsWithSubject(this, subject);
    }

    public void deleteStudentCards(ArrayList<StudentCard> studentCards) {
        try {
            for (StudentCard studentCard :
                    studentCards) {
                deleteStudentCard(studentCard.getId(), false);
            }
            DataSyncer.getInstance(context).deleteStudentCards(studentCards);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void updateStudentCard(StudentCard studentCard, boolean updateToServer) {
        try {
            StudentCardsDbHelper.updateStudentCard(studentCard, this);
            if (updateToServer)
                DataSyncer.getInstance(context).updateStudentCard(studentCard);

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void updateFromServer(ArrayList<StudentCard> studentCards) {
        try {
            deleteAllStudentCards();
            addStudentCards(studentCards, false);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public int findInFrontCardsNum() {

        return StudentCardsDbHelper.findInFrontCardsNum(this);
    }

    //ticks


    public void addTick(Tick tick) {
        try {
            TickDbHelper.addTick(tick, this);

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }

    }

    public void addTicks(ArrayList<Tick> ticks) {
        try {
            for (Tick t : ticks) {
                addTick(t);
            }

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void deleteTick(int id) {
        try {
            TickDbHelper.deleteTick(id, this);

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void deleteTicks(ArrayList<Tick> ticks) {
        try {
            for (Tick t : ticks) {
                deleteTick(t.getId());
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void deleteAllTicks(){
        TickDbHelper.deleteAllTicks(this);
    }

    public ArrayList<Tick> getAllTicks() {
        return TickDbHelper.getAllTicks(this);
    }

    public ArrayList<Tick> getTicksWithYear(int year) {
        return TickDbHelper.getTicksWithYear(year, this);
    }

    public Tick getTick(int id) {
        return TickDbHelper.getTick(id, this);
    }

    public void updateTick(Tick tick) {
        try {
            TickDbHelper.updateTick(tick, this);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void checkTicks() {
        TickDbHelper.checkTicks(this);
    }

    //queues


    public ArrayList<QueueObject> getAllQueue() {
        return QueueDbHelper.getAllQueues(this);
    }

    public ArrayList<QueueObject> getQueueWithWork(String work) {
        return QueueDbHelper.getQueuesWithWork(work, this);
    }

    public QueueObject getQueue(int id) {
        return QueueDbHelper.getQueue(id, this);
    }

    public void addQueue(String work, StudentCard queueObject) {
        try {
            QueueDbHelper.addQueue(work, queueObject, this);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void deleteQueue(int id) {
        QueueDbHelper.deleteQueue(id, this);
    }

    public void deleteAllQueue() {
        QueueDbHelper.deleteAllQueues(this);
    }

    //encrypt

    private void encrypt(SQLiteDatabase db){
        try {
            List<Card> cards = getAllCardsWithoutSubject(db);
            for (Card card:
                 cards) {
                String query = "UPDATE " + TABLE_CARDS + " SET " + KEY_QUESTION + "=\"" +
                        Encrypter.encryptMsg(card.getQuestion(), Encrypter.getKey()) + "\", " +
                        KEY_ANSWER + "=\"" + Encrypter.encryptMsg(card.getAnswer(), Encrypter.getKey()) +
                        "\" WHERE " + KEY_ID + "=" + card.getId();
                db.execSQL(query);
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    private void decrypt(SQLiteDatabase db) {
        try {
            List<Card> cards = getAllCardsWithoutSubject(db);
            for (Card card:
                    cards) {
                String question = Encrypter.decryptMsg(card.getQuestion(), Encrypter.getKey()).replace("\"", "\"\"");
                String answer = Encrypter.decryptMsg(card.getAnswer(), Encrypter.getKey()).replace("\"", "\"\"");
                String query = "UPDATE " + TABLE_CARDS + " SET " + KEY_QUESTION + "=\"" +
                        question + "\", " +
                        KEY_ANSWER + "=\"" + answer +
                        "\" WHERE " + KEY_ID + "=" + card.getId();
                db.execSQL(query);
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    private ArrayList<Card> getAllCardsWithoutSubject(SQLiteDatabase db){
        ArrayList<Card> cardList = new ArrayList<Card>();
        try {

            String selectQuery = "SELECT * " +
                    " FROM " + TABLE_CARDS;

            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int hasAnswerImage = cursor.getInt(cursor.getColumnIndex(KEY_ANSWER_IMAGE));
                    int hasQuestionImage = cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_IMAGE));
                    Card card = new Card(context, Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                            null, null, null, hasAnswerImage, hasQuestionImage,
                            cursor.getInt(cursor.getColumnIndex(KEY_HAS_TEST)) == 1,
                            cursor.getString(cursor.getColumnIndex(KEY_ANS1)),
                            cursor.getString(cursor.getColumnIndex(KEY_ANS2)),
                            cursor.getString(cursor.getColumnIndex(KEY_ANS3)));
                    card.setQuestion(cursor.getString(cursor.getColumnIndex(KEY_QUESTION)));
                    card.setAnswer(cursor.getString(cursor.getColumnIndex(KEY_ANSWER)));
                    card.setLesson(getLesson(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))));
                    cardList.add(card);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return cardList;
    }

    //notifications

    public void addNotification(Notification notification){
        NotificationDbHelper.addNotification(notification, this);
    }

    public ArrayList<Notification> getAllNotifications(){
        return NotificationDbHelper.getAllNotification(this);
    }

    public Notification getNotification(int id){
        return NotificationDbHelper.getNotification(id, this);
    }

    public void deleteNotification(int id){
        NotificationDbHelper.deleteNotification(id, this);
    }

    public void deleteAllNotifications(){
        NotificationDbHelper.deleteAllNotifications(this);
    }

}

