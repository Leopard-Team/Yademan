package ir.fekrafarinan.yademman.Leitner.Database;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ir.fekrafarinan.yademman.Leitner.Activities.HomeActivity;
import ir.fekrafarinan.yademman.Leitner.Models.Card;
import ir.fekrafarinan.yademman.Leitner.Models.QueueObject;
import ir.fekrafarinan.yademman.Leitner.Models.Student;
import ir.fekrafarinan.yademman.Leitner.Models.StudentCard;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.Leitner.Utils.DateTimeUtils;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class DataSyncer {

    private Context context;
    private Student student;
    private static DataSyncer myDataSyncer;

    private DataSyncer(Context context) {
        this.context = context;
        student = new SharedPreferencesHandler(context).read();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public synchronized static DataSyncer getInstance(Context context) {
        try {
            if (myDataSyncer == null) {
                myDataSyncer = new DataSyncer(context);
            }
            myDataSyncer.setContext(context);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return myDataSyncer;
    }

    public void syncWithServer(OnDataSyncEnd onDataSyncEnd) {
        try {
            student = new SharedPreferencesHandler(context).read();
            if (Connection.isConnected(context)) {
                releaseQueue();
                getDataFromServer(onDataSyncEnd);
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void releaseQueue() {
        try {
            DbHelper dbHelper = DbHelper.getInstance(context);

            ArrayList<StudentCard> addStudentCards = convertQueueToStudentCard
                    (dbHelper.getQueueWithWork(ChangeDataTypes.ADD));
            addToServer(addStudentCards);
            ArrayList<StudentCard> deleteStudentCards = convertQueueToStudentCard
                    (dbHelper.getQueueWithWork(ChangeDataTypes.DELETE));
            deleteFromServer(deleteStudentCards);
            ArrayList<StudentCard> updateStudentCards = convertQueueToStudentCard
                    (dbHelper.getQueueWithWork(ChangeDataTypes.UPDATE));
            for (int i = 0; i < updateStudentCards.size(); i++) {
                updateStudentCardToServer(updateStudentCards.get(i));
            }
            dbHelper.deleteAllQueue();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    private ArrayList<StudentCard> convertQueueToStudentCard(ArrayList<QueueObject> queues) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        ArrayList<StudentCard> studentCards = new ArrayList<>();
        try {
            for (QueueObject queueObject :
                    queues) {
                studentCards.add(dbHelper.getStudentCard(queueObject.getLocalId()));
            }
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return studentCards;
    }

    public void getDataFromServer(final OnDataSyncEnd onDataSyncEnd) {
        try {
            final DbHelper dbHelper = DbHelper.getInstance(context);
            HashMap<String, String> params = new HashMap<>();
            final ArrayList<StudentCard> studentCards = new ArrayList<>();
            if (student == null)
                return;
            params.put("Student", student.getUserName());
            params.put("localTime", DateTimeUtils.dateToDbString(DateTimeUtils.getNow()));
            params.put(student.getUserName(), new SharedPreferencesHandler(context).getToken());
            final Connection connection = new Connection(
                    context.getString(R.string.base_url) + "sendStudentCards.php",
                    params, ConnectionUi.getDefault(context)) {

                @Override
                public void endProcess(String result) {
                    try {
                        Log.i("result", result);
                        final JSONArray[] jsonArray = new JSONArray[1];
                        if (result.length() > 1) {
                            try {
                                jsonArray[0] = new JSONArray(result);
                                for (int i = 0; i < jsonArray[0].length(); i++) {
                                    JSONObject jsonObject = jsonArray[0].getJSONObject(i);
                                    Date lastAns = DateTimeUtils.dbStringToDate(jsonObject.getString("last_ans"));
                                    Card card = dbHelper.getCard(Integer.parseInt(jsonObject.getString("card_id")));
                                    int level = Integer.parseInt(jsonObject.getString("level"));
                                    int serverId = Integer.parseInt(jsonObject.getString("id"));
                                    StudentCard studentCard = new StudentCard(lastAns, card, level, student);
                                    studentCard.setServerId(serverId);
                                    studentCards.add(studentCard);
                                }
                                dbHelper.updateFromServer(
                                        studentCards);
                                dbHelper.checkTicks();
                            } catch (JSONException | ParseException e) {
                                Log.d("Exception", e.getMessage());
                            }
                        }

                    } catch (Exception e) {
                        Log.d("Exception", e.getMessage());
                    }
                }

                @Override
                protected void onResult(final String result) {
                    onDataSyncEnd.onEnd();
                }
            };
            connection.execute();


        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void addStudentCards(ArrayList<StudentCard> studentCards) {
        try {
            addToQueue(studentCards);

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    private void addToQueue(ArrayList<StudentCard> studentCards) {
        try {
            DbHelper dbHelper = DbHelper.getInstance(context);
            for (StudentCard sc : studentCards) {
                dbHelper.addQueue(ChangeDataTypes.ADD, sc);
            }

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    private void addToServer(final ArrayList<StudentCard> studentCards) {
        try {
            final DbHelper dbHelper = DbHelper.getInstance(context);
            HashMap<String, String> params = new HashMap<>();
            JSONObject json = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < studentCards.size(); i++) {
                if (studentCards.get(i) != null)
                    jsonArray.put(studentCards.get(i).makeJson().toString());
            }
            try {
                json.put("studentCards", jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            params.put("localTime", DateTimeUtils.dateToDbString(DateTimeUtils.getNow()));
            String jsonString = json.toString().replace("\\", "").replace("\"[", "[").
                    replace("]\"", "]").replace("\"{", "{").replace("}\"", "}");
            params.put("studentCards", jsonString);
            if (student == null)
                return;
            params.put(student.getUserName(), new SharedPreferencesHandler(context).getToken());
            params.put("username", student.getUserName());
            params.put("size", Integer.toString(studentCards.size()));
            Connection connection = new Connection(context.getString(R.string.base_url) + "addStudentCards.php",
                    params, ConnectionUi.getDefault(context)) {

                @Override
                public void endProcess(String result) {
                    try {
                        JSONArray resultArray = new JSONArray(result);
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject identification = resultArray.getJSONObject(i);
                            int localId = identification.getInt("localId");
                            int serverId = identification.getInt("serverId");
                            StudentCard studentCard = dbHelper.getStudentCard(localId);
                            studentCard.setServerId(serverId);
                            dbHelper.updateStudentCard(studentCard, false);
                        }
                    } catch (JSONException e) {
                        Log.d("Exception", e.getMessage());
                    }
                }

                @Override
                protected void onResult(String result) {

                }
            };
            connection.execute();

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }


    public void deleteStudentCards(List<StudentCard> cards) {
        try {
            deleteToQueue(cards);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    private void deleteToQueue(List<StudentCard> cards) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        for (StudentCard sc : cards) {
            dbHelper.addQueue(ChangeDataTypes.DELETE, sc);
        }
    }

    private void deleteFromServer(List<StudentCard> cards) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("size", Integer.toString(cards.size()));
            int[] serverIds = new int[cards.size()];
            for (int i = 0; i < cards.size(); i++) {
                serverIds[i] = cards.get(i).getServerId();
            }
            params.put("studentCards", Arrays.toString(serverIds));
            if (student == null)
                return;
            params.put("user", student.getUserName());
            params.put(HomeActivity.student.getUserName(), new SharedPreferencesHandler(context).getToken());
            Connection connection = new Connection(context.getString(R.string.base_url) + "deleteStudentCards.php",
                    params, ConnectionUi.getDefault(context)) {
                @Override
                protected void onResult(String result) {
                    Log.i("result", result);
                }
            };
            connection.execute();

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void updateStudentCard(StudentCard studentCard) {
        try {
            updateToQueue(studentCard);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    private void updateStudentCardToServer(StudentCard studentCard) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("serverId", Integer.toString(studentCard.getServerId()));
            params.put("lastAns", DateTimeUtils.dateToDbString(studentCard.getLastAns()));
            params.put("level", Integer.toString(studentCard.getLevel()));
            params.put("localTime", DateTimeUtils.dateToDbString(DateTimeUtils.getNow()));
            if (student == null)
                return;
            params.put("user", student.getUserName());
            params.put(student.getUserName(), new SharedPreferencesHandler(context).getToken());
            Connection connection = new Connection(context.getString(R.string.base_url) + "updateStudentCards.php",
                    params, ConnectionUi.getDefault(context)) {
                @Override
                protected void onResult(String result) {
                    Log.i("result", result);
                }
            };
            connection.execute();

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    private void updateToQueue(StudentCard studentCard) {
        try {
            DbHelper dbHelper = DbHelper.getInstance(context);
            dbHelper.addQueue(ChangeDataTypes.UPDATE, studentCard);

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }


}
