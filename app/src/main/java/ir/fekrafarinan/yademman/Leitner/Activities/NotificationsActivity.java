package ir.fekrafarinan.yademman.Leitner.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Models.Notification;
import ir.fekrafarinan.yademman.Leitner.Ui.NotificationsRecyclerViewAdapter;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.NotificationIntentService;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewNotifs;
    private static NotificationsRecyclerViewAdapter adapter;
    private static TextView txtNoNotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        findVies();
        new SharedPreferencesHandler(this).setNotificationsNumber(0);
        NotificationIntentService.clearShortcutbadger(this);

    }

    private void findVies() {
        recyclerViewNotifs = (RecyclerView) findViewById(R.id.notificationsRecyclerView);
        txtNoNotif = (TextView) findViewById(R.id.txtNoNotif);
        ArrayList<Notification> notifications = DbHelper.getInstance(this).getAllNotifications();
        adapter = new NotificationsRecyclerViewAdapter(notifications, this);
        recyclerViewNotifs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotifs.setAdapter(adapter);
        txtNoNotif.setTypeface(TypeFaceHandler.sultanBold);
        checkEmptyNotifs();
    }

    public static void checkEmptyNotifs() {
        if (adapter.getItemCount() == 0)
            txtNoNotif.setVisibility(View.VISIBLE);
        else
            txtNoNotif.setVisibility(View.INVISIBLE);

    }
}
