package ir.fekrafarinan.yademman.Leitner.Ui;

import android.app.Activity;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.Arrays;

import ir.fekrafarinan.yademman.Leitner.Activities.HomeActivity;
import ir.fekrafarinan.yademman.Leitner.Activities.SelectCardsActivity;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class TutorialViewer implements OnShowcaseEventListener{

    int counter = 0;
    Activity activity;
    ShowcaseView showcaseView;
    ArrayList<String> titles = new ArrayList<>(
            Arrays.asList(new String[]{"سلام دوست عزیز!","انتخاب درس", "انتخاب کارت", "ورود به جعبه", "هماهنگ سازی از طریق اینترنت", "فروشگاه"}));
    ArrayList<String> texts = new ArrayList<>(
            Arrays.asList(new String[]{"به اپ یادمان خوش اومدی.بریم که قابلیتای اپو ببینیم"
                    ,"اینجا میتونی مبحثی که میخوای بخونی رو انتخاب کنی",
                    "اینجا کارتای جعبتو میتونی انتخاب کنی",
                    "اینجا هم میتونی کارتای جعبه رو مرور کنی",
                    "با این دکمه میتونی کارت هات رو ذخیره و دریافت کنی تا بتونی با دستگاه های دیگه هم کارتاتو ببینی."
            , "این بخشم واسه خرید درس هاییه که میخوای بخونی و الان قفله."})
    );

    ArrayList<ViewTarget> targets = new ArrayList<>();


    public TutorialViewer(Activity activity) {
        this.activity = activity;
        targets = new ArrayList<>(Arrays.asList(
                new ViewTarget[]{null, new ViewTarget(R.id.btnSelectLesson, activity),
                        new ViewTarget(R.id.tab_select_cards, activity),
                        new ViewTarget(R.id.tab_read_cards, activity),
                        new ViewTarget(R.id.toolbarSyncBtn, activity),
                        new ViewTarget(R.id.tab_shopping, activity)}
        ));
    }


    public  void showTutorials() {
        showcaseView = new ShowcaseView.Builder(activity)
                .setTarget(Target.NONE)
                .setContentTitle(titles.get(0))
                .setContentText(texts.get(0))
                .replaceEndButton(makeOkButton())
                .hideOnTouchOutside().setShowcaseEventListener(this).build();
        showcaseView.setShouldCentreText(true);
    }

    public void showSelectCardsTutorial(){
        titles = new ArrayList<>(
                Arrays.asList(new String[]{"انتخاب سال" , "تیک"}));
        texts = new ArrayList<>(
                Arrays.asList(new String[]{"رو هر سالی که کلیک کنی میتونی لیست درسای هر سال رو ببینی"
                    , "با انتخاب هر سال یا درس میتونی کارتاشو به جعبت اضافه کنی"} )
        );
        targets = new ArrayList<>(Arrays.asList(
                new ViewTarget[]{new CustomViewTarget(R.id.btnYearTwo, 150, 0, activity),
                        new ViewTarget(R.id.yearTwoCheckBox, activity)}
        ));
        showcaseView = new ShowcaseView.Builder(activity)
                .setTarget(targets.get(0))
                .setContentTitle(titles.get(0))
                .setContentText(texts.get(0))
                .replaceEndButton(makeOkButton())
                .hideOnTouchOutside().setShowcaseEventListener(this).build();
        showcaseView.setShouldCentreText(true);
    }


    private Button makeOkButton(){
        Button button = new Button(activity.getApplicationContext());
        button.setText("بعدی");
        return button;
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
        counter++;
        if (targets.size() > counter) {
            this.showcaseView = new ShowcaseView.Builder(activity)
                    .setTarget(targets.get(counter))
                    .setContentTitle(titles.get(counter))
                    .setContentText(texts.get(counter))
                    .replaceEndButton(makeOkButton())
                    .hideOnTouchOutside().setShowcaseEventListener(this).build();
            this.showcaseView.setShouldCentreText(true);
        }else {
            if (activity instanceof HomeActivity) {
                new SharedPreferencesHandler(activity.getApplicationContext()).setIsTutorialShowed(true);
            }
            if (activity instanceof SelectCardsActivity)
                new SharedPreferencesHandler(activity.getApplicationContext()).setIsTutorialSelectCardsShowed(true);

        }
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

    }

    public class CustomViewTarget extends ViewTarget {

        private final View mView;
        private int offsetX;
        private int offsetY;

        public CustomViewTarget(View view) {
            super(view);
            mView = view;
        }

        public CustomViewTarget(int viewId, int offsetX, int offsetY, Activity activity) {
            super(viewId, activity);
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            mView = activity.findViewById(viewId);
        }

        @Override
        public Point getPoint() {
            int[] location = new int[2];
            mView.getLocationInWindow(location);
            int x = location[0] + mView.getWidth() / 2 + offsetX;
            int y = location[1] + mView.getHeight() / 2 + offsetY;
            return new Point(x, y);
        }
    }
}
