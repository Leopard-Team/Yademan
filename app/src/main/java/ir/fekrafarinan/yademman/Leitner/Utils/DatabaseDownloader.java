package ir.fekrafarinan.yademman.Leitner.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.ExecutionException;

import ir.fekrafarinan.yademman.Leitner.Activities.HomeActivity;
import ir.fekrafarinan.yademman.Leitner.Database.DataSyncer;
import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Database.OnDataSyncEnd;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.R;

public abstract class DatabaseDownloader {

    private Context context;
    private File filePath;
    private Activity activity;


    public DatabaseDownloader(Context context, File filePath, Activity activity) {
        this.context = context;
        this.filePath = filePath;
        this.activity = activity;
    }


    public void start(final ConstraintLayout constraintLayout) {
        final String serverVersion = getDatabaseVersion();
        if (!filePath.exists()) {
            Log.i("file not exist", "here");
            download();
            new SharedPreferencesHandler(context).setDatabaseVersion(serverVersion.split("\n")[0]);
        } else if (!isDatabaseUpToDate(serverVersion)) {
            makeUpdateSnackBar(constraintLayout, serverVersion);
        } else {
            onEnd("file exist");
        }
    }

    private void makeUpdateSnackBar(final ConstraintLayout constraintLayout, final String serverVersion) {
        Snackbar.make(constraintLayout, "ورژن دیتابیس قدیمیست لطفا آپدیت کنید", Snackbar.LENGTH_INDEFINITE).setAction("آپدیت", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Connection.isConnected(context)) {
                    DataSyncer.getInstance(context).syncWithServer(new OnDataSyncEnd() {
                        @Override
                        public void onEnd() {
                            context.getApplicationContext().getDatabasePath("database.db").delete();
                            downloadUpdateVersion();
                            new SharedPreferencesHandler(context).setDatabaseVersion(serverVersion.split("\n")[0]);
                            Log.i("DATABASE", "DATABASE VERSION CHANGED");
                        }
                    });
                }else {
                    Toast.makeText(context, "لطفا به اینترنت وصل شوید", Toast.LENGTH_SHORT).show();
                    makeUpdateSnackBar(constraintLayout, serverVersion);
                }
            }
        }).show();
    }

    private void downloadUpdateVersion() {
        Log.i("Here", "deleted file");
        if (Connection.isConnected(context)) {
            Log.i("downloading", filePath.getAbsolutePath());
            Log.i("isFileExist", filePath.exists()+"");
            FileDownloader downloader = new FileDownloader(filePath,
                    context.getString(R.string.base_url) + "database/database.db",
                    ConnectionUi.getDefault(context)) {
                @Override
                protected void onResult(String result) {
                    DbHelper.update(context);
                    DataSyncer.getInstance(context).syncWithServer(new OnDataSyncEnd() {
                        @Override
                        public void onEnd() {
                            context.startActivity(new Intent(context, HomeActivity.class));
                        }
                    });
                }
            };
            downloader.execute();
        } else {
            if (activity instanceof HomeActivity)
                ((HomeActivity) activity).retryDownload();
        }
    }

    public boolean isDatabaseUpToDate(String version) {
        String serverVersion = version.split("\n")[0];
        String localVersion = new SharedPreferencesHandler(context).getDatabaseVersion().split("\n")[0];
        Log.i("server" + serverVersion, "local" + localVersion);
        if (version.equals("notConnected"))
            return true;
        return (serverVersion.startsWith(localVersion));
    }

    private String getDatabaseVersion() {
        String version = "";
        Connection connection = new Connection(
                context.getString(R.string.base_url) + "database/version.php", null, ConnectionUi.getDefault(context)) {
            @Override
            protected void onResult(String result) {
                Log.d("getDatabaseVersion", result);
            }
        };
        try {
            version = connection.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return version;

    }


    private void download() {
        if (Connection.isConnected(context)) {
            Log.i("downloading", "downloading");
            FileDownloader downloader = new FileDownloader(filePath,
                    context.getString(R.string.base_url) + "database/database.db",
                    ConnectionUi.getDefault(context)) {
                @Override
                protected void onResult(String result) {
                    onEnd(result);
                }
            };
            downloader.execute();
        } else {
            if (activity instanceof HomeActivity)
                ((HomeActivity) activity).retryDownload();
        }
    }

    public static boolean isDataBaseExist(Context context) {
        return context.getDatabasePath("database.db").exists();
    }

    protected abstract void onEnd(String result);

}
