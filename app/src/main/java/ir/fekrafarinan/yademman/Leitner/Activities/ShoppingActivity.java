package ir.fekrafarinan.yademman.Leitner.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ir.fekrafarinan.yademman.Leitner.Database.DbHelper;
import ir.fekrafarinan.yademman.Leitner.Models.Lesson;
import ir.fekrafarinan.yademman.Leitner.Models.Subject;
import ir.fekrafarinan.yademman.Leitner.Ui.ConnectionUi;
import ir.fekrafarinan.yademman.Leitner.Ui.DialogHandler;
import ir.fekrafarinan.yademman.Leitner.Ui.TypeFaceHandler;
import ir.fekrafarinan.yademman.Leitner.Utils.Connection;
import ir.fekrafarinan.yademman.Leitner.Utils.SharedPreferencesHandler;
import ir.fekrafarinan.yademman.R;

public class ShoppingActivity extends AppCompatActivity {


    static final String TAG = "myDebug";
    static final String[] SKU_PREMIUMS = {"Tarikh-Adabiat", "Dini", "loghat", "Emla", "Zaban"};
    static final String[] SKU_PREMIUMS_DISCOUNT = {"Tarikh-Discount", "Dini-Discount",
            "Loghat-Discount", "Emla-Discount", "Zaban-Discount"};
    static final int[] firstNotBoughtIds = {74, 116, 213, 214, 228};
    static String[] SKU_PREMIUM = {SKU_PREMIUMS[0]};
    int index = 0;
    private ArrayList<Integer> boughts = new ArrayList<>();
    private ArrayList<Subject> subjects = new ArrayList<>();
    static final int RC_REQUEST = 1372; // The helper object
    IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    IabHelper.OnConsumeFinishedListener onConsumeFinishedListener;
    private TextView txtPrice1, txtPrice2, txtPrice3, txtPrice4, txtPrice5, txtChoosePrice, topTitle, budgetTv;
    private int[] prices = {0, 0, 0, 0, 0};
    private ImageButton btnBack;
    private boolean isQueryFinished = false;
    private boolean isAppExist = true;
    private BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        findViews();
        findIsAppExist();
        if (isAppExist) {
            setPurchaseHelpers();
        }
        findBoughts();
        setFonts();
    }

    private void findBoughts() {
        DbHelper dbHelper = DbHelper.getInstance(this);
        for (int i = 0; i < firstNotBoughtIds.length; i++) {
            Lesson lesson = dbHelper.getLesson(firstNotBoughtIds[i]);
            if (lesson.isBought())
                boughts.add(i);
        }
    }

    private void findIsAppExist() {
        isAppExist = isAppInstalled(this, "com.farsitel.bazaar");
        if (!isAppExist){
            Snackbar.make(bottomBar, "برای خرید باید اپلیکیشن کافه بازار نصب باشد", Snackbar.LENGTH_LONG).show();
        }
    }

    private void setPurchaseHelpers() {
        firstSetup();
        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (result.isFailure()) {
                    Log.d(TAG, "Error purchasing: " + result);
                    return;
                } else if (purchase.getSku().equals(SKU_PREMIUM[0])) {

                    Toast.makeText(ShoppingActivity.this, "خرید موفق", Toast.LENGTH_SHORT).show();
                    purchase(purchase);
                }
            }
        };

    }

    private void firstSetup() {
        final ConnectionUi connectionUi = ConnectionUi.getDefault(this);
        String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDNWOHgHmaQKJHvvS63V6FFRMLnp5S5qxpE5hNXV1qFzUwaBa2xnP1n57fVpeLSHwP0rj6m9R0Ta8lvh5uLfwpivkfEFBBvcCZ67r72IBQh0PUmNTcjaqTXNy1Bq5eFPcwFlWxv10gngM6oWTvAJfvnPyjeyclp8Opau+mANDSefzoNGmP78BCZZ0mKWQ4AQkyAlCk5AYJoeEKdolUycHfWxQFKwzZYnaf5eVROBjMCAwEAAQ==";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        onConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
            @Override
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                if (result.isSuccess()) {
                    Log.d(TAG, "consume success");
                } else {
                    Log.d(TAG, "consume failed");
                }
            }
        };
        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                Log.d(TAG, "Query inventory finished.");
                if (result.isFailure()) {
                    Log.d(TAG, "Failed to query inventory: " + result);
                } else {
                    for (int i = 0; i < SKU_PREMIUMS.length; i++) {
                        if (inventory.hasPurchase(SKU_PREMIUMS[i]))
                            mHelper.consumeAsync(inventory.getPurchase(SKU_PREMIUMS[i]), onConsumeFinishedListener);
                    }
                }
                Log.d(TAG, "Initial inventory query finished; enabling main UI.");
                connectionUi.end();
                isQueryFinished = true;
            }
        };
        connectionUi.start();
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");
                if (!result.isSuccess()) {
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
        setPrices(prices);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void setPrices(final int[] prices) {
        Connection connection = new Connection(getString(R.string.base_url) + "sendArray.php", null, ConnectionUi.noneUi(this)) {
            @Override
            protected void onResult(String result) {
                Log.i(TAG, "result:" + result);
                JSONArray array = null;
                try {
                    array = new JSONArray(result);
                    for (int i = 0; i < array.length(); i++) {
                        prices[i] = array.getInt(i);
                        setPriceTexts();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        connection.execute();
    }

    private void setPriceTexts() {
        if (txtPrice1 != null)
            txtPrice1.setText(subjects.get(0).getName() + "\n" + prices[0] + " تومان");
        if (txtPrice2 != null)
            txtPrice2.setText(subjects.get(1).getName() + "\n" + prices[1] + " تومان");
        if (txtPrice3 != null)
            txtPrice3.setText(subjects.get(2).getName() + "\n" + prices[2] + " تومان");
        if (txtPrice4 != null)
            txtPrice4.setText(subjects.get(3).getName() + "\n" + prices[3] + " تومان");
        if (txtPrice5 != null)
            txtPrice5.setText(subjects.get(4).getName() + "\n" + prices[4] + " تومان");
    }

    private void purchase(Purchase purchase) {
        HashMap<String, String> params = new HashMap<>();
        params.put("price", 0+"");
        HomeActivity.subject = subjects.get(index);
        DbHelper dbHelper = DbHelper.getInstance(this);
        final ArrayList<Lesson> lessons = dbHelper.getAllLessons();
        params.put("ids", Lesson.getIdList(lessons));
        params.put("user", HomeActivity.student.getUserName());
        params.put("size", lessons.size()+"");
        params.put(HomeActivity.student.getUserName(), new SharedPreferencesHandler(this).getToken());
        Connection connection = new Connection(this.getString(R.string.base_url) + "buy.php",
                params, ConnectionUi.getDefault(this)) {
            @Override
            protected void onResult(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    boolean success = json.getBoolean("success");
                    int budget = json.getInt("budget");
                    String message = json.getString("message");
                    if (success) {
                        Toast.makeText(ShoppingActivity.this, message, Toast.LENGTH_LONG).show();
                        for (int i = 0; i < lessons.size(); i++) {
                            lessons.get(i).afterBuy(ShoppingActivity.this);
                        }
                    } else {
                        Toast.makeText(ShoppingActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                    HomeActivity.student.setBudget(budget, ShoppingActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        connection.execute();
    }

    private void setFonts() {
        txtPrice1.setTypeface(TypeFaceHandler.sultanLight);
        txtPrice2.setTypeface(TypeFaceHandler.sultanLight);
        txtPrice3.setTypeface(TypeFaceHandler.sultanLight);
        txtPrice4.setTypeface(TypeFaceHandler.sultanLight);
        txtPrice5.setTypeface(TypeFaceHandler.sultanLight);
        txtChoosePrice.setTypeface(TypeFaceHandler.sultanLight);
        topTitle.setTypeface(TypeFaceHandler.sultanBold);
    }

    private void findViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        topTitle = (TextView) toolbar.findViewById(R.id.txtTitle);
        btnBack = (ImageButton) toolbar.findViewById(R.id.toolbarBackBtn);
        txtPrice1 = (TextView) findViewById(R.id.txtPrice1);
        txtPrice2 = (TextView) findViewById(R.id.txtPrice2);
        txtPrice3 = (TextView) findViewById(R.id.txtPrice3);
        txtPrice4 = (TextView) findViewById(R.id.txtPrice4);
        txtPrice5 = (TextView) findViewById(R.id.txtPrice5);
        txtChoosePrice = (TextView) findViewById(R.id.txtChoosePrice);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        initToolbar(toolbar);
        initBottomBar();
        DbHelper dbHelper = DbHelper.getInstance(this);
        subjects = dbHelper.getAllSubjects();
    }

    private void initBottomBar() {
        bottomBar.setDefaultTab(R.id.tab_shopping);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Intent intent = null;
                switch (tabId) {
                    case R.id.tab_home:
                        intent = new Intent(ShoppingActivity.this, HomeActivity.class);
                        break;
                    case R.id.tab_read_cards:
                        intent = new Intent(ShoppingActivity.this, CardActivity.class);
                        break;
                    case R.id.tab_select_cards:
                        intent = new Intent(ShoppingActivity.this, SelectCardsActivity.class);
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
        topTitle.setText(getResources().getString(R.string.button_shopping));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShoppingActivity.this, HomeActivity.class));
                finish();
            }
        });
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

    public void onClickBuyPrice(View view) {
        switch (view.getId()) {
            case R.id.imgPrice1:
                if (isQueryFinished && isAppExist)
                    buy(0);
                else if (!isAppExist)
                    Toast.makeText(this, "لطفا اپلیکیشن کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
                break;
            case R.id.imgPrice2:
                if (isQueryFinished && isAppExist)
                    buy(1);
                else if (!isAppExist)
                    Toast.makeText(this, "لطفا اپلیکیشن کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
                break;
            case R.id.imgPrice3:
                if (isQueryFinished && isAppExist)
                    buy(2);
                else if (!isAppExist)
                    Toast.makeText(this, "لطفا اپلیکیشن کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
                break;
            case R.id.imgPrice4:
                if (isQueryFinished && isAppExist)
                    buy(3);
                else if (!isAppExist)
                    Toast.makeText(this, "لطفا اپلیکیشن کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
                break;
            case R.id.imgPrice5:
                if (isQueryFinished && isAppExist)
                    buy(4);
                else if (!isAppExist)
                    Toast.makeText(this, "لطفا اپلیکیشن کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void buy(final int index) {
        if (!boughts.contains(index)) {
            new DialogHandler(this).showAlert(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showDialogDiscount(index);
                }
            }, getString(R.string.discount_question), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ShoppingActivity.this.index = index;
                    SKU_PREMIUM[0] = SKU_PREMIUMS[index];
                    mHelper.launchPurchaseFlow(ShoppingActivity.this, SKU_PREMIUM[0],
                            RC_REQUEST, mPurchaseFinishedListener, "payload-string");
                }
            });
        }else {
            Toast.makeText(this, getString(R.string.subject_bought), Toast.LENGTH_LONG).show();
        }
    }

    private void showDialogDiscount(final int index) {
        View view = this.getLayoutInflater().inflate(R.layout.dialog_forget_password, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ShoppingActivity.this, R.style.DialogTheme);
        builder.setCancelable(true);
        ((TextView) view.findViewById(R.id.txtEmail)).setTypeface(TypeFaceHandler.sultanBold);
        ((TextView) view.findViewById(R.id.txtEmail)).setText(getString(R.string.discount_code));
        final EditText etEmail = (EditText) view.findViewById(R.id.etEmail);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok_Button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (etEmail.getText().toString().length() > 0) {
                    String discount_code = etEmail.getText().toString();
                    sendDiscountRequest(discount_code, index);
                    dialogInterface.cancel();
                } else
                    Toast.makeText(ShoppingActivity.this, "تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final android.support.v7.app.AlertDialog dialog = builder.create();
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

    private void sendDiscountRequest(String discount_code, final int index) {
        HashMap<String, String> params = new HashMap<>();
        params.put("discount_code", discount_code);
        params.put("user", HomeActivity.student.getUserName());
        params.put(HomeActivity.student.getUserName(), new SharedPreferencesHandler(ShoppingActivity.this).getToken());
        new Connection(getString(R.string.base_url) + "discount.php", params, ConnectionUi.getDefault(ShoppingActivity.this)) {
            @Override
            protected void onResult(String result) {
                Log.i("result", result);
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getBoolean("success")){
                        buyDiscounted(index);
                    }else {
                        Toast.makeText(ShoppingActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("JSONException", e.getMessage());
                    Toast.makeText(ShoppingActivity.this, getString(R.string.failed_connection), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void buyDiscounted(int index) {
        ShoppingActivity.this.index = index;
        SKU_PREMIUM[0] = SKU_PREMIUMS_DISCOUNT[index];
        mHelper.launchPurchaseFlow(ShoppingActivity.this, SKU_PREMIUM[0],
                RC_REQUEST, mPurchaseFinishedListener, "payload-string");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
