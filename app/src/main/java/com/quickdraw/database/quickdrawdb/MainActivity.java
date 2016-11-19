package com.quickdraw.database.quickdrawdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    RelativeLayout activity_main;
    TextView namebox;
    TextView balancebox;
    TextView chooseaction;

    ImageButton dButton;
    ImageButton wButton;
    ImageButton dText;
    ImageButton wText;
    ImageButton hButton;
    ImageButton sendButton;
    ImageButton logoutButton;

    EditText amountInput;

    boolean dSelected = false;
    boolean wSelected = false;
    boolean deposited = false;
    boolean withdrew = false;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASE_URL);

        currentUser = LoginActivity.getCurrentUser();

        activity_main = (RelativeLayout)findViewById(R.id.activity_main);

        namebox = (TextView)findViewById(R.id.namebox);
        balancebox = (TextView)findViewById(R.id.balancebox);
        chooseaction = (TextView)findViewById(R.id.chooseaction);

        dButton = (ImageButton)findViewById(R.id.dButton);
        wButton = (ImageButton)findViewById(R.id.wButton);
        dText = (ImageButton)findViewById(R.id.dText);
        wText = (ImageButton)findViewById(R.id.wText);
        hButton = (ImageButton)findViewById(R.id.hButton);
        sendButton = (ImageButton)findViewById(R.id.sendButton);
        logoutButton = (ImageButton)findViewById(R.id.logoutButton);

        amountInput = (EditText)findViewById(R.id.amountInput);

        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dSelected = true;
                deposited = false;
                chooseaction.setText("You have selected:");
                animateForward(dButton, dText,
                        0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f,
                        0, 244, 0 , 0,
                        0, -80, 0, -212);
            }
        });

        wButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wSelected = true;
                withdrew = false;
                chooseaction.setText("You have selected:");
                animateForward(wButton, wText,
                        0, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f,
                        0, -244, 0, 0,
                        0, 94, 0, -212);
            }
        });

        dText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dSelected = true;
                deposited = false;
                chooseaction.setText("You have selected:");
                animateForward(dButton, dText,
                        0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f,
                        0, 244, 0 , 0,
                        0, -80, 0, -212);
            }
        });

        wText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wSelected = true;
                withdrew = false;
                chooseaction.setText("You have selected:");
                animateForward(wButton, wText,
                        0, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f,
                        0, -244, 0, 0,
                        0, 94, 0, -212);
            }
        });

        hButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dSelected) deposit();
                else if (wSelected) withdraw();
            }
        });

        amountInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                amountInput.setHint("");
                return false;
            }
        });

        amountInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (deposited == false && withdrew == false) {
                        chooseaction.setText("Choose an action:");
                    }
