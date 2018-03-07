package ir.fekrafarinan.yademman.Leitner.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;

public class NotificationEventReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_START_NOTIFICATION_SERVICE_REMIND = "ACTION_START_NOTIFICATION_SERVICE_REMIND";
    private static final String ACTION_START_NOTIFICATION_SERVICE_SYNC = "ACTION_START_NOTIFICATION_SERVICE_SYNC";
    private static final String ACTION_START_NOTIFICATION_SERVICE_SERVER = "ACTION_START_NOTIFICATION_SERVICE_SERVER";
    private static final String ACTION_DELETE_NOTIFICATION_REMIND = "ACTION_DELETE_NOTIFICATION_REMIND";
    private static final String ACTION_DELETE_NOTIFICATION_SYNC = "ACTION_DELETE_NOTIFICATION_SYNC";
    private static final String ACTION_DELETE_NOTIFICATION_SERVER = "ACTION_DELETE_NOTIFICATION_SERVER";

    private static final int NOTIFICATIONS_INTERVAL_IN_HOURS_REMIND = 24;
    private static final int NOTIFICATIONS_INTERVAL_IN_HOURS_SYNC = 72;
    private static final int NOTIFICATIONS_INTERVAL_IN_HOURS_SERVER = 6;
    private static final String NOTIFICATION_TIME_REMIND = "07:30:00";
    private static final String NOTIFICATION_TIME_SYNC = "21:00:00";
    private static final String NOTIFICATION_TIME_SERVER = "09:00:00";

    public static void setupAlarmRemind(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent alarmIntent = getStartPendingIntentRemind(context);
            try {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        getTriggerAt(DateTimeUtils.stringToDate
                                (DateTimeUtils.getNowWithCustomTime(NOTIFICATION_TIME_REMIND))),
                        NOTIFICATIONS_INTERVAL_IN_HOURS_REMIND * AlarmManager.INTERVAL_HOUR,
                        alarmIntent);
            } catch (ParseException e) {
                Log.e("Exception", e.getMessage());
            }
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
    }

    public static void setupAlarmSync(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent alarmIntent = getStartPendingIntentSync(context);
            try {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        getTriggerAt(DateTimeUtils.stringToDate
                                (DateTimeUtils.getNowWithCustomTime(NOTIFICATION_TIME_SYNC))),
                        NOTIFICATIONS_INTERVAL_IN_HOURS_SYNC * AlarmManager.INTERVAL_HOUR,
                        alarmIntent);
            } catch (ParseException e) {
                Log.e("Exception", e.getMessage());
            }
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
    }

    public static void setupAlarmServer(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent alarmIntent = getStartPendingIntentServer(context);
            try {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        getTriggerAt(DateTimeUtils.stringToDate
                                (DateTimeUtils.getNowWithCustomTime(NOTIFICATION_TIME_SERVER))),
                        NOTIFICATIONS_INTERVAL_IN_HOURS_SERVER * AlarmManager.INTERVAL_HOUR,
                        alarmIntent);
            } catch (ParseException e) {
                Log.e("Exception", e.getMessage());
            }
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntentRemind(context);
        alarmManager.cancel(alarmIntent);
    }

    private static long getTriggerAt(Date now) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR, NOTIFICATIONS_INTERVAL_IN_HOURS_REMIND);
            return calendar.getTimeInMillis();
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
        return 1;
    }

    private static PendingIntent getStartPendingIntentRemind(Context context) {
        try {
            Intent intent = new Intent(context, NotificationEventReceiver.class);
            intent.setAction(ACTION_START_NOTIFICATION_SERVICE_REMIND);
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
        return null;
    }

    private static PendingIntent getStartPendingIntentSync(Context context) {
        try {
            Intent intent = new Intent(context, NotificationEventReceiver.class);
            intent.setAction(ACTION_START_NOTIFICATION_SERVICE_SYNC);
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
        return null;
    }

    private static PendingIntent getStartPendingIntentServer(Context context) {
        try {
            Intent intent = new Intent(context, NotificationEventReceiver.class);
            intent.setAction(ACTION_START_NOTIFICATION_SERVICE_SERVER);
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
        return null;
    }


    public static PendingIntent getDeleteIntentRemind(Context context) {
        try {
            Intent intent = new Intent(context, NotificationEventReceiver.class);
            intent.setAction(ACTION_DELETE_NOTIFICATION_REMIND);
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
        return null;
    }

    public static PendingIntent getDeleteIntentSync(Context context) {
        try {
            Intent intent = new Intent(context, NotificationEventReceiver.class);
            intent.setAction(ACTION_DELETE_NOTIFICATION_SYNC);
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
        return null;
    }

    public static PendingIntent getDeleteIntentServer(Context context) {
        try {
            Intent intent = new Intent(context, NotificationEventReceiver.class);
            intent.setAction(ACTION_DELETE_NOTIFICATION_SERVER);
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
        return null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            Intent serviceIntent = null;
            SharedPreferencesHandler handler = new SharedPreferencesHandler(context);
            if (handler.doesContain() && handler.hasToNotification()) {
                if (ACTION_START_NOTIFICATION_SERVICE_REMIND.equals(action)) {
                    NotificationIntentService.number_of_in_front_cards = DbHelper.getInstance(context).findInFrontCardsNum();
                    Log.i(getClass().getSimpleName(), "onReceive from alarm, starting notification service");
                    if (NotificationIntentService.number_of_in_front_cards > 0)
                        serviceIntent = NotificationIntentService.createIntentStartNotificationServiceRemind(context);

                } else if (ACTION_DELETE_NOTIFICATION_REMIND.equals(action)) {
                    NotificationIntentService.number_of_in_front_cards = DbHelper.getInstance(context).findInFrontCardsNum();
                    Log.i(getClass().getSimpleName(), "onReceive delete notification action, starting notification service to handle delete");
                    serviceIntent = NotificationIntentService.createIntentDeleteNotificationDelete(context);
                }

                if (ACTION_START_NOTIFICATION_SERVICE_SYNC.equals(action)) {
                    Log.i(getClass().getSimpleName(), "onReceive from alarm, starting notification service");
                    if (DateTimeUtils.daysDifference(handler.getLastSyncDate(), DateTimeUtils.getNow()) > 2)
                        serviceIntent = NotificationIntentService.createIntentStartNotificationServiceSync(context);

                } else if (ACTION_DELETE_NOTIFICATION_SYNC.equals(action)) {
                    Log.i(getClass().getSimpleName(), "onReceive delete notification action, starting notification service to handle delete");
                    serviceIntent = NotificationIntentService.createIntentDeleteNotificationSync(context);
                }

                if (ACTION_START_NOTIFICATION_SERVICE_SERVER.equals(action)) {
                    Log.i(getClass().getSimpleName(), "onReceive from alarm, starting notification service");
                    serviceIntent = NotificationIntentService.createIntentStartNotificationServiceServer(context);

                } else if (ACTION_DELETE_NOTIFICATION_SERVER.equals(action)) {
                    Log.i(getClass().getSimpleName(), "onReceive delete notification action, starting notification service to handle delete");
                    serviceIntent = NotificationIntentService.createIntentDeleteNotificationServer(context);
                }

                if (serviceIntent != null) {
                    startWakefulService(context, serviceIntent);
                }
            }
        }catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
    }
}
