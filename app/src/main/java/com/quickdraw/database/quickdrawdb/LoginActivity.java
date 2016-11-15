package com.quickdraw.database.quickdrawdb;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.*;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static String currentUser;

    private static final String FIREBASE_URL = "https://quickdraw-db.firebaseio.com/";

    private Firebase firebaseRef;

    TextView statusbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { login(); }
        });

        findViewById(R.id.dbCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createDB(); }
        });
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
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                statusbox = (TextView)findViewById(R.id.statusbox);
                EditText nameInput = (EditText) findViewById(R.id.nameInput);
                String name = nameInput.getText().toString();
                if (dataSnapshot.hasChild(name)) {
                    firebaseRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            EditText pinInput = (EditText) findViewById(R.id.pinInput);
                            String pin = pinInput.getText().toString();

                            User user = dataSnapshot.getValue(User.class);
                            if (pin.equals(user.getPin())) {
                                statusbox.setText("Welcome " + user.getName() + " !");
                                currentUser = user.getName();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                pinInput.setText("");
                                statusbox.setText("Incorrect pin!");
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                else {
                    statusbox.setText("The user '" + name + "' does not exist");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

    public static String getCurrentUser() {
        return currentUser;
    }
}
