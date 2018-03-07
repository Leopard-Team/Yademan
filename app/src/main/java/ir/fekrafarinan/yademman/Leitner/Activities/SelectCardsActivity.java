package ir.fekrafarinan.yademman.Leitner.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Models.Card;
import ir.fekrafarinan.yademman.Leitner.Models.Lesson;
import ir.fekrafarinan.yademman.Leitner.Models.Quiz;
import ir.fekrafarinan.yademman.Leitner.Models.QuizCenter;
import ir.fekrafarinan.yademman.Leitner.Models.StudentCard;
import ir.fekrafarinan.yademman.Leitner.Models.Subject;
import ir.fekrafarinan.yademman.Leitner.Models.Tick;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Ui.DialogHandler;
import ir.fekrafarinan.yademman.Leitner.Ui.QuizRecyclerViewAdapter;
import ir.fekrafarinan.yademman.Leitner.Ui.SpinnerAdapter;
import ir.fekrafarinan.yademman.Leitner.Ui.TutorialViewer;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.Leitner.Utils.DateTimeUtils;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class SelectCardsActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox yearOne, yearTwo, yearThree, yearFour;
    private TextView txtSelectYear, txtSelectQuiz, topTitle, budgetTv;
    private Spinner selectSpinner;
    private ArrayList<Subject> subjects;
    private SpinnerAdapter spinnerAdapter;
    private Button btnCancelCards, btnOkCards, btnYearOne, btnYearTwo, btnYearThree, btnYearFour;
    private ImageButton btnBack;
    private ArrayList<Tick> lastTicks = new ArrayList<>();
    private ArrayList<Tick> added = new ArrayList<>();
    private ArrayList<Tick> removed = new ArrayList<>();
    private RecyclerView quizRecyclerView;
    private QuizRecyclerViewAdapter adapter;
    private AlertDialog dialog;
    private boolean removeAll = true;
    private BottomBar bottomBar;
    private ArrayList<Quiz> quizes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cards);
        findViews();
        setFonts();
        setListeners();
        getTicks();
        if (!new SharedPreferencesHandler(this).getIsTutorialSelectCardsShowed()) {
            new TutorialViewer(this).showSelectCardsTutorial();
        }
    }

    private void getTicks() {
        final CheckBox[] yearCheckBoxes = {yearOne, yearTwo, yearThree, yearFour};
        final Button[] yearButtons = {btnYearOne, btnYearTwo, btnYearThree, btnYearFour};
        DbHelper dbHelper = DbHelper.getInstance(SelectCardsActivity.this);
        lastTicks = dbHelper.getAllTicks();
        for (int year = 1; year < 5; year++) {
            int yearLessonsSize = dbHelper.getLessonsWithYear(year).size();
            if (dbHelper.getTicksWithYear(year).size() >= dbHelper.getBoughtLessonsSize(year)
                    && dbHelper.getTicksWithYear(year).size() != 0)
                yearCheckBoxes[year - 1].setChecked(true);
            else if (yearLessonsSize == 0) {
                yearButtons[year - 1].setEnabled(false);
                yearCheckBoxes[year - 1].setEnabled(false);
                yearButtons[year - 1].getBackground().setAlpha(128);
                yearCheckBoxes[year - 1].getBackground().setAlpha(128);
            }
            if (yearLessonsSize != 0){
                yearButtons[year - 1].setEnabled(true);
                yearCheckBoxes[year - 1].setEnabled(true);
                yearButtons[year - 1].getBackground().setAlpha(0);
                yearCheckBoxes[year - 1].getBackground().setAlpha(0);
            }
        }
        added = new ArrayList<>();
        removed = new ArrayList<>();
    }

    private void setListeners() {
        final DbHelper dbHelper = DbHelper.getInstance(SelectCardsActivity.this);
        View.OnClickListener checkBoxListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Lesson> lessons = null;
                Boolean checked = null;
                int year = 1;
                switch (view.getId()) {
                    case R.id.btnYearOne:
                        lessons = dbHelper.getLessonsWithYear(1);
                        checked = yearOne.isChecked();
                        year = 1;
                        break;
                    case R.id.btnYearTwo:
                        lessons = dbHelper.getLessonsWithYear(2);
                        checked = yearTwo.isChecked();
                        year = 2;
                        break;
                    case R.id.btnYearThree:
                        lessons = dbHelper.getLessonsWithYear(3);
                        checked = yearThree.isChecked();
                        year = 3;
                        break;
                    case R.id.btnYearFour:
                        lessons = dbHelper.getLessonsWithYear(4);
                        checked = yearFour.isChecked();
                        year = 4;
                        break;
                }
                showDialog(lessons, checked, year);
            }
        };
        final CheckBox[] yearCheckBoxes = {yearOne, yearTwo, yearThree, yearFour};
        for (int i = 0; i < yearCheckBoxes.length; i++) {
            final int year = i + 1;
            yearCheckBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    ArrayList<Lesson> lessons = dbHelper.getLessonsWithYear(year);
                    if (b) {
                        for (int j = 0; j < lessons.size(); j++) {
                            if (lessons.get(j).isBought())
                                add(new Tick(HomeActivity.subject.getId(), year, lessons.get(j).getId()));
                        }
                    } else {
                        for (int j = 0; j < lessons.size(); j++) {
                            if (lessons.get(j).isBought() && removeAll)
                                remove(new Tick(HomeActivity.subject.getId(), year, lessons.get(j).getId()));
                        }
                        removeAll = true;
                    }
                }
            });
        }
        btnOkCards.setOnClickListener(this);
        btnCancelCards.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnYearOne.setOnClickListener(checkBoxListener);
        btnYearTwo.setOnClickListener(checkBoxListener);
        btnYearThree.setOnClickListener(checkBoxListener);
        btnYearFour.setOnClickListener(checkBoxListener);
        selectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                HomeActivity.subject = subjects.get(position);
                for (int i = 0; i < 4; i++) {
                    yearCheckBoxes[i].setChecked(false);
                }
                getTicks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void add(Tick tick) {
        if (removed.contains(tick))
            removed.remove(removed.indexOf(tick));
        else if (!added.contains(tick)) {
            added.add(tick);
        }
    }

    private void remove(Tick tick) {
        if (added.contains(tick)) {
            added.remove(added.indexOf(tick));
        }
        else if (!removed.contains(tick)) {
            removed.add(tick);
        }
    }

    private void setFonts() {
        btnYearOne.setTypeface(TypeFaceHandler.bYekanLight);
        btnYearTwo.setTypeface(TypeFaceHandler.bYekanLight);
        btnYearThree.setTypeface(TypeFaceHandler.bYekanLight);
        btnYearFour.setTypeface(TypeFaceHandler.bYekanLight);
        txtSelectYear.setTypeface(TypeFaceHandler.sultanLight);
        txtSelectQuiz.setTypeface(TypeFaceHandler.sultanLight);
        btnOkCards.setTypeface(TypeFaceHandler.bYekanLight);
        btnCancelCards.setTypeface(TypeFaceHandler.bYekanLight);
        topTitle.setTypeface(TypeFaceHandler.sultanBold);
    }

    private void findViews() {
        subjects = DbHelper.getInstance(this).getAllSubjects();
        selectSpinner = (Spinner) findViewById(R.id.select_lesson_spinner);
        selectSpinner.setVisibility(View.VISIBLE);
        spinnerAdapter = new SpinnerAdapter(this, R.layout.select_spinner_item, subjects);
        selectSpinner.setAdapter(spinnerAdapter);
        selectSpinner.setSelection(subjects.indexOf(HomeActivity.subject));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        yearOne = (CheckBox) findViewById(R.id.yearOneCheckBox);
        yearTwo = (CheckBox) findViewById(R.id.yearTwoCheckBox);
        yearThree = (CheckBox) findViewById(R.id.yearThreeCheckBox);
        yearFour = (CheckBox) findViewById(R.id.yearFourCheckBox);
        txtSelectYear = (TextView) findViewById(R.id.txtSelectYear);
        txtSelectQuiz = (TextView) findViewById(R.id.txtSelectQuiz);
        topTitle = (TextView) toolbar.findViewById(R.id.txtTitle);
        btnCancelCards = (Button) findViewById(R.id.btnCancelCards);
        btnOkCards = (Button) findViewById(R.id.btnOkCards);
        btnYearOne = (Button) findViewById(R.id.btnYearOne);
        btnYearTwo = (Button) findViewById(R.id.btnYearTwo);
        btnYearThree = (Button) findViewById(R.id.btnYearThree);
        btnYearFour = (Button) findViewById(R.id.btnYearFour);
        btnBack = (ImageButton) toolbar.findViewById(R.id.toolbarBackBtn);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        quizRecyclerView = (RecyclerView) findViewById(R.id.quizRecyclerView);
        if (!Connection.isConnected(this)){
            Quiz quiz = new Quiz("اتصال به اینترنت برقرار نیست", "", QuizCenter.NOTCONNECTED, "[]");
            quizes.add(quiz);
        }else {
            getQuizezList();
        }
        adapter = new QuizRecyclerViewAdapter(quizes, this, this);
        quizRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        quizRecyclerView.setAdapter(adapter);
        initToolbar(toolbar);
        initBottomBar();
    }

    private void getQuizezList() {
        ConnectionUi connectionUi = new ConnectionUi(this) {
            @Override
            public void start() {
                Quiz quiz = new Quiz("در حال اتصال به سرور", "لطفا صبر کنید", QuizCenter.CONNECTING, "[]");
                quizes.add(quiz);
            }
            @Override
            public void update(Integer... values) {
            }
            @Override
            public void end() {
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("user", HomeActivity.student.getUserName());
        new Connection(getString(R.string.base_url) + "sendQuizes.php", params, connectionUi) {
            @Override
            public void endProcess(String result) {
                Log.i("Select/getQL/result", result);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    quizes.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject quizJson = jsonArray.getJSONObject(i);
                        String name = quizJson.getString("name");
                        String date = quizJson.getString("date");
                        String lessonsList = quizJson.getString("list");
                        QuizCenter quizCenter = QuizCenter.getByName(quizJson.getString("enum"));
                        Quiz quiz = new Quiz(name, date, quizCenter, lessonsList);
                        quiz.makeLessons(SelectCardsActivity.this);
                        quizes.add(quiz);
                    }
                } catch (JSONException e) {
                    Log.e("Select/getQuizesList", e.getMessage());
                }

            }

            @Override
            protected void onResult(String result) {
                adapter = new QuizRecyclerViewAdapter(quizes, SelectCardsActivity.this, SelectCardsActivity.this);
                quizRecyclerView.setAdapter(adapter);
            }
        }.execute();

    }

    private void initBottomBar() {
        bottomBar.setDefaultTab(R.id.tab_select_cards);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Intent intent = null;
                switch (tabId) {
                    case R.id.tab_home:
                        intent = new Intent(SelectCardsActivity.this, HomeActivity.class);
                        break;
                    case R.id.tab_read_cards:
                        intent = new Intent(SelectCardsActivity.this, CardActivity.class);
                        break;
                    case R.id.tab_shopping:
                        if (Connection.isConnected(SelectCardsActivity.this))
                            intent = new Intent(SelectCardsActivity.this, ShoppingActivity.class);
                        else
                            Toast.makeText(SelectCardsActivity.this, "لطفا اتصال اینترنت خود را برقرار کنید", Toast.LENGTH_LONG).show();
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
        topTitle.setText(getResources().getString(R.string.select_card_title));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        TextView budget = createBudgetTextView();
//        toolbar.addView(budget);
    }

    private TextView createBudgetTextView() {
        budgetTv = new TextView(this);
        budgetTv.setTypeface(TypeFaceHandler.sultanBold);
        budgetTv.setText("اعتبار:\n" + new SharedPreferencesHandler(this).getBudget() + " تومان");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        budgetTv.setLayoutParams(params);
        return budgetTv;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.toolbarBackBtn) {
            onBackPressed();
        } else if (view.getId() == R.id.btnOkCards) {
            final ConnectionUi connectionUi = ConnectionUi.getDefault(this);
            connectionUi.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Tick tick : added) {
                        addTickToDb(tick);
                    }
                    for (Tick tick : removed) {
                        Tick removingTick = getFromLast(tick);
                        if (removingTick != null)
                            removeTickFromDb(removingTick);
                    }
                    connectionUi.end();
                    startActivity(new Intent(SelectCardsActivity.this, HomeActivity.class));
                    finish();
                }
            }).start();
        } else if (view.getId() == R.id.btnCancelCards) {
            startActivity(new Intent(SelectCardsActivity.this, HomeActivity.class));
            finish();
        }
    }

    private void removeTickFromDb(Tick tick) {
        DbHelper dbHelper = DbHelper.getInstance(SelectCardsActivity.this);
        dbHelper.deleteTick(tick.getId());
        ArrayList<StudentCard> studentCards = dbHelper.getStudentCardsWithLesson(tick.getLessonId());
        dbHelper.deleteStudentCards(studentCards);
    }

    private void addTickToDb(Tick tick) {
        DbHelper dbHelper = DbHelper.getInstance(SelectCardsActivity.this);
        dbHelper.addTick(tick);
        ArrayList<Card> cards = dbHelper.getCardsWithLesson(tick.getLessonId());
        ArrayList<StudentCard> studentCards = new ArrayList<>();
        for (Card card : cards) {
            studentCards.add(new StudentCard(DateTimeUtils.getNow(), card, 1, HomeActivity.student));
        }
        dbHelper.addStudentCards(studentCards, true);
    }

    private Tick getFromLast(Tick tick) {
        int index = lastTicks.indexOf(tick);
        if (index >= 0 && index < lastTicks.size())
            return lastTicks.get(index);
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_from_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTicks();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void showDialog(List<Lesson> lessons, final boolean checked, final int year) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectCardsActivity.this, R.style.DialogTheme);
        builder.setCancelable(true);
        View view = this.getLayoutInflater().inflate(R.layout.dialog_latyout_select_cards, null);
        ((TextView) view.findViewById(R.id.txtTitleDialog)).setTypeface(TypeFaceHandler.sultanBold);
        final ArrayList<CheckBox> checkBoxes = addCheckBoxes((LinearLayout)
                view.findViewById(R.id.linearLayoutDialog), lessons, checked);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok_Button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean isAllChecked = true;
                final CheckBox[] yearCheckBoxes = {yearOne, yearTwo, yearThree, yearFour};
                for (int j = 0; j < checkBoxes.size(); j++) {
                    if (!checkBoxes.get(j).isChecked()) {
                        isAllChecked = false;
                        break;
                    }
                }
                if (isAllChecked && !checked) {
                    yearCheckBoxes[year - 1].setChecked(true);
                }
                if (!isAllChecked && checked)
                    yearCheckBoxes[year - 1].setChecked(false);


            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialog = builder.create();
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

    private ArrayList<CheckBox> addCheckBoxes(LinearLayout layout, final List<Lesson> lessons,
                                              final boolean checked) {
        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams rlp2 = new LinearLayout.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.MATCH_PARENT, 1.0f);
        LinearLayout.LayoutParams rlp3 = new LinearLayout.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        for (final Lesson lesson : lessons) {
            String name = lesson.getName();
            if (lesson.isBought()) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(name);
                checkBox.setTypeface(TypeFaceHandler.bYekanLight);
                checkBox.setGravity(Gravity.START);
                if (checked)
                    checkBox.setChecked(true);
                else if (lastTicks.contains(new Tick(HomeActivity.subject.getId(), lesson.getYear(), lesson.getId())))
                    checkBox.setChecked(true);
                else if (added.contains(new Tick(HomeActivity.subject.getId(), lesson.getYear(), lesson.getId())))
                    checkBox.setChecked(true);
                else
                    checkBox.setChecked(false);
                layout.addView(checkBox, rlp);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            add(new Tick(HomeActivity.subject.getId(), lesson.getYear(), lesson.getId()));
                        } else {
                            remove(new Tick(HomeActivity.subject.getId(), lesson.getYear(), lesson.getId()));
                        }
                    }
                });
                checkBoxes.add(checkBox);
            } else {
                LinearLayout l = new LinearLayout(this);
                l.setOrientation(LinearLayout.HORIZONTAL);
                TextView txtName = new TextView(this);
                txtName.setText(name);
                txtName.setTypeface(TypeFaceHandler.bYekanLight);
                txtName.setGravity(Gravity.CENTER | Gravity.START);
                ImageButton buyIcon = createBuyIcon(lesson);
                l.addView(buyIcon, rlp3);
                l.addView(txtName, rlp2);
                layout.addView(l, rlp);
            }
        }
        return checkBoxes;
    }

    private ImageButton createBuyIcon(final Lesson lesson) {
        ImageButton buyIcon = new ImageButton(this);
        buyIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_lock_red_24dp, null));
        buyIcon.setBackgroundColor(Color.TRANSPARENT);
        buyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogHandler(SelectCardsActivity.this).showAlert(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(SelectCardsActivity.this, ShoppingActivity.class));
                        finish();
                    }
                }, "آیا مایل به خرید این درس ها از فروشگاه هستید؟");
            }
        });
        return buyIcon;
    }
}
