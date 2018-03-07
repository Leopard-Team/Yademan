package ir.fekrafarinan.yademman.Leitner.Utils;


import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;

public abstract class Connection extends AsyncTask<Void, Void, String> {

    private String urlString;
    private ConnectionUi connectionUi;
    private Map<String, String> params = new HashMap<>();
    private HttpURLConnection connection;

    public Connection(String urlString, Map<String, String> params, ConnectionUi connectionUi) {
        this.connectionUi = connectionUi;
        this.urlString = urlString;
        this.params = params;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    @Override
    protected void onPreExecute() {
        connectionUi.start();
        connectionUi.setOnCancelDialog(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                cancel(true);
            }
        });
    }

    @Override
    protected String doInBackground(Void... voids) {
        BufferedReader reader = null;
        String result = "notConnected";
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(50000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            SharedPreferencesHandler handler = new SharedPreferencesHandler(connectionUi.getContext());
            if (handler.hasCookie()) {
                connection.setRequestProperty("Cookie",
                        handler.getCookie());
            }
            if (params != null) {
                Uri.Builder builder = new Uri.Builder();
                builder = appendQueryParameters(builder);
                String query = builder.build().getEncodedQuery();
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line).append("\n");
            result = sb.toString();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage(), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                Log.d("Exception", e.getMessage(), e);
            }
        }
        endProcess(result);
        return result;
    }

    public void endProcess(String result) {

    }

    public Uri.Builder appendQueryParameters(Uri.Builder builder) {
        for (String key :
                params.keySet()) {
            builder = builder.appendQueryParameter(key, params.get(key));
        }
        return builder;
    }

    @Override
    protected void onPostExecute(String result) {
        onResult(result);
        connectionUi.end();
    }

    protected abstract void onResult(String result);

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void setConnection(HttpsURLConnection connection) {
        this.connection = connection;
    }

}