//                    amountInput.setInputType(InputType.TYPE_NULL);
                    String amount = amountInput.getText().toString();
                    if (dSelected) {
                        animateBackward(dButton, dText,
                                0, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f,
                                244, 0, 0, 0,
                                -80, 0, -212, 0);
                        dSelected = false;
                    }
                    else if (wSelected) {
                        animateBackward(wButton, wText,
                                0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f,
                                -244, 0, 0, 0,
                                94, 0, -212, 0);
                        wSelected = false;
                    }
                }
                else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(amountInput, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        amountInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == keyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_ENTER) {
                        amountInput.setNextFocusDownId(R.id.amountInput);
                        if (dSelected) deposit();
                        else if (wSelected) withdraw();
                    }
                }
                return false;
            }
        });

        activity_main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                activity_main.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });

        showButtonTouch(dButton);
        showButtonTouch(wButton);
        showButtonTouch(dText);
        showButtonTouch(wText);
        showButtonTouch(hButton);
        showButtonTouch(sendButton);
        showButtonTouch(logoutButton);

        String uppercaseUser = currentUser.toUpperCase();
        namebox.setText("WELCOME, \n" + uppercaseUser + " :^)");

        amountInput.setVisibility(View.INVISIBLE);
        sendButton.setVisibility(View.INVISIBLE);

        getBalance();
    }

    public void showButtonTouch(ImageButton x) {
        x.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ImageButton image = (ImageButton) view;
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (view == sendButton) image.setAlpha(0.2f);
                    else image.setAlpha(0.5f);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (view == sendButton) image.setAlpha(0.5f);
                    else image.setAlpha(1.0f);
                }
                return false;
            }
        });
    }

    public void animateForward(ImageButton x, ImageButton xt,
                               float a, float b, int c, float d, int e, float f,
                               float xstart, float xend, float ystart, float yend,
                               float xstart2, float xend2, float ystart2, float yend2) {
        dButton.setAlpha(1.0f);
        wButton.setAlpha(1.0f);
        dText.setAlpha(1.0f);
        wText.setAlpha(1.0f);

        RotateAnimation rotate = new RotateAnimation(a, b, c, d, e, f);
        rotate.setDuration(800);
        rotate.setFillAfter(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(xstart, xend, ystart, yend);
        translateAnimation.setDuration(800);
        translateAnimation.setFillAfter(true);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(rotate);
        animationSet.addAnimation(translateAnimation);
        animationSet.setFillAfter(true);
        x.startAnimation(animationSet);
        x.setClickable(false);

        TranslateAnimation translateAnimation2 = new TranslateAnimation(xstart2, xend2, ystart2, yend2);
        translateAnimation2.setDuration(800);
        translateAnimation2.setFillAfter(true);
        xt.startAnimation(translateAnimation2);

        if (dSelected) {
            TranslateAnimation translateAnimation3 = new TranslateAnimation(0, 470, 0, 0);
            translateAnimation3.setDuration(800);
            translateAnimation3.setFillAfter(true);
            wButton.startAnimation(translateAnimation3);
            wButton.setClickable(false);

            TranslateAnimation translateAnimation4 = new TranslateAnimation(0, 470, 0, 0);
            translateAnimation4.setDuration(800);
            translateAnimation4.setFillAfter(true);
            wText.startAnimation(translateAnimation4);
        }

        else if (wSelected) {
            TranslateAnimation translateAnimation4 = new TranslateAnimation(0, -470, 0, 0);
            translateAnimation4.setDuration(800);
            translateAnimation4.setFillAfter(true);
            dButton.startAnimation(translateAnimation4);
            dButton.setClickable(false);

            TranslateAnimation translateAnimation5 = new TranslateAnimation(0, -470, 0, 0);
            translateAnimation5.setDuration(800);
            translateAnimation5.setFillAfter(true);
            dText.startAnimation(translateAnimation5);
        }

        dText.setClickable(false);
        wText.setClickable(false);

        Animation in = new AlphaAnimation(0, 1);
        in.setDuration(800);
        amountInput.startAnimation(in);
        Animation in2 = new AlphaAnimation(0, 0.5f);
        in2.setDuration(800);
        sendButton.startAnimation(in2);
        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                amountInput.setVisibility(View.VISIBLE);
                amountInput.requestFocus();
                sendButton.setVisibility(View.VISIBLE);
                sendButton.setAlpha(0.5f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animateBackward(ImageButton x, ImageButton xt,
                                float a, float b, int c, float d, int e, float f,
                                float xstart, float xend, float ystart, float yend,
                                float xstart2, float xend2, float ystart2, float yend2) {
        dButton.setAlpha(1.0f);
        wButton.setAlpha(1.0f);
        dText.setAlpha(1.0f);
        wText.setAlpha(1.0f);

        RotateAnimation rotate = new RotateAnimation(a, b, c, d, e, f);
        rotate.setDuration(800);
        rotate.setFillAfter(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(xstart, xend, ystart, yend);
        translateAnimation.setDuration(800);
        translateAnimation.setFillAfter(true);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(rotate);
        animationSet.addAnimation(translateAnimation);
        animationSet.setFillAfter(true);
        x.startAnimation(animationSet);
        x.setClickable(true);

        TranslateAnimation translateAnimation2 = new TranslateAnimation(xstart2, xend2, ystart2, yend2);
        translateAnimation2.setDuration(800);
        translateAnimation2.setFillAfter(true);
        xt.startAnimation(translateAnimation2);

        if (dSelected) {
            TranslateAnimation translateAnimation3 = new TranslateAnimation(470, 0, 0, 0);
            translateAnimation3.setDuration(800);
            translateAnimation3.setFillAfter(true);
            wButton.startAnimation(translateAnimation3);
            wButton.setClickable(true);

            TranslateAnimation translateAnimation4 = new TranslateAnimation(470, 0, 0, 0);
            translateAnimation4.setDuration(800);
            translateAnimation4.setFillAfter(true);
            wText.startAnimation(translateAnimation4);
        }

        else if (wSelected) {
            TranslateAnimation translateAnimation4 = new TranslateAnimation(-470, 0, 0, 0);
            translateAnimation4.setDuration(800);
            translateAnimation4.setFillAfter(true);
            dButton.startAnimation(translateAnimation4);
            dButton.setClickable(true);

            TranslateAnimation translateAnimation5 = new TranslateAnimation(-470, 0, 0, 0);
            translateAnimation5.setDuration(800);
            translateAnimation5.setFillAfter(true);
            dText.startAnimation(translateAnimation5);
        }

        dText.setClickable(true);
        wText.setClickable(true);

        Animation out = new AlphaAnimation(1, 0);
        out.setDuration(800);
        amountInput.startAnimation(out);
        Animation out2 = new AlphaAnimation(0.8f, 0);
        out2.setDuration(800);
        sendButton.startAnimation(out2);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                amountInput.setHint("Enter amount");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                amountInput.setText("");
                amountInput.setHint("Enter amount");
                amountInput.setVisibility(View.INVISIBLE);
                sendButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void getBalance() {
        firebaseRef.child(currentUser).addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
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

    public void deposit() {
        firebaseRef.child(currentUser).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                float balance = user.getBalance();

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

                deposited = true;
                DecimalFormat df = new DecimalFormat("0.00");
                String result = df.format(fltamount);
                chooseaction.setText("You deposited $" + result + " !");

                activity_main.requestFocus();
                View view = (View)activity_main;
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        getBalance();
    }

    public void withdraw() {
        firebaseRef.child(currentUser).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                float balance = user.getBalance();

                String amount = amountInput.getText().toString();
                if (amount.equals("")) return;
                float fltamount = Float.parseFloat(amount);

                float newBalance = balance - fltamount;

                if (newBalance < 0) {
                    amountInput.setText("");
                    chooseaction.setText("Error: Insufficient Funds");
                }

                else {
                    user.setBalance(newBalance);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date date = new Date();
                    String currentDate = dateFormat.format(date);
                    user.addTime(currentDate);

                    user.addTransaction(-fltamount);

                    firebaseRef.child(user.getName()).setValue(user);

                    withdrew = true;
                    DecimalFormat df = new DecimalFormat("0.00");
                    String result = df.format(fltamount);
                    chooseaction.setText("You withdrew $" + result + " !");

                    activity_main.requestFocus();
                    View view = (View)activity_main;
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        getBalance();
    }

    @Override
    public void onBackPressed() { startActivity(new Intent(MainActivity.this, LoginActivity.class)); }

    public void logout() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

}
