package ir.fekrafarinan.yademman.Leitner.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.fekrafarinan.yademman.Leitner.Database.DataFinderOfSubject;
import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Database.Encrypter;
import ir.fekrafarinan.yademman.Leitner.Models.Student;
import ir.fekrafarinan.yademman.Leitner.Models.StudentCard;
import ir.fekrafarinan.yademman.Leitner.Models.Subject;
import ir.fekrafarinan.yademman.Leitner.Ui.SpinnerAdapter;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.R;

public class CardActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.txtQuestionTestCard)
    TextView txtQuestionTestCard;
    @Bind(R.id.btnTestCard1)
    Button btnTestCard1;
    @Bind(R.id.btnTestCard2)
    Button btnTestCard2;
    @Bind(R.id.btnTestCard3)
    Button btnTestCard3;
    @Bind(R.id.btnTestCard4)
    Button btnTestCard4;
    @Bind(R.id.btnShowNextCard)
    Button btnShowNextCard;
    @Bind(R.id.switcherCardTest)
    ViewSwitcher switcherCardTest;
    private Button btnShowAnswer;
    private ViewFlipper flipper;
    private Spinner selectSpinner;
    private ArrayList<Subject> subjects;
    private SpinnerAdapter spinnerAdapter;
    private StudentCard studentCard;
    private TextView txtQuestionCard, txtAnswerCard, txtQuestionCardWithImage, txtAnswerCardWithImage, topTitle;
    private ImageButton btnBack, btnCorrectAnswer, btnWrongAnswer;
    private ImageView imgQuestion, imgAnswer;
    private ViewSwitcher switcherQuestion, switcherAnswer;
    private RoundCornerProgressBar correctAnswers;
    private RoundCornerProgressBar wrongAnswers;
    //    private VideoView videoAnswer;
    private BottomBar bottomBar;
    private int wrongAnswersNumber;
    private int correctAnswersNumber;
    private int allCards;
    private TextView txtAllCardsNumber;
    private boolean isTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        ButterKnife.bind(this);
        findViews();
        setFonts();
        initViews();
        setSpinnerListener();
        updateStudentCards();
    }

    private void setSpinnerListener() {
        selectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                HomeActivity.subject = subjects.get(position);
                txtAllCardsNumber.setText("0");
                DataFinderOfSubject.getTodayCardsNumber(CardActivity.this,
                        HomeActivity.subject, new DataFinderOfSubject.onFindEnd() {
                            @Override
                            public void onEnd(int number) {
                                txtAllCardsNumber.setText(number + "");
                            }
                        });
                correctAnswersNumber = 0;
                wrongAnswersNumber = 0;
                correctAnswers.setProgress(correctAnswersNumber);
                wrongAnswers.setProgress(wrongAnswersNumber);
                updateStudentCards();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateStudentCards() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                HomeActivity.student.updateStudentCards(CardActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                getCard();
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void getCard() {
        studentCard = HomeActivity.student.getFirstCard();
        if (studentCard != null) {
            try {
                if (!studentCard.getCard().isHasTest()) {
                    if (isTest) {
                        switcherCardTest.showNext();
                    }
                    isTest = false;
                    btnShowAnswer.setVisibility(View.VISIBLE);
                    txtQuestionCard.setText(Encrypter.decryptMsg(studentCard.getCard().getQuestion(),
                            Encrypter.getKey()));
                    txtAnswerCard.setText(Encrypter.decryptMsg(studentCard.getCard().getAnswer(),
                            Encrypter.getKey()));
                    txtQuestionCardWithImage.setText(Encrypter.decryptMsg(studentCard.getCard().getQuestion(),
                            Encrypter.getKey()));
                    txtAnswerCardWithImage.setText(Encrypter.decryptMsg(studentCard.getCard().getAnswer(),
                            Encrypter.getKey()));
                } else {
                    Button[] buttons = new Button[]{btnTestCard1, btnTestCard2, btnTestCard3, btnTestCard4};
                    btnShowNextCard.setVisibility(View.GONE);
                    if (!isTest)
                        switcherCardTest.showNext();
                    else {
                        switcherCardTest.showNext();
                        switcherCardTest.showNext();
                    }
                    isTest = true;
                    txtQuestionTestCard.setText(Encrypter.decryptMsg(studentCard.getCard().getQuestion(),
                            Encrypter.getKey()));
                    Random random = new Random();
                    Button btnAns = buttons[random.nextInt(4)];
                    btnAns.setText(Encrypter.decryptMsg(studentCard.getCard().getAnswer(), Encrypter.getKey()));
                    int btnWrongNum = 0;
                    for (Button button :
                            buttons) {
                        button.setBackgroundColor(ContextCompat.getColor(CardActivity.this, R.color.transparent));
//                        btnAns.setCompoundDrawables(ContextCompat.getDrawable(CardActivity.this, R.drawable.ic_keyboard_arrow_left_black_24dp),
//                                null, null, null);
                        if (button != btnAns) {
                            btnWrongNum++;
                            button.setText(studentCard.getCard().getAns(btnWrongNum));
                        }
                    }
                    setTestListeners(buttons, btnAns);
                }
            } catch (Exception e) {
                Log.d("Exception", e.getMessage());
            }
            if (studentCard.getCard().hasQuestionImage()) {
                imgQuestion.setImageBitmap(studentCard.getCard().getQuestionImage());
                switcherQuestion.showNext();
            } else {
                switcherQuestion.setDisplayedChild(0);
            }
            if (studentCard.getCard().hasAnswerImage()) {
                imgAnswer.setImageBitmap(studentCard.getCard().getAnswerImage());
                switcherAnswer.showNext();
            } else if (studentCard.getCard().hasAnswerVideo()) {
                switcherAnswer.setDisplayedChild(2);
//                videoAnswer.setVideoURI(studentCard.getCard().getAnswerVideo());
//                videoAnswer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        mp.setLooping(false);
//                        videoAnswer.start();
//                    }
//                });
            } else {
                switcherAnswer.setDisplayedChild(0);
            }
        } else {
            if (isTest)
                switcherCardTest.showNext();
            isTest = false;
            txtQuestionCardWithImage.setText(R.string.no_card);
            txtAnswerCardWithImage.setText(R.string.no_card);
            txtQuestionCard.setText(R.string.no_card);
            txtAnswerCard.setText(R.string.no_card);
            btnShowAnswer.setVisibility(View.GONE);
        }
    }


    private void setTestListeners(final Button[] buttons, final Button btnAns) {
        btnAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentCard.getAnswer(true, Student.getLeitnerRule(), CardActivity.this);
                correctAnswersNumber++;
                btnAns.setBackgroundColor(ContextCompat.getColor(CardActivity.this, R.color.darkGreen));
                btnShowNextCard.setVisibility(View.VISIBLE);
                removeTestListeners(buttons);
            }
        });
        for (final Button button :
                buttons) {
            if (button != btnAns) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        studentCard.getAnswer(false, Student.getLeitnerRule(), CardActivity.this);
                        wrongAnswersNumber++;
                        button.setBackgroundColor(ContextCompat.getColor(CardActivity.this, R.color.colorAccent));
                        btnAns.setBackgroundColor(ContextCompat.getColor(CardActivity.this, R.color.darkGreen));
                        removeTestListeners(buttons);
                        btnShowNextCard.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    private void removeTestListeners(Button[] buttons) {
        for (Button button :
                buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    private void initViews() {
        subjects = DbHelper.getInstance(this).getAllSubjects();
        selectSpinner = (Spinner) findViewById(R.id.select_lesson_spinner);
        selectSpinner.setVisibility(View.VISIBLE);
        spinnerAdapter = new SpinnerAdapter(this, R.layout.select_spinner_item, subjects);
        selectSpinner.setAdapter(spinnerAdapter);
        selectSpinner.setSelection(subjects.indexOf(HomeActivity.subject));
        btnShowAnswer.setOnClickListener(this);
        btnCorrectAnswer.setOnClickListener(this);
        btnWrongAnswer.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnShowNextCard.setOnClickListener(this);
        txtQuestionCardWithImage.setMovementMethod(new ScrollingMovementMethod());
        txtAnswerCardWithImage.setMovementMethod(new ScrollingMovementMethod());
        txtQuestionCard.setMovementMethod(new ScrollingMovementMethod());
        txtAnswerCard.setMovementMethod(new ScrollingMovementMethod());
        btnShowAnswer.setMovementMethod(new ScrollingMovementMethod());
    }

    private void setFonts() {
        txtQuestionTestCard.setTypeface(TypeFaceHandler.bYekanLight);
        btnTestCard1.setTypeface(TypeFaceHandler.bYekanLight);
        btnTestCard2.setTypeface(TypeFaceHandler.bYekanLight);
        btnTestCard3.setTypeface(TypeFaceHandler.bYekanLight);
        btnTestCard4.setTypeface(TypeFaceHandler.bYekanLight);
        btnTestCard1.setTypeface(TypeFaceHandler.bYekanLight);
        btnShowNextCard.setTypeface(TypeFaceHandler.sultanBold);
        btnShowAnswer.setTypeface(TypeFaceHandler.sultanBold);
        txtQuestionCard.setTypeface(TypeFaceHandler.bYekanLight);
        txtAnswerCard.setTypeface(TypeFaceHandler.bYekanLight);
        txtAllCardsNumber.setTypeface(TypeFaceHandler.sultanBold);
        txtQuestionCardWithImage.setTypeface(TypeFaceHandler.bYekanLight);
        txtAnswerCardWithImage.setTypeface(TypeFaceHandler.bYekanLight);
        topTitle.setTypeface(TypeFaceHandler.sultanBold);
        int fontSize = this.getSharedPreferences("shared", MODE_PRIVATE).getInt("fontSize", 18);
        txtQuestionCard.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtAnswerCard.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtQuestionCardWithImage.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtAnswerCardWithImage.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    private void findViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        flipper = (ViewFlipper) findViewById(R.id.cardFlipper);
        btnShowAnswer = (Button) findViewById(R.id.btnShowAnswer);
        btnCorrectAnswer = (ImageButton) findViewById(R.id.btnCorrectAnswer);
        btnWrongAnswer = (ImageButton) findViewById(R.id.btnWrongAnswer);
        txtQuestionCard = (TextView) findViewById(R.id.txtQuestionCard);
        txtAnswerCard = (TextView) findViewById(R.id.txtAnswerCard);
        txtAllCardsNumber = (TextView) findViewById(R.id.txtAllCardsNumber);
        txtQuestionCardWithImage = (TextView) findViewById(R.id.txtQuestionCardWithImage);
        txtAnswerCardWithImage = (TextView) findViewById(R.id.txtAnswerCardWithImage);
        topTitle = (TextView) toolbar.findViewById(R.id.txtTitle);
        btnBack = (ImageButton) toolbar.findViewById(R.id.toolbarBackBtn);
        imgAnswer = (ImageView) findViewById(R.id.imgAnswer);
        imgQuestion = (ImageView) findViewById(R.id.imgQuestion);
        switcherQuestion = (ViewSwitcher) findViewById(R.id.switcherQuestion);
        switcherAnswer = (ViewSwitcher) findViewById(R.id.switcherAnswer);
//        videoAnswer = (VideoView) findViewById(R.id.videoAnswer);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        correctAnswers = (RoundCornerProgressBar) findViewById(R.id.progressBarCorrectAnswers);
        wrongAnswers = (RoundCornerProgressBar) findViewById(R.id.progressBarWrongAnswers);
        initToolbar(toolbar);
        initBottomBar();
        initProgressBar();
        switcherCardTest.showNext();
//        btnShowNextCard.setVisibility(View.GONE);
        switcherCardTest.setInAnimation(this, R.anim.enter_from_left);
        switcherCardTest.setOutAnimation(this, R.anim.exit_from_right);
    }

    private void initProgressBar() {
        DataFinderOfSubject.getTodayCardsNumber(this, HomeActivity.subject, new DataFinderOfSubject.onFindEnd() {
            @Override
            public void onEnd(int number) {
                if (number != 0) {
                    correctAnswers.setMax(number);
                    wrongAnswers.setMax(number);
                    wrongAnswersNumber = 0;
                    correctAnswersNumber = 0;
                    allCards = number;
                    txtAllCardsNumber.setText(String.valueOf(allCards));
                }
            }
        });
    }

    private void initBottomBar() {
        bottomBar.setDefaultTab(R.id.tab_read_cards);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Intent intent = null;
                switch (tabId) {
                    case R.id.tab_home:
                        intent = new Intent(CardActivity.this, HomeActivity.class);
                        break;
                    case R.id.tab_shopping:
                        if (Connection.isConnected(CardActivity.this))
                            intent = new Intent(CardActivity.this, ShoppingActivity.class);
                        else
                            Toast.makeText(CardActivity.this, "لطفا اتصال اینترنت خود را برقرار کنید", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.tab_select_cards:
                        intent = new Intent(CardActivity.this, SelectCardsActivity.class);
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
        topTitle.setText(getResources().getString(R.string.card_title));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnShowAnswer) {
            flipper.setInAnimation(this, R.anim.grow_from_middle);
            flipper.setOutAnimation(this, R.anim.shrink_to_middle);
            flipper.getOutAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            flipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            allCards--;
            flipper.showNext();
        } else if (view.getId() == R.id.toolbarBackBtn) {
            onBackPressed();
        } else if (view.getId() == R.id.btnCorrectAnswer) {
            correctAnswersNumber++;
            studentCard.getAnswer(true, Student.getLeitnerRule(), CardActivity.this);
            flipper.setInAnimation(this, R.anim.enter_from_left);
            flipper.setOutAnimation(this, R.anim.exit_from_right);
            flipper.getOutAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    getCard();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            flipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            flipper.showNext();
        } else if (view.getId() == R.id.btnWrongAnswer) {
            wrongAnswersNumber++;
            studentCard.getAnswer(false, Student.getLeitnerRule(), CardActivity.this);
            flipper.setInAnimation(this, R.anim.enter_from_left);
            flipper.setOutAnimation(this, R.anim.exit_from_right);
            flipper.getOutAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    getCard();

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            flipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            flipper.showNext();
        } else if (view.getId() == R.id.btnShowNextCard) {
            allCards--;
            getCard();
        }
        correctAnswers.setProgress(correctAnswersNumber);
        wrongAnswers.setProgress(wrongAnswersNumber);
        txtAllCardsNumber.setText(String.valueOf(allCards));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_from_right);
    }
}
