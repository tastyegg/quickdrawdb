package com.quickdraw.database.quickdrawdb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    TextView textbox;
    TextView namebox;
    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;

    private static final String FIREBASE_URL = "https://quickdraw-db.firebaseio.com/";

    private Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textbox = (TextView)findViewById(R.id.textbox);
        textbox.setText("balance");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

        findViewById(R.id.enterButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { sendMessage(); }
        });

        findViewById(R.id.dataButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getData(); }
        });

        findViewById(R.id.wButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { withdraw(); }
        });

        findViewById(R.id.dButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { deposit(); }
        });
    }

    public void getData() {
        EditText nameInput2 = (EditText) findViewById(R.id.nameInput2);
        String name = nameInput2.getText().toString();
        if (!name.equals("")) {

            namebox = (TextView)findViewById(R.id.namebox);
            namebox.setText(name);

            firebaseRef.child(name).addValueEventListener(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    textbox = (TextView)findViewById(R.id.textbox);
                    User user = dataSnapshot.getValue(User.class);
                    textbox.setText(Float.toString(user.getBalance()));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            nameInput2.setText("");
        }
    }

    public void sendMessage() {
        EditText nameInput = (EditText) findViewById(R.id.nameInput);
        String name = nameInput.getText().toString();
        EditText balanceInput = (EditText) findViewById(R.id.balanceInput);
        String balanceStr = balanceInput.getText().toString();
        float balance = Float.parseFloat((balanceStr));
        User user = new User(name, balance);
        if (!name.equals("")) {
            firebaseRef.child(name).setValue(user);
            nameInput.setText("");
            balanceInput.setText("");
        }
    }

    public void withdraw() {
        EditText nameInput2 = (EditText) findViewById(R.id.nameInput2);
        String name = nameInput2.getText().toString();
        if (!name.equals("")) {

            namebox = (TextView) findViewById(R.id.namebox);
            namebox.setText(name);

            firebaseRef.child(name).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    float balance = user.getBalance();

                    EditText amountInput = (EditText) findViewById(R.id.amountInput);
                    String amount = amountInput.getText().toString();
                    float fltamount = Float.parseFloat(amount);

                    float newBalance = balance - fltamount;

                    user.setBalance(newBalance);

                    user.addTransaction(-fltamount);

                    firebaseRef.child(user.getName()).setValue(user);
                    amountInput.setText("");
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            nameInput2.setText("");
        }
    }

    public void deposit() {
        EditText nameInput2 = (EditText) findViewById(R.id.nameInput2);
        String name = nameInput2.getText().toString();
        if (!name.equals("")) {

            namebox = (TextView) findViewById(R.id.namebox);
            namebox.setText(name);

            firebaseRef.child(name).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    float balance = user.getBalance();

                    EditText amountInput = (EditText) findViewById(R.id.amountInput);
                    String amount = amountInput.getText().toString();
                    float fltamount = Float.parseFloat(amount);

                    float newBalance = balance + fltamount;

                    user.setBalance(newBalance);

                    user.addTransaction(fltamount);

                    firebaseRef.child(user.getName()).setValue(user);
                    amountInput.setText("");
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            nameInput2.setText("");
        }
    }

}
