package ir.fekrafarinan.yademman.Leitner.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.R;

public class FirstPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        ((TextView) findViewById(R.id.txtLoadingFirstPage)).setTypeface(TypeFaceHandler.sultanBold);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(FirstPageActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}
