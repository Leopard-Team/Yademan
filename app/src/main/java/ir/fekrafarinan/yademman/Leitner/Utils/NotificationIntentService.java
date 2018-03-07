package ir.fekrafarinan.yademman.Leitner.Utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ir.fekrafarinan.yademman.Leitner.Activities.HomeActivity;
import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Models.Notification;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.R;
import me.leolin.shortcutbadger.ShortcutBadger;


public class NotificationIntentService extends IntentService {
    public static final int NOTIFICATION_ID_REMIND = 1;
    public static final int NOTIFICATION_ID_SYNC = 2;
    public static final int NOTIFICATION_ID_SERVER = 3;
    public static final String ACTION_START_REMIND = "ACTION_START_REMIND";
    public static final String ACTION_START_SYNC = "ACTION_START_SYNC";
    public static final String ACTION_START_SERVER = "ACTION_START_SERVER";
    public static final String ACTION_DELETE_REMIND = "ACTION_DELETE_REMIND";
    public static final String ACTION_DELETE_SYNC = "ACTION_DELETE_SYNC";
    public static final String ACTION_DELETE_SERVER = "ACTION_DELETE_SERVER";
    public static int number_of_in_front_cards = 0;

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationServiceRemind(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START_REMIND);
        return intent;
    }

    public static Intent createIntentStartNotificationServiceSync(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START_SYNC);
        return intent;
    }

    public static Intent createIntentStartNotificationServiceServer(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START_SERVER);
        return intent;
    }

    public static Intent createIntentDeleteNotificationDelete(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE_REMIND);
        return intent;
    }

    public static Intent createIntentDeleteNotificationSync(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE_SYNC);
        return intent;
    }

    public static Intent createIntentDeleteNotificationServer(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE_SERVER);
        return intent;
    }


    public static void setShotcutBadger(Context context, int num) {
        ShortcutBadger.applyCount(context, num);
    }

    public static void clearShortcutbadger(Context context) {
        ShortcutBadger.removeCount(context);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START_REMIND.equals(action)) {
                processStartNotificationRemind();
            }
            if (ACTION_START_SYNC.equals(action)) {
                processStartNotificationSync();
            }
            if (ACTION_START_SERVER.equals(action)) {
                processStartNotificationServer();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }

    }


    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotificationRemind() {
        try {
            // Do something. For example, fetch fresh data from backend to create a rich notification?
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            Notification notification = new Notification(getString(R.string.notification_title),
                    getString(R.string.notification_text) + number_of_in_front_cards, DateTimeUtils.getNow());
            builder.setContentTitle(notification.getTitle())
                    .setAutoCancel(true)
                    .setColor(ResourcesCompat.getColor(getResources(), android.R.color.transparent, null))
                    .setContentText(notification.getMessage())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));


            Intent mainIntent = new Intent(this, HomeActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    NOTIFICATION_ID_REMIND,
                    mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntentRemind(this));
            DbHelper.getInstance(this).addNotification(notification);
            SharedPreferencesHandler handler = new SharedPreferencesHandler(this);
            handler.setNotificationsNumber(handler.getNotificationsNumber() + 1);
            clearShortcutbadger(NotificationIntentService.this);
            if (handler.getNotificationsNumber() > 0)
                setShotcutBadger(NotificationIntentService.this, handler.getNotificationsNumber());
            final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID_REMIND, builder.build());
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
    }

    private void processStartNotificationSync() {
        try {
            // Do something. For example, fetch fresh data from backend to create a rich notification?

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            Notification notification = new Notification(getString(R.string.notification_title2),
                    getString(R.string.notification_text2), DateTimeUtils.getNow());
            builder.setContentTitle(notification.getTitle())
                    .setAutoCancel(true)
                    .setColor(ResourcesCompat.getColor(getResources(), android.R.color.transparent, null))
                    .setContentText(notification.getMessage())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));


            Intent mainIntent = new Intent(this, HomeActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    NOTIFICATION_ID_SYNC,
                    mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntentSync(this));
            DbHelper.getInstance(this).addNotification(notification);
            SharedPreferencesHandler handler = new SharedPreferencesHandler(this);
            handler.setNotificationsNumber(handler.getNotificationsNumber() + 1);
            clearShortcutbadger(NotificationIntentService.this);
            if (handler.getNotificationsNumber() > 0)
                setShotcutBadger(NotificationIntentService.this, handler.getNotificationsNumber());
            final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID_SYNC, builder.build());
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
    }

    private void processStartNotificationServer() {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("user", HomeActivity.student.getUserName());
            Connection connection = new Connection(getString(R.string.base_url) + "notification.php", params, ConnectionUi.noneUi(this)) {
                @Override
                protected void onResult(String result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        SharedPreferencesHandler handler = new SharedPreferencesHandler(NotificationIntentService.this);
                        int id = json.getInt("id");
                        String title = json.getString("title");
                        String message = json.getString("message");
                        if (handler.getLastNotifId() < id) {
                            final NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationIntentService.this);
                            Notification notification = new Notification(title, message, DateTimeUtils.getNow());
                            builder.setContentTitle(notification.getTitle())
                                    .setAutoCancel(true)
                                    .setColor(ResourcesCompat.getColor(getResources(), android.R.color.transparent, null))
                                    .setContentText(notification.getMessage())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));


                            Intent mainIntent = new Intent(NotificationIntentService.this, HomeActivity.class);

                            PendingIntent pendingIntent = PendingIntent.getActivity(NotificationIntentService.this,
                                    NOTIFICATION_ID_SERVER,
                                    mainIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(pendingIntent);
                            builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntentServer(NotificationIntentService.this));
                            DbHelper.getInstance(NotificationIntentService.this).addNotification(notification);
                            handler.setNotificationsNumber(handler.getNotificationsNumber() + 1);
                            clearShortcutbadger(NotificationIntentService.this);
                            if (handler.getNotificationsNumber() > 0)
                                setShotcutBadger(NotificationIntentService.this, handler.getNotificationsNumber());
                            final NotificationManager manager = (NotificationManager) NotificationIntentService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(NOTIFICATION_ID_SERVER, builder.build());
                            handler.setLastNotifId(id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            if (Connection.isConnected(this))
                connection.execute();
            // Do something. For example, fetch fresh data from backend to create a rich notification?
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
    }


}
