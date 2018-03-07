package ir.fekrafarinan.yademman.Leitner.Ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class ConnectionUi {

    private Context context;
    private ProgressDialog dialog;

    public ConnectionUi(Context context) {
        this.context = context;
    }

    public abstract void start();

    public abstract void update(Integer... values);

    public abstract void end();

    public void defaultStart() {
        dialog = new DialogHandler(context).getInstanceProgress();
        dialog.show();
    }

    public void defaultEnd() {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
    }

    public void defaultUpdate(Integer... values) {
        dialog.setMessage("در حال دانلود دیتابیس..." + values[0] + "%");
    }

    public void defaultCancel() {
        dialog.cancel();
    }

    public void setOnCancelDialog(DialogInterface.OnCancelListener canceler) {
        if (dialog != null)
            dialog.setOnCancelListener(canceler);
    }

    public static ConnectionUi getDefault(Context context) {
        ConnectionUi output = new ConnectionUi(context) {
            @Override
            public void start() {
                defaultStart();
            }

            @Override
            public void update(Integer... values) {
                defaultUpdate(values);
            }

            @Override
            public void end() {
                defaultEnd();
            }
        };
        return output;
    }

    public static ConnectionUi noneUi(Context context) {
        ConnectionUi output = new ConnectionUi(context) {
            @Override
            public void start() {
            }

            @Override
            public void update(Integer... values) {
            }

            @Override
            public void end() {
            }
        };
        return output;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
