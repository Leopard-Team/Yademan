package ir.fekrafarinan.yademman.Leitner.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.R;

public class SignUpActivity extends AppCompatActivity {
    String result = null;
    EditText phoneNumber;
    TextView phoneNumberTv;
    Button signUp, goLogin, goEmailSignUp;
    private static final String PASSWORD_PATTERN =
            ".*";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (checkInputs()) {
                    if (Connection.isConnected(SignUpActivity.this)) {
                        sendSms();
                    } else
                        showConnectivityDialog();
                }
            }
        });
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
        goEmailSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, EmailSignUpActivity.class));
                finish();
            }
        });
    }

    private boolean checkInputs() {
        String number = phoneNumber.getText().toString();
        if (!number.startsWith("09")){
            Toast.makeText(this, getString(R.string.not_start_with_09), Toast.LENGTH_LONG).show();
            return false;
        }if (!(number.length() == 11)){
            Toast.makeText(this, getString(R.string.number_length_not_valid), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void sendSms() {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phoneNumber.getText().toString());
        new Connection(getString(R.string.base_url) + "sms.php",
                params, ConnectionUi.getDefault(this)) {
            @Override
            protected void onResult(String result) {
                Log.i("sendSms/result", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("success")){
                        Intent intent = new Intent(SignUpActivity.this, VerificationActivity.class);
                        intent.putExtra("code", jsonObject.getString("code"));
                        intent.putExtra("line_number", jsonObject.getString("line_number"));
                        intent.putExtra("phoneNumber", phoneNumber.getText().toString());
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_from_left);
                    }else {
                        Toast.makeText(SignUpActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    closeKeyboard();
                    phoneNumber.setText("");
                    Log.e("Exception", e.getMessage());
                    Toast.makeText(SignUpActivity.this, "خطا", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void showConnectivityDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this, R.style.DialogTheme);
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
            InputMethodManager imm = (InputMethodManager) getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initViews() {
        phoneNumber = (EditText) findViewById(R.id.editPhoneNumber);
        signUp = (Button) findViewById(R.id.btnSignUp);
        goLogin = (Button) findViewById(R.id.btnGoLogin);
        goEmailSignUp = (Button) findViewById(R.id.btnEmailSignup);
        phoneNumberTv = (TextView) findViewById(R.id.txtPhoneNumber);
        //Setting Fonts
        ((TextView) findViewById(R.id.txtSignUp)).setTypeface(TypeFaceHandler.sultanBold);
        phoneNumberTv.setTypeface(TypeFaceHandler.sultanLight);
        ((Button) findViewById(R.id.btnSignUp)).setTypeface(TypeFaceHandler.sultanBold);
        ((Button) findViewById(R.id.btnGoLogin)).setTypeface(TypeFaceHandler.bYekanLight);
        ((Button) findViewById(R.id.btnEmailSignup)).setTypeface(TypeFaceHandler.bYekanLight);

    }

//    private boolean checkInputs() {
//        Pattern p = Pattern.compile(PASSWORD_PATTERN);
//        Matcher m = p.matcher(pass.getText().toString());
//        if (verify.getText().length() < 4 || verify.getText().toString().length() > 50) {
//            Toast.makeText(SignUpActivity.this, "طول نام کاربری باید بین 4 تا 50 کاراکتر باشد", Toast.LENGTH_SHORT).show();
//            verify.setText(null);
//            verify.requestFocus();
//            pass.setText(null);
//            return false;
//        } else if (!m.matches()) {
//            pass.setText(null);
//            Toast.makeText(SignUpActivity.this, "رمز عبور باید شامل یک عدد و یک حرف بزرگ و یک علامت باشد", Toast.LENGTH_SHORT).show();
//            pass.requestFocus();
//            return false;
//        } else if (pass.getText().toString().length() > 50) {
//            pass.setText(null);
//            Toast.makeText(SignUpActivity.this, "رطول رمز عبور از پنجاه کاراکتر بیشتر نمیتواند باشد", Toast.LENGTH_SHORT).show();
//            pass.requestFocus();
//            return false;
//        }
//        return true;
//    }

    private void signUp(String U, String P, String E) {
        HashMap<String, String> params = new HashMap<>();
        params.put("ESignUp", E);
        params.put("PSignUp", P);
        params.put("USignUp", U);
        Connection connection = new Connection(getString(R.string.base_url) + "signup.php",
                params, ConnectionUi.getDefault(SignUpActivity.this)) {
            @Override
            protected void onResult(String result) {
                Log.i("result", result);
                if (result.startsWith("1")) {
                    Toast.makeText(SignUpActivity.this, R.string.success_sign_up, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, R.string.failed_sign_up, Toast.LENGTH_SHORT).show();
                }
            }
        };
        connection.execute();
    }
}

