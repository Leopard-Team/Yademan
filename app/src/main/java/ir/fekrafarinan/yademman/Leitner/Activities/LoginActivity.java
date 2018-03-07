package ir.fekrafarinan.yademman.Leitner.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Models.Student;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.Leitner.Utils.DateTimeUtils;
import ir.fekrafarinan.yademman.Leitner.Utils.NotificationEventReceiver;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class LoginActivity extends AppCompatActivity {
    private boolean isConnected, isVerified;
    private Student student;
    private EditText username, pass;
    private Button login, goSignUp, forgetPass;
    private static final String PASSWORD_PATTERN =
            ".*";
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WAKE_LOCK,
            "com.farsitel.bazaar.permission.PAY_THROUGH_BAZAAR"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkPermissions(permissions);
        SharedPreferencesHandler userHelper = new SharedPreferencesHandler(getApplicationContext());
        if (userHelper.doesContain()) {
            startActivity(new Intent(LoginActivity.this, FirstPageActivity.class));
            finish();
        }
        initViews();
        setListeners();
    }

    private void setListeners() {
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                closeKeyboard();
                if (isNetworkAvailable())
                    login(username.getText().toString(), pass.getText().toString());
                else
                    showConnectivityDialog();
            }
        });
        goSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        View view = this.getLayoutInflater().inflate(R.layout.dialog_forget_password, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this, R.style.DialogTheme);
        builder.setCancelable(true);
        ((TextView) view.findViewById(R.id.txtEmail)).setTypeface(TypeFaceHandler.sultanBold);
        ((TextView) view.findViewById(R.id.txtEmail)).setText(getString(R.string.email_or_phone));
        final EditText etEmail = (EditText) view.findViewById(R.id.etEmail);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok_Button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (etEmail.getText().toString().length() > 0) {
                    String email = etEmail.getText().toString();
                    sendForgetRequest(email);
                    dialogInterface.cancel();
                } else
                    Toast.makeText(LoginActivity.this, "تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(TypeFaceHandler.bYekanLight);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(TypeFaceHandler.bYekanLight);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            }
        });
        dialog.show();
    }

    private void sendForgetRequest(String email) {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        Connection connection = new Connection(getString(R.string.base_url) + "forgotPassword.php", params, ConnectionUi.noneUi(LoginActivity.this)) {
            @Override
            protected void onResult(String result) {
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        };
        try {
            connection.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void showConnectivityDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.DialogTheme);
        builder.setMessage(R.string.text_connect_failed);
        builder.setCancelable(true);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.show();
        TextView txtMessage = (TextView) alertDialog.findViewById(android.R.id.message);
        txtMessage.setTypeface(TypeFaceHandler.bYekanLight);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean hasPermissions(){
        for (String permission :
                permissions) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, permission) !=
                    PackageManager.PERMISSION_GRANTED &&
                    !permission.equals("com.farsitel.bazaar.permission.PAY_THROUGH_BAZAAR")) {
                Log.i("permission", permission);
                return false;
            }
        }
        return true;
    }

    private void checkPermissions(String[] permissions) {
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        for (String permission :
                permissions) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, permission) !=
                    PackageManager.PERMISSION_GRANTED)
                notGrantedPermissions.add(permission);
        }
        if (notGrantedPermissions.size() > 0)
            ActivityCompat.requestPermissions(this,
                    notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]),
                    1);
    }
    private void initViews() {
        username = (EditText) findViewById(R.id.editVerify);
        pass = (EditText) findViewById(R.id.editPassword);
        login = (Button) findViewById(R.id.btnLogin);
        goSignUp = (Button) findViewById(R.id.btnGoSignUp);
        forgetPass = (Button) findViewById(R.id.btnForgetPassword);
        //Setting Fonts
        new TypeFaceHandler(getApplicationContext().getAssets());
        ((TextView) findViewById(R.id.txtSignUp)).setTypeface(TypeFaceHandler.sultanBold);
        ((TextView) findViewById(R.id.txtLoginUserName)).setTypeface(TypeFaceHandler.sultanLight);
        ((TextView) findViewById(R.id.txtLoginPassWord)).setTypeface(TypeFaceHandler.sultanLight);
        ((Button) findViewById(R.id.btnLogin)).setTypeface(TypeFaceHandler.sultanBold);
        ((Button) findViewById(R.id.btnGoSignUp)).setTypeface(TypeFaceHandler.bYekanLight);
        forgetPass.setTypeface(TypeFaceHandler.bYekanLight);
    }

    private void login(String U, String P) {
        HashMap<String, String> params = new HashMap<>();
        params.put("USER", U);
        params.put("PASS", P);
        final Connection connection = new Connection(getString(R.string.base_url) + "login.php", params, ConnectionUi.getDefault(LoginActivity.this)) {
            @Override
            protected void onResult(String result) {
                try {
                    JSONObject user = new JSONObject(result);

                    isConnected = user.getBoolean("success");
                    if (isConnected) {
                        isVerified = user.getInt("isVerified") == 1;
                        if (isVerified) {
                            String username = user.getString("user_name");
                            String email = user.getString("email");
                            String token = user.getString("token");
                            int budget = user.getInt("budget");
                            JSONArray lessons = user.getJSONArray("lesson_id");
                            student = new Student(username, email,
                                    DbHelper.getInstance(LoginActivity.this), budget);
                            SharedPreferencesHandler userHelper = new SharedPreferencesHandler(getApplicationContext());
                            userHelper.write(student);
                            userHelper.setLessonIds(lessons.toString());
                            userHelper.setToken(token);
                            NotificationEventReceiver.setupAlarmRemind(LoginActivity.this);
                            NotificationEventReceiver.setupAlarmSync(LoginActivity.this);
                            NotificationEventReceiver.setupAlarmServer(LoginActivity.this);
                            setCookies(this.getConnection(), LoginActivity.this);
                            new SharedPreferencesHandler(LoginActivity.this).setLastSyncDate(DateTimeUtils.getNow());
                            LoginActivity.this.startActivity(new Intent(LoginActivity.this, FirstPageActivity.class));
                            LoginActivity.this.finish();
                            Snackbar.make(login, R.string.text_login_success, Snackbar.LENGTH_LONG).show();
                        }else {
                            Snackbar.make(login, getString(R.string.email_verify), Snackbar.LENGTH_LONG).show();
                        }
                    } else
                        Snackbar.make(login, user.getString("message"), Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                }

            }
        };

        if (checkInputs()) {
            connection.execute();
            if (isConnected) {
                username.setText(null);
                pass.setText(null);
                username.requestFocus();
            } else {
                username.setText(null);
                pass.setText(null);
                username.requestFocus();
            }
        }
    }

    public static void setCookies(HttpURLConnection connection, Context context) {
        final String COOKIES_HEADER = "Set-Cookie";
        HomeActivity.cookieManager = new CookieManager();

        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                HomeActivity.cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }
        SharedPreferencesHandler handler = new SharedPreferencesHandler(context);
        handler.setCookie(TextUtils.join(";", HomeActivity.cookieManager.getCookieStore().getCookies()));
        handler.setHasCookie(true);
    }


    private boolean checkInputs() {
        Pattern p = Pattern.compile(PASSWORD_PATTERN);
        Matcher m = p.matcher(pass.getText().toString());
        if (username.getText().length() < 4) {
            Toast.makeText(LoginActivity.this, "Invalid UserName", Toast.LENGTH_SHORT).show();
            username.setText(null);
            username.requestFocus();
            pass.setText(null);
            return false;
        } else if (!m.matches()) {
            pass.setText(null);
            Toast.makeText(LoginActivity.this, "Invalid PassWord", Toast.LENGTH_SHORT).show();
            pass.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!hasPermissions()){
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(LoginActivity.this, R.string.dont_have_permission, Toast.LENGTH_LONG).show();
                }
            };
            login.setOnClickListener(onClickListener);
            goSignUp.setOnClickListener(onClickListener);
            forgetPass.setOnClickListener(onClickListener);
            Snackbar.make(login, "لطفا دسترسی های لازم را به برنامه بدهید", Snackbar.LENGTH_INDEFINITE).setAction("دادن دسترسی ها", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermissions(permissions);
                }
            }).show();
        }else {
            setListeners();
        }
    }
}
