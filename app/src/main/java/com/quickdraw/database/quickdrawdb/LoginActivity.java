package com.quickdraw.database.quickdrawdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.*;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static String currentUser;

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
        String name = "Anna";
        Random r = new Random();
        int balance = r.nextInt(1000);
        int pinint = r.nextInt(9999);
        String pin = String.valueOf(pinint);
        User user = new User(name, balance, pin);
        firebaseRef.child(name).setValue(user);

        name = "Bob";
        r = new Random();
        balance = r.nextInt(1000);
        pinint = r.nextInt(9999);
        pin = String.valueOf(pinint);
        user = new User(name, balance, pin);
        firebaseRef.child(name).setValue(user);

        name = "Chris";
        r = new Random();
        balance = r.nextInt(1000);
        pinint = r.nextInt(9999);
        pin = String.valueOf(pinint);
        user = new User(name, balance, pin);
        firebaseRef.child(name).setValue(user);

        name = "Dan";
        r = new Random();
        balance = r.nextInt(1000);
        pinint = r.nextInt(9999);
        pin = String.valueOf(pinint);
        user = new User(name, balance, pin);
        firebaseRef.child(name).setValue(user);

        name = "Eric";
        r = new Random();
        balance = r.nextInt(1000);
        pinint = r.nextInt(9999);
        pin = String.valueOf(pinint);
        user = new User(name, balance, pin);
        firebaseRef.child(name).setValue(user);
    }

    public void login() {
        check.setVisibility(View.INVISIBLE);
        check2.setVisibility(View.INVISIBLE);
        xmark.setVisibility(View.INVISIBLE);
        xmark2.setVisibility(View.INVISIBLE);
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = nameInput.getText().toString();
                if (!name.equals("")) {
                    if (dataSnapshot.hasChild(name)) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                check.setVisibility(View.VISIBLE);
                            }
                        }, 300);
                        firebaseRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
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

                                    currentUser = user.getName();

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

}
