package ir.fekrafarinan.yademman.Leitner.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.R;

public class EmailSignUpActivity extends AppCompatActivity {
    String result = null;
    EditText username, pass, email;
    Button signUp, goLogin;
    private static final String PASSWORD_PATTERN =
            ".*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);
        initViews();
        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isNetworkAvailable()) {
                    if (checkInputs()) {
                        closeKeyboard();
                        signUp(username.getText().toString(), pass.getText().toString(), email.getText().toString());
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
                startActivity(new Intent(EmailSignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void showConnectivityDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EmailSignUpActivity.this, R.style.DialogTheme);
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

    private void initViews() {
        username = (EditText) findViewById(R.id.editUserName);
        pass = (EditText) findViewById(R.id.editPassword);
        email = (EditText) findViewById(R.id.editEmail);
        signUp = (Button) findViewById(R.id.btnSignUp);
        goLogin = (Button) findViewById(R.id.btnGoLogin);
        //Setting Fonts
        ((TextView) findViewById(R.id.txtSignUp)).setTypeface(TypeFaceHandler.sultanBold);
        ((TextView) findViewById(R.id.txtSignUpEmail)).setTypeface(TypeFaceHandler.sultanLight);
        ((TextView) findViewById(R.id.txtSignUpUserName)).setTypeface(TypeFaceHandler.sultanLight);
        ((TextView) findViewById(R.id.txtSignUpPassWord)).setTypeface(TypeFaceHandler.sultanLight);
        ((Button) findViewById(R.id.btnSignUp)).setTypeface(TypeFaceHandler.sultanLight);
        ((Button) findViewById(R.id.btnGoLogin)).setTypeface(TypeFaceHandler.bYekanLight);
    }

    private boolean checkInputs() {
        Pattern p = Pattern.compile(PASSWORD_PATTERN);
        Matcher m = p.matcher(pass.getText().toString());
        if (username.getText().length() < 4 || username.getText().toString().length() > 50) {
            Toast.makeText(EmailSignUpActivity.this, "طول نام کاربری باید بین 4 تا 50 کاراکتر باشد", Toast.LENGTH_SHORT).show();
            username.setText(null);
            username.requestFocus();
            pass.setText(null);
            return false;
        } else if (!m.matches()) {
            pass.setText(null);
            Toast.makeText(EmailSignUpActivity.this, "رمز عبور باید شامل یک عدد و یک حرف بزرگ و یک علامت باشد", Toast.LENGTH_SHORT).show();
            pass.requestFocus();
            return false;
        } else if (pass.getText().toString().length() > 50) {
            pass.setText(null);
            Toast.makeText(EmailSignUpActivity.this, "رطول رمز عبور از پنجاه کاراکتر بیشتر نمیتواند باشد", Toast.LENGTH_SHORT).show();
            pass.requestFocus();
            return false;
        }
        return true;
    }

    private void signUp(String U, String P, String E) {
        HashMap<String, String> params = new HashMap<>();
        params.put("ESignUp", E);
        params.put("PSignUp", P);
        params.put("USignUp", U);
        Connection connection = new Connection(getString(R.string.base_url) + "email-signup.php",
                params, ConnectionUi.getDefault(EmailSignUpActivity.this)) {
            @Override
            protected void onResult(String result) {
                Log.i("result", result);
                if (result.startsWith("1")){
                    Toast.makeText(EmailSignUpActivity.this, R.string.success_sign_up, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EmailSignUpActivity.this, LoginActivity.class));
                    finish();
                }else {
                    Toast.makeText(EmailSignUpActivity.this, R.string.failed_sign_up_email, Toast.LENGTH_SHORT).show();
                }
            }
        };
        connection.execute();
    }
}

