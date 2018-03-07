package ir.fekrafarinan.yademman.Leitner.Ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import ir.fekrafarinan.yademman.Leitner.Activities.NotificationsActivity;
import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Models.Notification;
import ir.fekrafarinan.yademman.Leitner.Utils.DateTimeUtils;
import ir.fekrafarinan.yademman.R;

/**
 * Created by Mohammad on 9/10/2017.
 */

public class NotificationsRecyclerViewAdapter extends RecyclerView.Adapter<NotificationsRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Notification> notifications;
    private Context context;

    public NotificationsRecyclerViewAdapter(ArrayList<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, message;
        ImageButton delete;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            delete = (ImageButton) itemView.findViewById(R.id.btnDeleteNotif);
            title = (TextView) itemView.findViewById(R.id.txtNotifTitle);
            date = (TextView) itemView.findViewById(R.id.txtNotifDate);
            message = (TextView) itemView.findViewById(R.id.txtNotifMessage);
            title.setTypeface(TypeFaceHandler.sultanBold);
            date.setTypeface(TypeFaceHandler.sultanLight);
            message.setTypeface(TypeFaceHandler.bYekanLight);
            this.itemView = itemView;
        }
    }

    @Override
    public NotificationsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notificatopm_recycler_view_item, parent, false);
        return new NotificationsRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationsRecyclerViewAdapter.MyViewHolder holder, final int position) {
        Notification notif = notifications.get(position);
        holder.title.setText(notif.getTitle());
        holder.message.setText(notif.getMessage());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper.getInstance(context).deleteNotification(notifications.get(position).getId());
                notifications.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, notifications.size());
                NotificationsActivity.checkEmptyNotifs();
            }
        });
        holder.date.setText(DateTimeUtils.dateToString(notif.getDate()));

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


}
