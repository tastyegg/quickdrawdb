package com.quickdraw.database.quickdrawdb;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView balancebox;
    TextView namebox;
    ImageButton logoutButton;

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
        setContentView(R.layout.activity_main);

        balancebox = (TextView)findViewById(R.id.balancebox);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

        findViewById(R.id.wButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { withdraw(); }
        });

        findViewById(R.id.dButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { deposit(); }
        });

        findViewById(R.id.hButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(MainActivity.this, HistoryActivity.class)); }
        });

        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { logout(); }
        });


        namebox = (TextView)findViewById(R.id.namebox);
        currentUser = LoginActivity.getCurrentUser();
        String uppercaseUser = currentUser.toUpperCase();
        namebox.setText("WELCOME, \n" + uppercaseUser + " :^)");

        getBalance();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void logout() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void getBalance() {
        firebaseRef.child(currentUser).addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                balancebox = (TextView)findViewById(R.id.balancebox);
                User user = dataSnapshot.getValue(User.class);
                DecimalFormat df = new DecimalFormat("0.00");
                float balance = user.getBalance();
                String result;
                if (balance < 0) {
                    balance *= -1;
                    result = df.format(balance);
                    balancebox.setText("Balance: - $" + result);
                }
                else {
                    result = df.format(balance);
                    balancebox.setText("Balance: $" + result);
                }
                balancebox.setTextColor(Color.CYAN);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void withdraw() {
        firebaseRef.child(currentUser).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                float balance = user.getBalance();

                EditText amountInput = (EditText) findViewById(R.id.amountInput);
                String amount = amountInput.getText().toString();
                if (amount.equals("")) return;
                float fltamount = Float.parseFloat(amount);

                float newBalance = balance - fltamount;

                user.setBalance(newBalance);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
                String currentDate = dateFormat.format(date);
                user.addTime(currentDate);

                user.addTransaction(-fltamount);

                firebaseRef.child(user.getName()).setValue(user);
                amountInput.setText("");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
             }
        });
        getBalance();
    }

    public void deposit() {
        firebaseRef.child(currentUser).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                float balance = user.getBalance();

                EditText amountInput = (EditText) findViewById(R.id.amountInput);
                String amount = amountInput.getText().toString();
                if (amount.equals("")) return;
                float fltamount = Float.parseFloat(amount);

                float newBalance = balance + fltamount;

                user.setBalance(newBalance);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
                String currentDate = dateFormat.format(date);
                user.addTime(currentDate);

                user.addTransaction(fltamount);

                firebaseRef.child(user.getName()).setValue(user);
                amountInput.setText("");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        getBalance();
    }

}
