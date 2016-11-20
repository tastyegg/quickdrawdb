package com.quickdraw.database.quickdrawdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static String currentUser;
    private static String currentUserName;

    EditText nameInput;
    EditText pinInput;

    ImageView check;
    ImageView check2;
    ImageView xmark;
    ImageView xmark2;

    Button dbCreate;
    Button loginButton;

    Handler handler;

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
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

        handler = new Handler();

        nameInput = (EditText)findViewById(R.id.nameInput);
        pinInput = (EditText)findViewById(R.id.pinInput);

        check = (ImageView)findViewById(R.id.check);
        check2 = (ImageView)findViewById(R.id.check2);
        xmark = (ImageView)findViewById(R.id.xmark);
        xmark2 = (ImageView)findViewById(R.id.xmark2);

        dbCreate = (Button)findViewById(R.id.dbCreate);
        loginButton = (Button)findViewById(R.id.loginButton);

//        pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinInput.setTypeface(Typeface.DEFAULT);
        pinInput.setTransformationMethod(new PasswordTransformationMethod());

        xmark.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                nameInput.setText("");
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { login(); }
        });

        dbCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createDB(); }
        });

        pinInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == keyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_ENTER) {
                        login();
                    }
                }
                return false;
            }
        });

        check.setVisibility(View.INVISIBLE);
        check2.setVisibility(View.INVISIBLE);
        xmark.setVisibility(View.INVISIBLE);
        xmark2.setVisibility(View.INVISIBLE);
    }

    public void createDB() {
        String[] names = {"Anna", "Bob", "Chris", "Daniel", "Eric", "Janet", "Kevin", "Molly", "Sally", "Terry"};
        for (int i = 0; i < names.length; i++) {
            createRandomUsers(names[i], i);
        }
    }

    public void createRandomUsers(String name, int pos) {
        String userID = "user00" + pos;

        Random r = new Random();
        float balance = r.nextFloat() * (100000 - 40000) + 40000;
        int pinint = r.nextInt(9999);
        if (pinint < 1000) {
            pinint += 1000;
        }

        DecimalFormat df = new DecimalFormat("0.00");
        String result = df.format(balance);
        balance = Float.parseFloat(result);

        String pin = String.valueOf(pinint);
        User user = new User(userID, name, balance, pin);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String currentDate = dateFormat.format(date);

        for (int i = 0; i < 5; i++) {
            float trans = r.nextFloat() * (10000 - 1000) + 1000;
            int neg = r.nextInt(100);
            if (neg % 2 == 0) {
                trans *= -1;
            }
            result = df.format(trans);
            trans = Float.parseFloat(result);
            user.addTransaction(trans);

            user.addTime(currentDate);
        }

        firebaseRef.child(userID).setValue(user);
    }

    public void login() {
        check.setVisibility(View.INVISIBLE);
        check2.setVisibility(View.INVISIBLE);
        xmark.setVisibility(View.INVISIBLE);
        xmark2.setVisibility(View.INVISIBLE);
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id = nameInput.getText().toString();
                if (!id.equals("")) {
                    if (dataSnapshot.hasChild(id)) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                check.setVisibility(View.VISIBLE);
                            }
                        }, 300);
                        firebaseRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String pin = pinInput.getText().toString();

                                User user = dataSnapshot.getValue(User.class);

                                if (pin.equals(user.getPin())) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            check2.setVisibility(View.VISIBLE);
                                        }
                                    }, 800);

                                    currentUser = user.getUserID();
                                    currentUserName = user.getName();

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }, 900);
                                }
                                else {
                                    pinInput.setText("");
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            xmark2.setVisibility(View.VISIBLE);
                                        }
                                    }, 800);
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                    else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                xmark.setVisibility(View.VISIBLE);
                            }
                        }, 300);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentUserName() {
        return currentUserName;
    }

}
