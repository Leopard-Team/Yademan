package ir.fekrafarinan.yademman.Leitner.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import cn.iwgang.countdownview.CountdownView;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.R;

public class VerificationActivity extends AppCompatActivity {

    String verificationCode = "", line_number="";
    String phoneNumber = "";
    Button btnVerify, btnRetry, btnGoLogin, goEmailSignUp;
    EditText verifyCode;
    BroadcastReceiver smsReceiver;
    CountdownView countdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        verificationCode = getIntent().getStringExtra("code");
        line_number = getIntent().getStringExtra("line_number");
        Log.i("verifyCode", verificationCode);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        initViews();
        setFonts();
        setListeners();
//        setSmsReceiver();
        setTimer();
    }

    private void setFonts() {
        ((TextView) findViewById(R.id.txtVerify)).setTypeface(TypeFaceHandler.sultanBold);
        ((TextView) findViewById(R.id.txtVerifyCode)).setTypeface(TypeFaceHandler.sultanLight);
        btnVerify.setTypeface(TypeFaceHandler.sultanBold);
        btnGoLogin.setTypeface(TypeFaceHandler.bYekanLight);
        goEmailSignUp.setTypeface(TypeFaceHandler.bYekanLight);
    }

    private void setListeners() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCode();
            }
        });
        btnGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_from_left);
            }
        });
        goEmailSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerificationActivity.this, EmailSignUpActivity.class));
                finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_from_left);
            }
        });
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms();
            }
        });
    }

    private void checkCode() {
        if (verificationCode.equals(verifyCode.getText().toString())) {
            closeKeyboard();
            Intent intent = new Intent(this, SignUpDetailsActivity.class);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_from_left);
        } else {
            Toast.makeText(this, getString(R.string.wrong_verify_code), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnRetry = (Button) findViewById(R.id.btn_retry);
        btnGoLogin = (Button) findViewById(R.id.btnGoLogin);
        goEmailSignUp = (Button) findViewById(R.id.btnEmailSignup);
        verifyCode = (EditText) findViewById(R.id.editVerify);
        countdownView = (CountdownView) findViewById(R.id.cv_timer);

    }

    private void setTimer() {
        new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                btnRetry.setVisibility(View.VISIBLE);
            }
        }.start();
        countdownView.start(120000);
    }

    private void setSmsReceiver() {
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                    Bundle bundle = intent.getExtras();
                    SmsMessage[] smsMessages = null;
                    String msg_from = "";
                    String msgBody = "";
                    if (bundle != null) {
                        //---retrieve the SMS message received---
                        try {
                            Object[] pdus = (Object[]) bundle.get("pdus");
                            smsMessages = new SmsMessage[pdus.length];
                            for (int i = 0; i < smsMessages.length; i++) {
                                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                                msg_from = smsMessages[i].getOriginatingAddress();
                                Log.i("msg_from", msg_from);
                                msgBody = smsMessages[i].getMessageBody();
                                Log.i("msgBody", msgBody);
                                if (msg_from.equals(line_number)) {
                                    String msgWithoutTxt = msgBody.replaceAll("[^0-9]+", " ");
                                    Log.i("msgWithoutTxt", msgWithoutTxt);
                                    String code = Arrays.asList(msgWithoutTxt.trim().split(" ")).get(0);
                                    Log.i("code", code);
                                    if (code.equals(verificationCode)) {
                                        verifyCode.setText(verificationCode);
                                        btnVerify.performClick();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Exception", e.getMessage());
                        }
                    }

                }
            }
        };
        registerReceiver(smsReceiver, filter);
    }

    private void sendSms() {
        if (Connection.isConnected(this)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("phone", phoneNumber);
            new Connection(getString(R.string.base_url) + "sms.php",
                    params, ConnectionUi.getDefault(this)) {
                @Override
                protected void onResult(String result) {
                    Log.i("sendSms/result", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getBoolean("success")) {
                            Intent intent = new Intent(VerificationActivity.this, VerificationActivity.class);
                            intent.putExtra("code", jsonObject.getString("code"));
                            intent.putExtra("line_number", jsonObject.getString("line_number"));
                            intent.putExtra("phoneNumber", phoneNumber);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_from_left);
                        } else {
                            Log.d("server error", jsonObject.getString("message"));
                            Toast.makeText(VerificationActivity.this, "خطا در ارسال پیام", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("Exception", e.getMessage());
                        Toast.makeText(VerificationActivity.this, "خطا", Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        } else {
            showConnectivityDialog();
        }
    }

    private void showConnectivityDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VerificationActivity.this, R.style.DialogTheme);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (smsReceiver != null)
//            unregisterReceiver(smsReceiver);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(VerificationActivity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
