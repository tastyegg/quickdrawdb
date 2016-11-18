package com.quickdraw.database.quickdrawdb;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;

import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class HistoryActivity extends AppCompatActivity {

    TextView titlebox;
    TextView currentbalance;
    ImageButton logoutButton;

    TextView trans1;
    TextView trans2;
    TextView trans3;
    TextView trans4;
    TextView trans5;

    TextView when1;
    TextView when2;
    TextView when3;
    TextView when4;
    TextView when5;

    TextView[] trans = new TextView[5];
    TextView[] whens = new TextView[5];

    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private String currentUser;

    private static final String FIREBASE_URL = "https://quickdraw-db.firebaseio.com/";

    private Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(61, 61, 101));
        }
        setContentView(R.layout.activity_history);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

        currentUser = LoginActivity.getCurrentUser();

        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { logout(); }
        });

        titlebox = (TextView)findViewById(R.id.titlebox);
        currentbalance = (TextView)findViewById(R.id.currentbalance);

        trans1 = (TextView)findViewById(R.id.trans1);
        trans2 = (TextView)findViewById(R.id.trans2);
        trans3 = (TextView)findViewById(R.id.trans3);
        trans4 = (TextView)findViewById(R.id.trans4);
        trans5 = (TextView)findViewById(R.id.trans5);

        trans[0] = trans1;
        trans[1] = trans2;
        trans[2] = trans3;
        trans[3] = trans4;
        trans[4] = trans5;

//        trans1.setPaintFlags(trans1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        trans2.setPaintFlags(trans2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        trans3.setPaintFlags(trans3.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        trans4.setPaintFlags(trans4.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        trans5.setPaintFlags(trans5.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        when1 = (TextView)findViewById(R.id.when1);
        when2 = (TextView)findViewById(R.id.when2);
        when3 = (TextView)findViewById(R.id.when3);
        when4 = (TextView)findViewById(R.id.when4);
        when5 = (TextView)findViewById(R.id.when5);

        whens[0] = when1;
        whens[1] = when2;
        whens[2] = when3;
        whens[3] = when4;
        whens[4] = when5;

        firebaseRef.child(currentUser).addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                float balance = user.getBalance();
                DecimalFormat df = new DecimalFormat("0.00");
                String result;
                if (balance < 0) {
                    balance *= -1;
                    result = df.format(balance);
                    currentbalance.setText("Balance: - $" + result);
                }
                else {
                    result = df.format(balance);
                    currentbalance.setText("Balance: $" + result);
                }
                currentbalance.setTextColor(Color.CYAN);

                float[] transactions = user.getHistory();
                String[] timedates = user.getTimes();

                for (int i = 0; i < transactions.length; i++) {
                    if (transactions[i] == 0) {
                        if (i == 0) {
                            trans[i].setText("N/A\n");
                            whens[i].setText("N/A\n");
                            for (int n = 1; n < trans.length; n++) {
                                trans[n].setVisibility(View.INVISIBLE);
                                whens[n].setVisibility(View.INVISIBLE);
                            }
                            return;
                        }
                        else {
                            trans[i].setText("\n");
                            whens[i].setText("\n");
                        }
                    }
                    else {
                        if (transactions[i] < 0) {
                            transactions[i] *= -1;
                            result = df.format(transactions[i]);
                            trans[i].setText(" - $" + result + "\n");
                            trans[i].setTextColor(Color.RED);
                            whens[i].setText(timedates[i]);
                            whens[i].setTextColor(Color.RED);
                        }
                        else {
                            result = df.format(transactions[i]);
                            trans[i].setText("+ $" + result + "\n");
                            trans[i].setTextColor(Color.GREEN);
                            whens[i].setText(timedates[i]);
                            whens[i].setTextColor(Color.GREEN);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HistoryActivity.this, MainActivity.class));
    }

    public void logout() {
        startActivity(new Intent(HistoryActivity.this, LoginActivity.class));
    }

}
