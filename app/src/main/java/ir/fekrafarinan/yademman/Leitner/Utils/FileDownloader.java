package ir.fekrafarinan.yademman.Leitner.Utils;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;

public abstract class FileDownloader extends AsyncTask<Void, Integer, String> {

    protected ConnectionUi connectionUi;
    private File filePath;
    private String urlPath;

    public FileDownloader(File filePath, String urlPath, ConnectionUi connectionUi) {
        this.filePath = filePath;
        this.urlPath = urlPath;
        this.connectionUi = connectionUi;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        connectionUi.start();
        connectionUi.setOnCancelDialog(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                cancel(true);
                Log.e("ERROR DOWNLOADING FILE", "DOWNLOADING CANCELED");
            }
        });
    }


    @Override
    protected String doInBackground(Void... params) {
        InputStream input;
        OutputStream output;
        HttpURLConnection connection = null;
        try {
            filePath.getParentFile().mkdirs();
//            filePath.createNewFile();
            URL url = new URL(urlPath);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            int fileLength = connection.getContentLength();
            input = connection.getInputStream();
            output = new FileOutputStream(filePath);
            byte data[] = new byte[300000];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    Log.e("DOWNLOAD", "CONNECTION LOST");
                    connectionUi.defaultCancel();
                    input.close();
                    return null;
                }
                total += count;
                if (fileLength > 0)
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
        } catch (IOException e) {
            Log.e("Exception DOWNLOAD", e.getMessage());
            return null;
        }
        return "success";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        connectionUi.update(values);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result == null) {
            if (connectionUi.getContext().getApplicationContext().getDatabasePath("database.db").delete())
                Log.i("DELETE FILE", "FILE DELETED HERE");
        }
        connectionUi.end();
        onResult(result);
    }

    protected abstract void onResult(String result);
}
