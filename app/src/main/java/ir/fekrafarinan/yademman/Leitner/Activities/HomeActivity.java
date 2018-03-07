package ir.fekrafarinan.yademman.Leitner.Activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.net.CookieManager;

import ir.fekrafarinan.yademman.Leitner.Database.DataSyncer;
import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Database.OnDataSyncEnd;
import ir.fekrafarinan.yademman.Leitner.Models.Notification;
import ir.fekrafarinan.yademman.Leitner.Models.Student;
import ir.fekrafarinan.yademman.Leitner.Models.Subject;
import ir.fekrafarinan.yademman.Leitner.Ui.DialogHandler;
import ir.fekrafarinan.yademman.Leitner.Ui.SubjectRecyclerViewAdapter;
import ir.fekrafarinan.yademman.Leitner.Ui.TutorialViewer;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.DatabaseDownloader;
import ir.fekrafarinan.yademman.Leitner.Utils.DateTimeUtils;
import ir.fekrafarinan.yademman.Leitner.Utils.NotificationEventReceiver;
import ir.fekrafarinan.yademman.Leitner.Utils.NotificationIntentService;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    public static Student student;
    public static Subject subject;
    private TextView txtSelectLesson, topTitle;
    private Button btnSelectLesson;
    private ImageButton btnBack, btnSync,btnNotif;
    private ConstraintLayout constraintLayout;
    public static CookieManager cookieManager;
    private RecyclerView recyclerViewCards;
    private BottomBar bottomBar;
    private static SubjectRecyclerViewAdapter adapter;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.i("Intent", "new Intent");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViews();
        setFonts();
        setListeners();
        //createMenu();
        downloadDatabase();
    }

    private void downloadDatabase() {
        SharedPreferencesHandler userHelper = new SharedPreferencesHandler(getApplicationContext());
        student = userHelper.read();
        new DatabaseDownloader(HomeActivity.this, getApplication().getDatabasePath("database.db"), this) {
            @Override
            protected void onEnd(String result) {
                afterDownload(result);
            }
        }.start(constraintLayout);
    }

    private void afterDownload(String result) {
        if (result != null) {
            SharedPreferencesHandler userHelper = new SharedPreferencesHandler(getApplicationContext());
            if (subject == null)
                subject = DbHelper.getInstance(this).getAllSubjects().get(0);
            student = userHelper.read();
            student.setBought_ids(userHelper.getLessonIds());
            student.updateBoughtDatabase();
            updateStudentCards();
            btnSelectLesson.setText(subject.getName());
            recyclerViewCards.setLayoutManager(new LinearLayoutManager(this));
            setAdapter();
            if (!new SharedPreferencesHandler(this).getIsTutorialShowed()) {
                new TutorialViewer(this).showTutorials();
            }
        } else
            retryDownload();
    }

    private void setAdapter() {
        adapter = new SubjectRecyclerViewAdapter(DbHelper.getInstance(this).getAllSubjects(), this);
        recyclerViewCards.setAdapter(adapter);
    }

    public void retryDownload() {
        Snackbar.make(constraintLayout, "لطفا دوباره تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("دوباره", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                if (getApplicationContext().getDatabasePath("database.db").delete())
                    Log.i("File Db", "DELETED");
                else
                    Log.i("File Db", "N DELETED");
            }
        }).show();
    }


    private void setListeners() {
        btnSelectLesson.setOnClickListener(this);
        btnSync.setOnClickListener(this);
        btnNotif.setOnClickListener(this);
    }

    private void setFonts() {
        new TypeFaceHandler(getApplicationContext().getAssets());
        txtSelectLesson.setTypeface(TypeFaceHandler.sultanLight);
        btnSelectLesson.setTypeface(TypeFaceHandler.sultanBold);
        topTitle.setTypeface(TypeFaceHandler.sultanBold);
    }

    private void findViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtSelectLesson = (TextView) findViewById(R.id.txtSelectLesson);
        btnSelectLesson = (Button) findViewById(R.id.btnSelectLesson);
        btnBack = (ImageButton) toolbar.findViewById(R.id.toolbarBackBtn);
        btnSync = (ImageButton) toolbar.findViewById(R.id.toolbarSyncBtn);
        btnNotif = (ImageButton) toolbar.findViewById(R.id.toolbarNotifBtn);
        topTitle = (TextView) toolbar.findViewById(R.id.txtTitle);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        recyclerViewCards = (RecyclerView) findViewById(R.id.listCards);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        initToolbar(toolbar);
        initBottomBar();
    }

    private void initBottomBar() {
        bottomBar.setDefaultTab(R.id.tab_home);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Intent intent = null;
                switch (tabId) {
                    case R.id.tab_read_cards:
                        intent = new Intent(HomeActivity.this, CardActivity.class);
                        break;
                    case R.id.tab_shopping:
                        intent = new Intent(HomeActivity.this, ShoppingActivity.class);
                        break;
                    case R.id.tab_select_cards:
                        intent = new Intent(HomeActivity.this, SelectCardsActivity.class);
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }

    private void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        topTitle.setText(getResources().getString(R.string.main_title));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        btnBack.setVisibility(View.INVISIBLE);
        btnSync.setVisibility(View.VISIBLE);
        btnNotif.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSelectLesson) {
            showDialog();
        } else if (view.getId() == R.id.toolbarSyncBtn) {
            Toast.makeText(HomeActivity.this, R.string.updateing_database, Toast.LENGTH_SHORT).show();
            DataSyncer.getInstance(HomeActivity.this).syncWithServer(new OnDataSyncEnd() {
                @Override
                public void onEnd() {
                    Toast.makeText(HomeActivity.this, getString(R.string.updating_ended), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                }
            });
            new SharedPreferencesHandler(this).setLastSyncDate(DateTimeUtils.getNow());
        } else if (view.getId() == R.id.toolbarNotifBtn) {
            startActivity(new Intent(HomeActivity.this, NotificationsActivity.class));
        }
    }

    private void updateStudentCards() {
        student.updateStudentCards(this);
    }

    private void showDialog() {
        View view = this.getLayoutInflater().inflate(R.layout.dialog_latyout_select_lesson, null);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupDialogInterface);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.DialogTheme);
        builder.setCancelable(true);
        ((TextView) view.findViewById(R.id.txtTitleDialog)).setTypeface(TypeFaceHandler.sultanBold);
        addRadioButtons(radioGroup);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok_Button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(radioButtonID);
                int idx = radioGroup.indexOfChild(radioButton);
                Subject selectedSubject = DbHelper.getInstance(getApplicationContext()).getAllSubjects().get(idx);
                HomeActivity.subject = selectedSubject;
                btnSelectLesson.setText(selectedSubject.getName());
                setAdapter();
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

    private void addRadioButtons(RadioGroup rg) {
        int num = 0;
        RadioGroup.LayoutParams rlp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        for (Subject subject : DbHelper.getInstance(this).getAllSubjects()) {
            RadioButton rd = new RadioButton(this);
            rd.setText(subject.getName());
            rd.setTypeface(TypeFaceHandler.bYekanLight);
            rg.addView(rd, rlp);
            if (num == HomeActivity.subject.getId() - 1)
                rd.setChecked(true);
            num++;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DatabaseDownloader.isDataBaseExist(this))
            recyclerViewCards.setAdapter(new SubjectRecyclerViewAdapter(DbHelper.getInstance(this).getAllSubjects(), this));
    }

    @Override
    public void onBackPressed() {
        new DialogHandler(HomeActivity.this).showAlert(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();

            }
        }, getString(R.string.exit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            finish();
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        } else if (item.getItemId() == R.id.action_help) {
            new SharedPreferencesHandler(this).setIsTutorialShowed(false);
            new SharedPreferencesHandler(this).setIsTutorialSelectCardsShowed(false);
            startActivity(new Intent(this, HomeActivity.class));
        } else if (item.getItemId() == R.id.action_shopping) {
            startActivity(new Intent(this, ShoppingActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
