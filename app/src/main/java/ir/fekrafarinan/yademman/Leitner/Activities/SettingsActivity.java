package ir.fekrafarinan.yademman.Leitner.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ir.fekrafarinan.yademman.Leitner.Database.DataSyncer;
import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Database.OnDataSyncEnd;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Ui.DialogHandler;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnChangeEmail, btnChangePassword, btnDeleteAccount, btnLogOut;
    private HashMap<String, String> params = new HashMap<>();
    private EditText etNewPass, etNewPassVerify, etOldPass, etPass, etNewEmail;
    private CheckBox hasToSavePhoto, hasToNotification;
    private TextView txtChangeFontSize, txtFontSize;
    private SeekBar seekBarFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViews();
        setListeners();
        setFonts();
    }

    private void setFonts() {
        btnChangeEmail.setTypeface(TypeFaceHandler.bYekanLight);
        btnChangePassword.setTypeface(TypeFaceHandler.bYekanLight);
        btnDeleteAccount.setTypeface(TypeFaceHandler.bYekanLight);
        btnLogOut.setTypeface(TypeFaceHandler.bYekanLight);
        txtChangeFontSize.setTypeface(TypeFaceHandler.bYekanLight);
        txtFontSize.setTypeface(TypeFaceHandler.bYekanLight);
        hasToSavePhoto.setTypeface(TypeFaceHandler.bYekanLight);
        hasToNotification.setTypeface(TypeFaceHandler.bYekanLight);
    }

    private void findViews() {
        btnChangeEmail = (Button) findViewById(R.id.btnChangeEmail);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnDeleteAccount = (Button) findViewById(R.id.btnDeleteAccount);
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        hasToSavePhoto = (CheckBox) findViewById(R.id.checkSavePhoto);
        hasToNotification = (CheckBox) findViewById(R.id.checkNotification);
        txtChangeFontSize = (TextView) findViewById(R.id.txtChangeFontSize);
        txtFontSize = (TextView) findViewById(R.id.txtFontSize);
        seekBarFontSize = (SeekBar) findViewById(R.id.seekBarFontSize);
        SharedPreferences preferences = this.getSharedPreferences("shared", MODE_PRIVATE);
        hasToSavePhoto.setChecked(preferences.getBoolean("hasSavePhoto", false));
        hasToNotification.setChecked(new SharedPreferencesHandler(SettingsActivity.this).hasToNotification());
        seekBarFontSize.setProgress(preferences.getInt("fontSize", 18));
    }

    private void setListeners() {
        btnChangeEmail.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
        btnDeleteAccount.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        hasToSavePhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("shared", Context.MODE_PRIVATE).edit();
                editor.putBoolean("hasSavePhoto", b);
                editor.apply();
            }
        });
        hasToNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                new SharedPreferencesHandler(SettingsActivity.this).setHasToNotitfication(b);
            }
        });
        seekBarFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int size = i * 35 / 100 + 15;
                txtFontSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int size = seekBar.getProgress() * 35 / 100 + 15;
                SharedPreferences.Editor editor = getSharedPreferences("shared", Context.MODE_PRIVATE).edit();
                editor.putInt("fontSize", size);
                editor.apply();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChangeEmail:
                showDialogNewEmail();
                break;
            case R.id.btnChangePassword:
                showDialogNewPass();
                break;
            case R.id.btnDeleteAccount:
                showDialogDeleteAccount();
                break;
            case R.id.btnLogOut:
                showDialogLogOut();
                break;
        }
    }

    private void showDialogLogOut() {
        new DialogHandler(this).showAlert(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DataSyncer.getInstance(SettingsActivity.this).syncWithServer(new OnDataSyncEnd() {
                    @Override
                    public void onEnd() {
                        logOut();
                    }
                });
            }
        }, "آیا مایل به هماهنگ سازی اطلاعات با سرور قبل از خروج هستید؟", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logOut();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void logOut() {
        SharedPreferencesHandler handler = new SharedPreferencesHandler(getApplicationContext());
        handler.delete();
        handler.setHasCookie(false);
        DbHelper dbHelper = DbHelper.getInstance(SettingsActivity.this);
        ArrayList<Integer> boughtLessonIds = handler.getLessonIds();
        for (int id :
                boughtLessonIds) {
            dbHelper.setLessonBought(false, id);
        }
        handler.setLessonIds("[]");
        dbHelper.deleteAllStudentCards();
        dbHelper.deleteAllQueue();
        dbHelper.deleteAllTicks();
        Intent mStartActivity = new Intent(SettingsActivity.this, LoginActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(SettingsActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private void showDialogDeleteAccount() {
        View view = this.getLayoutInflater().inflate(R.layout.dialog_layout_delete_account, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.DialogTheme);
        builder.setCancelable(true);
        ((TextView) view.findViewById(R.id.txtOldPass)).setTypeface(TypeFaceHandler.sultanBold);
        etPass = (EditText) view.findViewById(R.id.etOldPass);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok_Button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (etPass.getText().toString().length() > 0) {
                    String pass = etPass.getText().toString();
                    deleteInServer(pass);
                    dialogInterface.cancel();
                } else
                    Toast.makeText(SettingsActivity.this, "تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
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

    private void deleteInServer(String pass) {
        params.put("password", pass);
        params.put("verify", HomeActivity.student.getUserName());
        params.put(HomeActivity.student.getUserName(), new SharedPreferencesHandler(this).getToken());
        Connection connectionDelete = new Connection(getString(R.string.base_url) + "deleteStudent.php",
                params, ConnectionUi.getDefault(this)) {
            @Override
            protected void onResult(String result) {
                Snackbar.make(btnChangePassword, result, Snackbar.LENGTH_LONG).show();
                logOut();
            }
        };
        connectionDelete.execute();
    }

    private void showDialogNewPass() {
        View view = this.getLayoutInflater().inflate(R.layout.dialog_layout_change_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.DialogTheme);
        builder.setCancelable(true);
        ((TextView) view.findViewById(R.id.txtOldPass)).setTypeface(TypeFaceHandler.sultanBold);
        ((TextView) view.findViewById(R.id.txtNewPass)).setTypeface(TypeFaceHandler.sultanBold);
        ((TextView) view.findViewById(R.id.txtNewPassVerify)).setTypeface(TypeFaceHandler.sultanBold);
        etOldPass = (EditText) view.findViewById(R.id.etOldPass);
        etNewPass = (EditText) view.findViewById(R.id.etNewPass);
        etNewPassVerify = (EditText) view.findViewById(R.id.etNewPassVerify);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok_Button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (etNewPass.getText().toString().length() > 0 && etNewPassVerify.getText().toString().length() > 0 && etOldPass.getText().toString().length() > 0) {
                    final String newPass = etNewPass.getText().toString();
                    String newPassVerify = etNewPassVerify.getText().toString();
                    final String oldPass = etOldPass.getText().toString();
                    if (newPassVerify.equals(newPass)) {
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updatePasswordInServer(oldPass, newPass);
                            }
                        };
                        new DialogHandler(SettingsActivity.this).showAlert(listener);
                    }

                } else
                    Toast.makeText(SettingsActivity.this, "تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
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

    private void showDialogNewEmail() {
        View view = this.getLayoutInflater().inflate(R.layout.dialog_layout_change_email, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.DialogTheme);
        builder.setCancelable(true);
        ((TextView) view.findViewById(R.id.txtNewEmail)).setTypeface(TypeFaceHandler.sultanBold);
        ((TextView) view.findViewById(R.id.txtOldPass)).setTypeface(TypeFaceHandler.sultanBold);
        etPass = (EditText) view.findViewById(R.id.etOldPass);
        etNewEmail = (EditText) view.findViewById(R.id.etNewEmail);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok_Button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (etPass.getText().toString().length() > 0 && etNewEmail.getText().toString().length() > 0) {
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String pass = etPass.getText().toString();
                            String newEmail = etNewEmail.getText().toString();
                            dialogInterface.cancel();
                            updateEmailInServer(pass, newEmail);
                        }
                    };
                    new DialogHandler(SettingsActivity.this).showAlert(listener);

                } else
                    Toast.makeText(SettingsActivity.this, "تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
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

    private void updateEmailInServer(String pass, String newEmail) {
        params.put("verify", HomeActivity.student.getUserName());
        params.put("newEmail", newEmail);
        params.put("password", pass);
        params.put(HomeActivity.student.getUserName(), new SharedPreferencesHandler(this).getToken());
        Connection connectionEmail = new Connection(getString(R.string.base_url) + "edit.php", params, ConnectionUi.getDefault(this)) {
            @Override
            protected void onResult(String result) {
                Boolean success = false;
                try {
                    JSONObject res = new JSONObject(result);
                    success = res.getBoolean("res");
                    String message = res.getString("message");
                    Log.i("UPDATE MESSAGE", message);
                    Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        connectionEmail.execute();
//        showDialogNewEmail();
    }

    private void updatePasswordInServer(String oldPass, String newPass) {
        params.put("newPassword", newPass);
        params.put("oldPassword", oldPass);
        params.put("verify", HomeActivity.student.getUserName());
        params.put(HomeActivity.student.getUserName(), new SharedPreferencesHandler(this).getToken());
        Connection connectionPassword = new Connection(getString(R.string.base_url) + "edit.php", params, ConnectionUi.getDefault(this)) {
            @Override
            protected void onResult(String result) {
                try {
                    JSONObject res = new JSONObject(result);
                    String message = res.getString("message");
                    Snackbar.make(btnChangePassword, message, Snackbar.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        connectionPassword.execute();
    }

}