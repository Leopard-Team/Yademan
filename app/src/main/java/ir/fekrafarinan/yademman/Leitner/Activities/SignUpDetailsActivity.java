package ir.fekrafarinan.yademman.Leitner.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Models.Student;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.DateTimeUtils;
import ir.fekrafarinan.yademman.Leitner.Utils.NotificationEventReceiver;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class SignUpDetailsActivity extends AppCompatActivity {
    String result = null, phoneNumber = "";
    EditText username, pass, retryPass;
    Button signUp, goLogin, goEmailSignUp;
    private static final String PASSWORD_PATTERN =
            ".*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_details);
        closeKeyboard();
        initViews();
        setFonts();
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (Connection.isConnected(SignUpDetailsActivity.this)) {
                    if (checkInputs()) {
                        closeKeyboard();
                        signUp(username.getText().toString(), pass.getText().toString(), phoneNumber);
                        result = "";
                        username.setText(null);
                        pass.setText(null);
                        username.requestFocus();
                    }
                } else
                    showConnectivityDialog();
            }
        });
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpDetailsActivity.this, LoginActivity.class));
                finish();
            }
        });
        goEmailSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpDetailsActivity.this, EmailSignUpActivity.class));
                finish();
            }
        });
    }

    private void setFonts() {
        new TypeFaceHandler(getAssets());
        ((TextView) findViewById(R.id.txtSignUp)).setTypeface(TypeFaceHandler.sultanBold);
        ((TextView) findViewById(R.id.txtSignUpUserName)).setTypeface(TypeFaceHandler.sultanLight);
        ((TextView) findViewById(R.id.txtSignUpPassWord)).setTypeface(TypeFaceHandler.sultanLight);
        ((TextView) findViewById(R.id.txtSignUpRetryPassword)).setTypeface(TypeFaceHandler.sultanLight);
        goLogin.setTypeface(TypeFaceHandler.bYekanLight);
        signUp.setTypeface(TypeFaceHandler.sultanBold);

    }

    private void showConnectivityDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpDetailsActivity.this, R.style.DialogTheme);
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

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(SignUpDetailsActivity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initViews() {
        username = (EditText) findViewById(R.id.editUserName);
        pass = (EditText) findViewById(R.id.editPassword);
        retryPass = (EditText) findViewById(R.id.editRetryPassword);
        signUp = (Button) findViewById(R.id.btnSignUp);
        goLogin = (Button) findViewById(R.id.btnGoLogin);
        goEmailSignUp = (Button) findViewById(R.id.btnEmailSignup);
        //Setting Fonts
        ((TextView) findViewById(R.id.txtSignUp)).setTypeface(TypeFaceHandler.sultanBold);
        ((TextView) findViewById(R.id.txtSignUpUserName)).setTypeface(TypeFaceHandler.sultanLight);
        ((TextView) findViewById(R.id.txtSignUpRetryPassword)).setTypeface(TypeFaceHandler.sultanLight);
        ((TextView) findViewById(R.id.txtSignUpPassWord)).setTypeface(TypeFaceHandler.sultanLight);
        ((Button) findViewById(R.id.btnSignUp)).setTypeface(TypeFaceHandler.sultanLight);
        ((Button) findViewById(R.id.btnGoLogin)).setTypeface(TypeFaceHandler.bYekanLight);
        ((Button) findViewById(R.id.btnEmailSignup)).setTypeface(TypeFaceHandler.bYekanLight);

    }

    private boolean checkInputs() {
        Pattern p = Pattern.compile(PASSWORD_PATTERN);
        Matcher m = p.matcher(pass.getText().toString());
        if (username.getText().length() < 4 || username.getText().toString().length() > 50) {
            Toast.makeText(SignUpDetailsActivity.this, "طول نام کاربری باید بین 4 تا 50 کاراکتر باشد", Toast.LENGTH_SHORT).show();
            username.setText(null);
            username.requestFocus();
            pass.setText(null);
            return false;
        } else if (!m.matches()) {
            pass.setText(null);
            Toast.makeText(SignUpDetailsActivity.this, "رمز عبور باید شامل یک عدد و یک حرف بزرگ و یک علامت باشد", Toast.LENGTH_SHORT).show();
            pass.requestFocus();
            return false;
        } else if (pass.getText().toString().length() > 50) {
            pass.setText(null);
            Toast.makeText(SignUpDetailsActivity.this, "رطول رمز عبور از پنجاه کاراکتر بیشتر نمیتواند باشد", Toast.LENGTH_SHORT).show();
            pass.requestFocus();
            return false;
        } else if (!retryPass.getText().toString().equals(pass.getText().toString())) {
            retryPass.setText(null);
            pass.setText(null);
            Toast.makeText(SignUpDetailsActivity.this, "رمز عبور ها مطابقت ندارد", Toast.LENGTH_LONG).show();
            pass.requestFocus();
            return false;
        }
        return true;
    }

    private void signUp(final String U, final String P, String phoneNumber) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phoneNumber", phoneNumber);
        params.put("PSignUp", P);
        params.put("USignUp", U);
        Connection connection = new Connection(getString(R.string.base_url) + "signup.php",
                params, ConnectionUi.getDefault(SignUpDetailsActivity.this)) {
            @Override
            protected void onResult(String result) {
                if (result.startsWith("1")) {
                    Toast.makeText(SignUpDetailsActivity.this, R.string.success_sign_up, Toast.LENGTH_SHORT).show();
                    login(U, P);
                } else {
                    Toast.makeText(SignUpDetailsActivity.this, R.string.failed_sign_up, Toast.LENGTH_SHORT).show();
                }
            }
        };
        connection.execute();
    }

    private void login(String U, String P) {
        Log.i("logging in", "log in");
        HashMap<String, String> params = new HashMap<>();
        params.put("USER", U);
        params.put("PASS", P);
        final Connection connection = new Connection(getString(R.string.base_url) +
                "login.php", params, ConnectionUi.getDefault(SignUpDetailsActivity.this)) {
            @Override
            protected void onResult(String result) {
                Log.i("result/login", result);
                try {
                    JSONObject user = new JSONObject(result);

                    boolean isConnected = user.getBoolean("success");
                    if (isConnected) {
                        boolean isVerified = user.getInt("isVerified") == 1;
                        String username = user.getString("user_name");
                        String email = user.getString("email");
                        String token = user.getString("token");
                        int budget = user.getInt("budget");
                        JSONArray lessons = user.getJSONArray("lesson_id");
                        Student student = new Student(username, email,
                                DbHelper.getInstance(SignUpDetailsActivity.this), budget);
                        SharedPreferencesHandler userHelper = new SharedPreferencesHandler(getApplicationContext());
                        userHelper.write(student);
                        userHelper.setLessonIds(lessons.toString());
                        userHelper.setToken(token);
                        NotificationEventReceiver.setupAlarmRemind(SignUpDetailsActivity.this);
                        NotificationEventReceiver.setupAlarmSync(SignUpDetailsActivity.this);
                        NotificationEventReceiver.setupAlarmServer(SignUpDetailsActivity.this);
                        LoginActivity.setCookies(this.getConnection(), SignUpDetailsActivity.this);
                        new SharedPreferencesHandler(SignUpDetailsActivity.this).setLastSyncDate(DateTimeUtils.getNow());
                        Log.i("finishing", "finishing");
                        SignUpDetailsActivity.this.startActivity(new Intent(SignUpDetailsActivity.this, FirstPageActivity.class));
                        SignUpDetailsActivity.this.finish();
                        Snackbar.make(signUp, R.string.text_login_success, Snackbar.LENGTH_LONG).show();
                    } else
                        Snackbar.make(signUp, user.getString("message"), Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                }

            }
        };
        connection.execute();
    }
}
