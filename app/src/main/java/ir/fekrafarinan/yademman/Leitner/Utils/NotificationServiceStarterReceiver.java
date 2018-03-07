package ir.fekrafarinan.yademman.Leitner.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationServiceStarterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationEventReceiver.setupAlarmRemind(context);
        NotificationEventReceiver.setupAlarmSync(context);
        NotificationEventReceiver.setupAlarmServer(context);
    }
}
